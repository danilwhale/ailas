package mcp.mobius.waila.mixin.server;

import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.CommandDispatcher;
import mcp.mobius.waila.command.CommandDumpHandlers;
import net.minecraft.server.command.handler.CommandManager;
import net.minecraft.server.command.source.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandManager.class)
public abstract class MixinCommandManager {
	@Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;findAmbiguities(Lcom/mojang/brigadier/AmbiguityConsumer;)V"), method = "<init>")
	private void ailas$addCommand(CommandDispatcher<CommandSourceStack> instance, AmbiguityConsumer<CommandSourceStack> consumer) {
		CommandDumpHandlers.register(instance);
		instance.findAmbiguities(consumer);
	}
}
