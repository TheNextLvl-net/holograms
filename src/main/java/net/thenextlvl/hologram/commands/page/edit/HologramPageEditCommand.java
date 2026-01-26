package net.thenextlvl.hologram.commands.page.edit;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.suggestions.LineSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
public final class HologramPageEditCommand extends BrigadierCommand {
    private HologramPageEditCommand(final HologramPlugin plugin) {
        super(plugin, "edit", "holograms.command.page.edit");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageEditCommand(plugin);
        final var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.INSTANCE);
        return command.create().then(hologramArgument(plugin).then(line
                .then(HologramPageEditIntervalCommand.create(plugin))
                .then(HologramPageEditPauseCommand.create(plugin))
                .then(HologramPageEditRandomCommand.create(plugin))
                .then(HologramPageEditSetCommand.create(plugin))));
    }
}
