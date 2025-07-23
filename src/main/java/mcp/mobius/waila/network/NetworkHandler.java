package mcp.mobius.waila.network;

import io.netty.buffer.Unpooled;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.ConfigEntry;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.resource.Identifier;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;

import java.util.Set;

public class NetworkHandler {

    public static final Identifier REQUEST_ENTITY = new Identifier(Waila.MODID, "request_entity");
    public static final Identifier REQUEST_TILE = new Identifier(Waila.MODID, "request_tile");

    public static void init() {
		ServerPlayNetworking.registerListenerAsync(REQUEST_ENTITY, (server, handler, player, data) -> {
			World world = player.world;
			Entity entity = world.getEntity(data.readInt());
			server.execute(() -> {
				if (entity == null)
					return;

				NbtCompound tag = new NbtCompound();
				if (WailaRegistrar.INSTANCE.hasNBTEntityProviders(entity)) {
					WailaRegistrar.INSTANCE.getNBTEntityProviders(entity).values().forEach(l -> l.forEach(p -> p.appendServerData(tag, (ServerPlayerEntity) player, world, (LivingEntity) entity)));
				} else {
					entity.writeEntityNbt(tag);
				}

				tag.putInt("WailaEntityID", entity.getNetworkId());

				PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
				buf.writeNbtCompound(tag);
				((ServerPlayerEntity) player).networkHandler.sendPacket(new CustomPayloadS2CPacket(ClientNetworkHandler.RECEIVE_DATA, buf));
			});
			return true;
		});
		ServerPlayNetworking.registerListenerAsync(REQUEST_TILE, (server, handler, player, data) -> {
			World world = player.world;
			BlockPos pos = data.readBlockPos();

			server.execute(() -> {
				if (!world.isChunkLoaded(pos))
					return;

				BlockEntity tile = world.getBlockEntity(pos);
				if (tile == null)
					return;

				BlockState state = world.m_2431061(pos);

				NbtCompound tag = new NbtCompound();
				if (WailaRegistrar.INSTANCE.hasNBTProviders(tile) || WailaRegistrar.INSTANCE.hasNBTProviders(state.getBlock())) {
					WailaRegistrar.INSTANCE.getNBTProviders(tile).values().forEach(l -> l.forEach(p -> p.appendServerData(tag, (ServerPlayerEntity) player, world, tile)));
					WailaRegistrar.INSTANCE.getNBTProviders(state.getBlock()).values().forEach(l -> l.forEach(p -> p.appendServerData(tag, (ServerPlayerEntity) player, world, tile)));
				} else {
					tile.writeNbt(tag);
				}

				tag.putInt("x", pos.getX());
				tag.putInt("y", pos.getY());
				tag.putInt("z", pos.getZ());
				tag.putString("id", Registry.BLOCK_ENTITY_TYPE.getKey(tile.getType()).toString());

				PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
				buf.writeNbtCompound(tag);
				((ServerPlayerEntity) player).networkHandler.sendPacket(new CustomPayloadS2CPacket(ClientNetworkHandler.RECEIVE_DATA, buf));
			});
			return true;
		});
    }

    @Environment(EnvType.CLIENT)
    public static void requestEntity(Entity entity) {
        if (Minecraft.getInstance().getNetworkHandler() == null)
            return;

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getNetworkId());
        Minecraft.getInstance().getNetworkHandler().m_0632702().send(new CustomPayloadC2SPacket(REQUEST_ENTITY, buf));
    }

    @Environment(EnvType.CLIENT)
    public static void requestTile(BlockEntity blockEntity) {
        if (Minecraft.getInstance().getNetworkHandler() == null)
            return;

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(blockEntity.getPos());
        Minecraft.getInstance().getNetworkHandler().m_0632702().send(new CustomPayloadC2SPacket(REQUEST_TILE, buf));
    }

    @Environment(EnvType.SERVER)
    public static void sendConfig(PluginConfig config, ServerPlayerEntity player) {
        Waila.LOGGER.info("Sending config to {} ({})", player.getGameProfile().getName(), player.getGameProfile().getId());
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        Set<ConfigEntry> entries = config.getSyncableConfigs();
        buf.writeInt(entries.size());
        entries.forEach(e -> {
            buf.writeInt(e.getId().toString().length());
            buf.writeString(e.getId().toString());
            buf.writeBoolean(e.getValue());
        });

        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(ClientNetworkHandler.GET_CONFIG, buf));
    }
}
