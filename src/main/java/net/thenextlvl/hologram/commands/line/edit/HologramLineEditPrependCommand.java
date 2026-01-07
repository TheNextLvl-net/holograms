package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditPrependCommand extends BrigadierCommand {
    private HologramLineEditPrependCommand(HologramPlugin plugin) {
        super(plugin, "prepend", "holograms.command.line.edit.prepend");
    }

    // todo: hologram line edit <hologram> <index> prepend <text>
    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditPrependCommand(plugin);
        return command.create();
    }
}
