package mcp.mobius.waila.command;

import com.mojang.brigadier.CommandDispatcher;
import mcp.mobius.waila.api.impl.DumpGenerator;
import net.minecraft.server.command.handler.CommandManager;
import net.minecraft.server.command.source.CommandSourceStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CommandDumpHandlers {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(CommandManager.literal("dumpHandlers")
                .requires(source -> source.hasPermissions(2))
                .executes(context -> {
                    File file = new File("waila_handlers.md");
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(DumpGenerator.generateInfoDump());
                        context.getSource().sendSuccess(new TranslatableText("command.waila.dump_success"), false);
                        return 1;
                    } catch (IOException e) {
                        context.getSource().sendFailure(new LiteralText(e.getClass().getSimpleName() + ": " + e.getMessage()));
                        return 0;
                    }
                })
        );
    }
}
