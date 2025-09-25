package net.thenextlvl.hologram.command;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.command.argument.HologramArgumentType;
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
                .then(HologramDeleteCommand.create(plugin))
                .then(HologramListCommand.create(plugin))
                .build();
    }
    
    public static RequiredArgumentBuilder<CommandSourceStack, ?> hologramArgument(HologramPlugin plugin) {
        return Commands.argument("hologram", new HologramArgumentType(plugin));
    }
}
