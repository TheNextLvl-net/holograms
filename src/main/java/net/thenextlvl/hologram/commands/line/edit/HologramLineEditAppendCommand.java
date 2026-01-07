package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditAppendCommand extends BrigadierCommand {
    private HologramLineEditAppendCommand(HologramPlugin plugin) {
        super(plugin, "append", "holograms.command.line.edit.append");
    }

    // todo: hologram line edit <hologram> <index> append <text>
    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditAppendCommand(plugin);
        return command.create();
    }
}
