package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.state.property.ComparatorMode;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.state.property.Properties;
import net.minecraft.resource.Identifier;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class HUDHandlerVanilla implements IComponentProvider, IServerDataProvider<BlockEntity> {

    static final HUDHandlerVanilla INSTANCE = new HUDHandlerVanilla();

    static final Identifier OBJECT_NAME_TAG = new Identifier(Waila.MODID, "object_name");

    @Override
    public ItemStack getStack(IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginMinecraft.CONFIG_HIDE_SILVERFISH) && accessor.getBlock() instanceof InfestedBlock)
            return new ItemStack(((InfestedBlock) accessor.getBlock()).getHost().asItem());

        if (accessor.getBlock() == Blocks.WHEAT)
            return new ItemStack(Items.WHEAT);

        if (accessor.getBlock() == Blocks.BEETROOTS)
            return new ItemStack(Items.BEETROOT);

		if (accessor.getBlockEntity() instanceof SkullBlockEntity) {
			SkullBlockEntity skull = (SkullBlockEntity) accessor.getBlockEntity();
			if (skull.getProfile() != null) {
				ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
				NbtCompound tag = new NbtCompound();
				tag.put("SkullOwner", NbtUtils.writeProfile(new NbtCompound(), skull.getProfile()));
				stack.setNbt(tag);
				return stack;
			}
		}

        return ItemStack.EMPTY;
    }

    @Override
    public void appendHead(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
		if (config.get(PluginMinecraft.CONFIG_HIDE_SILVERFISH) && accessor.getBlock() instanceof InfestedBlock)
			((ITaggableList<Identifier, Text>) tooltip).setTag(OBJECT_NAME_TAG, new LiteralText(String.format(Waila.CONFIG.get().getFormatting().getBlockName(), accessor.getStack().getDisplayName().getFormattedString())));

		if (accessor.getBlock() == Blocks.MOB_SPAWNER && config.get(PluginMinecraft.CONFIG_SPAWNER_TYPE)) {
            MobSpawnerBlockEntity spawner = (MobSpawnerBlockEntity) accessor.getBlockEntity();
            ((ITaggableList<Identifier, Text>) tooltip).setTag(OBJECT_NAME_TAG, new TranslatableText(accessor.getBlock().getTranslationKey())
                    .append(new LiteralText(" ("))
                    .append(spawner.getSpawner().getDisplayEntity().getDisplayName())
                    .append(new LiteralText(")"))
            );
        }
    }

    @Override
    public void appendBody(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginMinecraft.CONFIG_CROP_PROGRESS)) {
            if (accessor.getBlock() instanceof WheatBlock) {
				WheatBlock crop = (WheatBlock) accessor.getBlock();
                addMaturityTooltip(tooltip, accessor.getBlockState().get(crop.getAgeProperty()) / (float) crop.getMaxAge());
            } else if (accessor.getBlock() == Blocks.MELON_STEM || accessor.getBlock() == Blocks.PUMPKIN_STEM) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(Properties.AGE_7) / 7F);
            } else if (accessor.getBlock() == Blocks.COCOA) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(Properties.AGE_2) / 2.0F);
            } else if (accessor.getBlock() == Blocks.SWEET_BERRY_BUSH) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(Properties.AGE_3) / 3.0F);
			} else if (accessor.getBlock() == Blocks.NETHER_WART) {
				addMaturityTooltip(tooltip, accessor.getBlockState().get(Properties.AGE_3) / 3.0F);
			}
        }

        if (config.get(PluginMinecraft.CONFIG_LEVER) && accessor.getBlock() instanceof LeverBlock) {
            boolean active = accessor.getBlockState().get(Properties.POWERED);
            tooltip.add(new TranslatableText("tooltip.waila.state", new TranslatableText("tooltip.waila.state_" + (active ? "on" : "off"))));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_REPEATER) && accessor.getBlock() == Blocks.REPEATER) {
            int delay = accessor.getBlockState().get(Properties.DELAY);
            tooltip.add(new TranslatableText("tooltip.waila.delay", delay));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_COMPARATOR) && accessor.getBlock() == Blocks.COMPARATOR) {
            ComparatorMode mode = accessor.getBlockState().get(Properties.COMPARATOR_MODE);
            tooltip.add(new TranslatableText("tooltip.waila.mode", new TranslatableText("tooltip.waila.mode_" + (mode == ComparatorMode.COMPARE ? "comparator" : "subtractor"))));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_REDSTONE) && accessor.getBlock() == Blocks.REDSTONE_WIRE) {
            tooltip.add(new TranslatableText("tooltip.waila.power", accessor.getBlockState().get(Properties.POWER)));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_JUKEBOX) && accessor.getBlock() == Blocks.JUKEBOX) {
            if (accessor.getServerData().contains("record"))
                tooltip.add(new TranslatableText("record.nowPlaying", Text.Serializer.fromJson(accessor.getServerData().getString("record"))));
            else
                tooltip.add(new TranslatableText("tooltip.waila.empty"));
        }

		if (config.get(PluginMinecraft.CONFIG_PLAYER_HEAD_NAME) && accessor.getBlockEntity() instanceof SkullBlockEntity) {
			SkullBlockEntity skull = (SkullBlockEntity) accessor.getBlockEntity();
			if (skull.getProfile() != null && !StringUtils.isBlank(skull.getProfile().getName()))
				tooltip.add(new LiteralText(skull.getProfile().getName()));
		}
    }

    @Override
    public void appendServerData(NbtCompound data, ServerPlayerEntity player, World world, BlockEntity blockEntity) {
        if (blockEntity instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity jukebox = (JukeboxBlockEntity) blockEntity;
            data.putString("record", Text.Serializer.toJson(jukebox.getRecord().getDisplayName()));
        }
    }

    private static void addMaturityTooltip(List<Text> tooltip, float growthValue) {
        growthValue *= 100.0F;
        if (growthValue < 100.0F)
            tooltip.add(new TranslatableText("tooltip.waila.crop_growth", String.format("%.0f%%", growthValue)));
        else
            tooltip.add(new TranslatableText("tooltip.waila.crop_growth", new TranslatableText("tooltip.waila.crop_mature")));
    }
}
