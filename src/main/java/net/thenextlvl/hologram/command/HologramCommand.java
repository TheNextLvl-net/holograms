package net.thenextlvl.hologram.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.command.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramCommand extends BrigadierCommand {
    private HologramCommand(HologramPlugin plugin) {
        super(plugin, "hologram", "holograms.command");
    }

    public static LiteralCommandNode<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramCommand(plugin);
        return command.create()
                .then(HologramCreateCommand.create(plugin))
                .build();
    }
}
