package net.thenextlvl.hologram.commands.page.edit;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.suggestions.PageSuggestionProvider;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramPageEditCommand extends BrigadierCommand {
    private HologramPageEditCommand(final HologramPlugin plugin) {
        super(plugin, "edit", "holograms.command.page.edit");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageEditCommand(plugin);
        final var page = Commands.argument("page", IntegerArgumentType.integer(1))
                .suggests(PageSuggestionProvider.INSTANCE);
        return command.create().then(page
                .then(HologramPageEditIntervalCommand.create(plugin))
                .then(HologramPageEditPauseCommand.create(plugin))
                .then(HologramPageEditRandomCommand.create(plugin))
                .then(HologramPageEditSetCommand.create(plugin)));
    }
}
