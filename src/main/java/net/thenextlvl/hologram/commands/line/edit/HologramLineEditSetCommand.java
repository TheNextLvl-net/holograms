package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditSetCommand extends BrigadierCommand {
    private HologramLineEditSetCommand(HologramPlugin plugin) {
        super(plugin, "set", "holograms.command.line.edit.set");
    }

    // todo: hologram line edit <hologram> <index> set <text>
    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditSetCommand(plugin);
        return command.create();
    }
}
