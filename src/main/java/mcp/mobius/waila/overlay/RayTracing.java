package mcp.mobius.waila.overlay;

import com.google.common.collect.Lists;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.unmapped.C_9836145;
import net.minecraft.unmapped.C_0190618;
import net.minecraft.world.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.unmapped.C_8075157;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RayTracing {

    public static final RayTracing INSTANCE = new RayTracing();
    private HitResult target = null;
    private Minecraft mc = Minecraft.getInstance();

    private RayTracing() {
    }

    public void fire() {
        if (mc.crosshairTarget != null && mc.crosshairTarget.m_5790247() == HitResult.Type.ENTITY) {
            this.target = mc.crosshairTarget;
            return;
        }

        Entity viewpoint = mc.getCamera();
        if (viewpoint == null)
            return;

        this.target = this.rayTrace(viewpoint, mc.interactionManager.getReach(), 0);
    }

    public HitResult getTarget() {
        return this.target;
    }

    public ItemStack getTargetStack() {
        return target != null && target.m_5790247() == HitResult.Type.BLOCK ? getIdentifierStack() : ItemStack.EMPTY;
    }

    public Entity getTargetEntity() {
        return target.m_5790247() == HitResult.Type.ENTITY ? getIdentifierEntity() : null;
    }

    public HitResult rayTrace(Entity entity, double playerReach, float partialTicks) {
        Vec3d eyePosition = entity.getEyePosition(partialTicks);
        Vec3d lookVector = entity.getRotationVec(partialTicks);
        Vec3d traceEnd = eyePosition.add(lookVector.x * playerReach, lookVector.y * playerReach, lookVector.z * playerReach);

        C_8075157.C_2235948 fluidView = Waila.CONFIG.get().getGeneral().shouldDisplayFluids() ? C_8075157.C_2235948.SOURCE_ONLY : C_8075157.C_2235948.NONE;
        C_8075157 context = new C_8075157(eyePosition, traceEnd, C_8075157.C_9353126.OUTLINE, fluidView, entity);
        return entity.getSourceWorld().m_0211792(context);
    }

    public ItemStack getIdentifierStack() {
        List<ItemStack> items = this.getIdentifierItems();

        if (items.isEmpty())
            return ItemStack.EMPTY;

        return items.get(0);
    }

    public Entity getIdentifierEntity() {
        if (this.target == null || this.target.m_5790247() != HitResult.Type.ENTITY)
            return null;

        List<Entity> entities = Lists.newArrayList();

        Entity entity = ((C_0190618) this.target).m_5279979();
        if (WailaRegistrar.INSTANCE.hasOverrideEntityProviders(entity)) {
            Collection<List<IEntityComponentProvider>> overrideProviders = WailaRegistrar.INSTANCE.getOverrideEntityProviders(entity).values();
            for (List<IEntityComponentProvider> providers : overrideProviders)
                for (IEntityComponentProvider provider : providers)
                    entities.add(provider.getOverride(DataAccessor.INSTANCE, PluginConfig.INSTANCE));
        }

        return entities.size() > 0 ? entities.get(0) : entity;
    }

    public List<ItemStack> getIdentifierItems() {
        List<ItemStack> items = Lists.newArrayList();

        if (this.target == null)
            return items;

        switch (this.target.m_5790247()) {
            case ENTITY: {
                if (WailaRegistrar.INSTANCE.hasStackEntityProviders(((C_0190618) target).m_5279979())) {
                    Collection<List<IEntityComponentProvider>> providers = WailaRegistrar.INSTANCE.getStackEntityProviders(((C_0190618) target).m_5279979()).values();
                    for (List<IEntityComponentProvider> providersList : providers) {
                        for (IEntityComponentProvider provider : providersList) {
                            ItemStack providerStack = provider.getDisplayItem(DataAccessor.INSTANCE, PluginConfig.INSTANCE);
                            if (providerStack.isEmpty())
                                continue;

                            items.add(providerStack);
                        }
                    }
                }
                break;
            }
            case BLOCK: {
                World world = mc.world;
                BlockPos pos = ((C_9836145) target).m_6754399();
                BlockState state = world.m_2431061(pos);
                if (state.isAir())
                    return items;

                BlockEntity tile = world.getBlockEntity(pos);

                if (WailaRegistrar.INSTANCE.hasStackProviders(state.getBlock()))
                    handleStackProviders(items, WailaRegistrar.INSTANCE.getStackProviders(state.getBlock()).values());

                if (tile != null && WailaRegistrar.INSTANCE.hasStackProviders(tile))
                    handleStackProviders(items, WailaRegistrar.INSTANCE.getStackProviders(tile).values());

                if (!items.isEmpty())
                    return items;

                ItemStack pick = state.getBlock().getPickItem(world, pos, state);
                if (!pick.isEmpty())
                    return Collections.singletonList(pick);

                if (items.isEmpty() && state.getBlock().asItem() != Items.AIR)
                    items.add(new ItemStack(state.getBlock()));

                break;
            }
        }

        return items;
    }

    private void handleStackProviders(List<ItemStack> items, Collection<List<IComponentProvider>> providers) {
        for (List<IComponentProvider> providersList : providers) {
            for (IComponentProvider provider : providersList) {
                ItemStack providerStack = provider.getStack(DataAccessor.INSTANCE, PluginConfig.INSTANCE);
                if (providerStack.isEmpty())
                    continue;

                items.add(providerStack);
            }
        }
    }
}
