package net.thenextlvl.hologram.commands.page.edit;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.edit.EditCommands;
import net.thenextlvl.hologram.commands.edit.Resolvers;
import net.thenextlvl.hologram.commands.suggestions.LineSuggestionProvider;
import net.thenextlvl.hologram.commands.suggestions.PageSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
public final class HologramPageEditContentCommand extends BrigadierCommand {
    private HologramPageEditContentCommand(final HologramPlugin plugin) {
        super(plugin, "edit", "holograms.command.page.edit");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageEditContentCommand(plugin);
        final var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.INSTANCE);
        final var page = Commands.argument("page", IntegerArgumentType.integer(1))
                .suggests(PageSuggestionProvider.INSTANCE);

        final var pageBuilder = EditCommands.addAllEditCommands(page, plugin, Resolvers.PAGE);

        return command.create().then(hologramArgument(plugin).then(line.then(pageBuilder)));
    }
}
