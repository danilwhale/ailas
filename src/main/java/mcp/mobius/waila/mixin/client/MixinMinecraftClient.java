package mcp.mobius.waila.mixin.client;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.overlay.WailaTickHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/unmapped/C_8039257;m_8238460(Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.AFTER))
    private void clientTick(CallbackInfo callbackInfo) {
        WailaTickHandler.INSTANCE.tickClient();
    }

    @Inject(method = "handleKeyBindings", at = @At("TAIL"))
    private void handleKeybinds(CallbackInfo callbackInfo) {
        WailaClient.handleKeybinds();
    }
}
