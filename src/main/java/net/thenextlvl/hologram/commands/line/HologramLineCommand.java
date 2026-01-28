package net.thenextlvl.hologram.commands.line;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramLineCommand extends BrigadierCommand {
    private HologramLineCommand(final HologramPlugin plugin) {
        super(plugin, "line", "holograms.command.line");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineCommand(plugin);
        return command.create()
                .then(HologramLineAddCommand.create(plugin))
                .then(HologramLineEditCommand.create(plugin))
                .then(HologramLineInsertCommand.create(plugin))
                .then(HologramLineMoveCommand.create(plugin))
                .then(HologramLineRemoveCommand.create(plugin))
                .then(HologramLineSwapCommand.create(plugin));
    }
}
