package mcp.mobius.waila.overlay.tooltiprenderers;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.SnbtParser;
import net.minecraft.resource.Identifier;
import net.minecraft.util.registry.Registry;

import java.awt.Dimension;

public class TooltipRendererStack implements ITooltipRenderer {

    @Override
    public Dimension getSize(NbtCompound tag, ICommonAccessor accessor) {
        return new Dimension(18, 18);
    }

    @Override
    public void draw(NbtCompound tag, ICommonAccessor accessor, int x, int y) {
        int count = tag.getInt("count");
        if (count <= 0)
            return;

        Item item = Registry.ITEM.getOrDefault(new Identifier(tag.getString("id")));
        if (item == Items.AIR)
            return;

        NbtCompound stackTag = null;
        try {
            stackTag = SnbtParser.parse(tag.getString("nbt"));
        } catch (CommandSyntaxException e) {
            // No-op
        }

        ItemStack stack = new ItemStack(item, count);
        if (stackTag != null)
            stack.setNbt(stackTag);

        Lighting.turnOnGui();
        DisplayUtil.renderStack(x, y, stack);
        Lighting.turnOff();
    }

}
