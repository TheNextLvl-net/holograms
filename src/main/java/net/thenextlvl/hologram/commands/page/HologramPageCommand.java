package net.thenextlvl.hologram.commands.page;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.page.edit.HologramPageEditCommand;
import net.thenextlvl.hologram.commands.suggestions.LineSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
public final class HologramPageCommand extends BrigadierCommand {
    private HologramPageCommand(final HologramPlugin plugin) {
        super(plugin, "page", "holograms.command.page");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageCommand(plugin);
        final var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.INSTANCE);
        return command.create().then(hologramArgument(plugin).then(line
                .then(HologramPageAddCommand.create(plugin))
                .then(HologramPageClearCommand.create(plugin))
                .then(HologramPageEditCommand.create(plugin))
                .then(HologramPageListCommand.create(plugin))
                .then(HologramPageRemoveCommand.create(plugin))));
    }
}
