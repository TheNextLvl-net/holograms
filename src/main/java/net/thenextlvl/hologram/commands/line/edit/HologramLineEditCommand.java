package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramLineEditCommand extends BrigadierCommand {
    private HologramLineEditCommand(HologramPlugin plugin) {
        super(plugin, "edit", "holograms.command.line.edit");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditCommand(plugin);
        return command.create()
                .then(HologramLineEditAppendCommand.create(plugin))
                .then(HologramLineEditPrependCommand.create(plugin))
                .then(HologramLineEditReplaceCommand.create(plugin))
                .then(HologramLineEditSetCommand.create(plugin));
    }
}
