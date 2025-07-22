package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class HUDHandlerFurnace implements IComponentProvider, IServerDataProvider<BlockEntity> {

    static final HUDHandlerFurnace INSTANCE = new HUDHandlerFurnace();

    @Override
    public void appendBody(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (!config.get(PluginMinecraft.CONFIG_DISPLAY_FURNACE))
            return;

        if (!accessor.getBlockState().get(Properties.LIT))
            return;

        NbtList furnaceItems = accessor.getServerData().getList("furnace", 0x0A);
        DefaultedList<ItemStack> inventory = DefaultedList.of(3, ItemStack.EMPTY);
        for (int i = 0; i <furnaceItems.size(); i++)
            inventory.set(i, ItemStack.fromNbt(furnaceItems.getCompound(i)));

        NbtCompound progress = new NbtCompound();
        progress.putInt("progress", accessor.getServerData().getInt("progress"));
        progress.putInt("total", accessor.getServerData().getInt("total"));

        RenderableTextComponent renderables = new RenderableTextComponent(
                getRenderable(inventory.get(0)),
                getRenderable(inventory.get(1)),
                new RenderableTextComponent(PluginMinecraft.RENDER_FURNACE_PROGRESS, progress),
                getRenderable(inventory.get(2))
        );

        tooltip.add(renderables);
    }

    @Override
    public void appendServerData(NbtCompound data, ServerPlayerEntity player, World world, BlockEntity blockEntity) {
        FurnaceBlockEntity furnace = (FurnaceBlockEntity) blockEntity;
        NbtList items = new NbtList();
        items.add(furnace.getStack(0).writeNbt(new NbtCompound()));
        items.add(furnace.getStack(1).writeNbt(new NbtCompound()));
        items.add(furnace.getStack(2).writeNbt(new NbtCompound()));
        data.put("furnace", items);
        NbtCompound furnaceTag = furnace.writeNbt(new NbtCompound());
        data.putInt("progress", furnaceTag.getInt("CookTime")); // smh
        data.putInt("total", furnaceTag.getInt("CookTimeTotal")); // smh
    }

    private static RenderableTextComponent getRenderable(ItemStack stack) {
        if (!stack.isEmpty()) {
            NbtCompound tag = new NbtCompound();
            tag.putString("id", Registry.ITEM.getKey(stack.getItem()).toString());
            tag.putInt("count", stack.getSize());
            if (stack.hasNbt())
                tag.putString("nbt", stack.getNbt().toString());
            return new RenderableTextComponent(PluginMinecraft.RENDER_ITEM, tag);
        } else {
            NbtCompound spacerTag = new NbtCompound();
            spacerTag.putInt("width", 18);
            return new RenderableTextComponent(PluginMinecraft.RENDER_SPACER, spacerTag);
        }
    }
}
