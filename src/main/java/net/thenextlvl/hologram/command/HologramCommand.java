package net.thenextlvl.hologram.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;

public class HologramCommand {
    public static LiteralCommandNode<CommandSourceStack> create(HologramPlugin plugin) {
        return Commands.literal("hologram")
                .requires(source -> source.getSender().hasPermission("holograms.command"))
                .then(HologramCreateCommand.create(plugin))
                .build();
    }
}
