package net.thenextlvl.hologram.commands.action.argument;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ActionTypes;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class RunConsoleCommand extends HologramStringActionCommand {
    private RunConsoleCommand(final HologramPlugin plugin) {
        super(plugin, ActionTypes.types().runConsoleCommand(), "run-console-command", "command");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        return new RunConsoleCommand(plugin).create();
    }
}
