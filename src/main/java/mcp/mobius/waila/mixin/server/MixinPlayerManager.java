package mcp.mobius.waila.mixin.server;

import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.network.NetworkHandler;
import net.minecraft.network.Connection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    @Inject(method = "onLogin", at = @At("TAIL"))
    private void onPlayerConnect(Connection clientConnection, ServerPlayerEntity player, CallbackInfo callbackInfo) {
        NetworkHandler.sendConfig(PluginConfig.INSTANCE, player);
    }
}
