package net.thenextlvl.hologram.commands.line;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.HologramCommand;
import net.thenextlvl.hologram.commands.action.ActionTargetResolver;
import net.thenextlvl.hologram.commands.action.HologramActionCommand;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.suggestions.LineSuggestionProvider;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramLineActionCommand extends BrigadierCommand {
    private HologramLineActionCommand(final HologramPlugin plugin) {
        super(plugin, "action", "holograms.command.line.action");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineActionCommand(plugin);
        final var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.ANY_LINE);
        return command.create().then(HologramCommand.hologramArgument(plugin)
                .then(HologramActionCommand.create(plugin, line, ActionTargetResolver.LINE)));
    }
}
