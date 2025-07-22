package mcp.mobius.waila.addons.core;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererHealth;
import net.minecraft.block.Block;
import net.minecraft.block.LiquidBlock;
import net.minecraft.entity.decoration.DecorationEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.resource.Identifier;

public class PluginCore implements IWailaPlugin {

    static final Identifier RENDER_ENTITY_HEALTH = new Identifier(Waila.MODID, "render_health");

    static final Identifier CONFIG_SHOW_REGISTRY = new Identifier(Waila.MODID, "show_registry");
    public static final Identifier CONFIG_SHOW_ENTITY = new Identifier(Waila.MODID, "show_entities");
    static final Identifier CONFIG_SHOW_ENTITY_HEALTH = new Identifier(Waila.MODID, "show_entity_hp");
    static final Identifier CONFIG_SHOW_STATES = new Identifier(Waila.MODID, "show_states");

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(HUDHandlerBlocks.INSTANCE, TooltipPosition.HEAD, Block.class);
        registrar.registerComponentProvider(HUDHandlerBlocks.INSTANCE, TooltipPosition.BODY, Block.class);
        registrar.registerComponentProvider(HUDHandlerBlocks.INSTANCE, TooltipPosition.TAIL, Block.class);

        registrar.registerStackProvider(HUDHandlerFluids.INSTANCE, LiquidBlock.class);
        registrar.registerComponentProvider(HUDHandlerFluids.INSTANCE, TooltipPosition.HEAD, LiquidBlock.class);

        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.HEAD, LivingEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.HEAD, DecorationEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.HEAD, MinecartEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.BODY, LivingEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.TAIL, LivingEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.TAIL, DecorationEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.TAIL, MinecartEntity.class);

        registrar.addConfig(CONFIG_SHOW_REGISTRY, false);
        registrar.addConfig(CONFIG_SHOW_ENTITY, true);
        registrar.addConfig(CONFIG_SHOW_ENTITY_HEALTH, true);
        registrar.addConfig(CONFIG_SHOW_STATES, false);

        registrar.registerTooltipRenderer(RENDER_ENTITY_HEALTH, new TooltipRendererHealth());
    }
}
