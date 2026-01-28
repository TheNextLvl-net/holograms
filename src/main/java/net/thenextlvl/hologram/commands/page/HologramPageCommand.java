package net.thenextlvl.hologram.commands.page;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramPageCommand extends BrigadierCommand {
    private HologramPageCommand(final HologramPlugin plugin) {
        super(plugin, "page", "holograms.command.page");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageCommand(plugin);
        return command.create()
                .then(HologramPageAddCommand.create(plugin))
                .then(HologramPageClearCommand.create(plugin))
                .then(HologramPageEditCommand.create(plugin))
                .then(HologramPageInsertCommand.create(plugin))
                .then(HologramPageListCommand.create(plugin))
                .then(HologramPageMoveCommand.create(plugin))
                .then(HologramPageRemoveCommand.create(plugin))
                .then(HologramPageSettingsCommand.create(plugin))
                .then(HologramPageSwapCommand.create(plugin));
    }
}
