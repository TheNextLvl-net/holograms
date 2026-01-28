package net.thenextlvl.hologram.commands.line;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.edit.EditCommands;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver;
import net.thenextlvl.hologram.commands.suggestions.LineSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
public final class HologramLineEditCommand extends BrigadierCommand {
    private HologramLineEditCommand(final HologramPlugin plugin) {
        super(plugin, "edit", "holograms.command.line.edit");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineEditCommand(plugin);
        final var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.ANY_LINE);
        return command.create().then(hologramArgument(plugin)
                .then(EditCommands.addAllEditCommands(line, plugin, LineTargetResolver.LINE)));
    }
}
