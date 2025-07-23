package mcp.mobius.waila.network;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.Identifier;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;

import java.util.Map;

public class ClientNetworkHandler {

    public static final Identifier RECEIVE_DATA = new Identifier(Waila.MODID, "receive_data");
    public static final Identifier GET_CONFIG = new Identifier(Waila.MODID, "send_config");

    public static void init() {
        ClientPlayNetworking.registerListenerAsync(ClientNetworkHandler.RECEIVE_DATA, (minecraft, handler, data) -> {
			NbtCompound tag = data.readNbtCompound();
			minecraft.execute(() -> DataAccessor.INSTANCE.setServerData(tag));
			return true;
		});

		ClientPlayNetworking.registerListenerAsync(ClientNetworkHandler.GET_CONFIG, (minecraft, handler, data) -> {
			int size = data.readInt();
			Map<Identifier, Boolean> temp = Maps.newHashMap();
			for (int i = 0; i < size; i++) {
				int idLength = data.readInt();
				Identifier id = new Identifier(data.readString(idLength));
				boolean value = data.readBoolean();
				temp.put(id, value);
			}

			minecraft.execute(() -> {
				temp.forEach(PluginConfig.INSTANCE::set);
				Waila.LOGGER.info("Received config from the server: {}", new Gson().toJson(temp));
			});
			return true;
		});
    }
}
