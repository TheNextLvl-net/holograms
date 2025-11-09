package net.thenextlvl.hologram.command;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.command.argument.HologramArgumentType;
import net.thenextlvl.hologram.command.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.command.line.HologramLineCommand;
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
                .then(HologramLineCommand.create(plugin))
                .then(HologramListCommand.create(plugin))
                // edit command
                //  hologram line remove <hologram> <index>
                //  hologram line edit <hologram> <index> replace <match> <text>
                //  hologram line edit <hologram> <index> set <text>
                //  hologram line edit <hologram> <index> append <text>
                //  hologram line edit <hologram> <index> prepend <text>
                //  hologram line move <hologram> <index> <new-index>
                .build();
    }
    
    public static RequiredArgumentBuilder<CommandSourceStack, ?> hologramArgument(HologramPlugin plugin) {
        return Commands.argument("hologram", new HologramArgumentType(plugin));
    }
}
