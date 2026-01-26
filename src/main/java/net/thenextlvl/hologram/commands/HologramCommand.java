package net.thenextlvl.hologram.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.arguments.HologramArgumentType;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.line.HologramLineCommand;
import net.thenextlvl.hologram.commands.page.HologramPageCommand;
import net.thenextlvl.hologram.commands.translation.HologramTranslationCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramCommand extends BrigadierCommand {
    private HologramCommand(final HologramPlugin plugin) {
        super(plugin, "hologram", "holograms.command");
    }

    public static LiteralCommandNode<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramCommand(plugin);
        return command.create()
                .then(HologramCreateCommand.create(plugin))
                .then(HologramDeleteCommand.create(plugin))
                .then(HologramLineCommand.create(plugin))
                .then(HologramListCommand.create(plugin))
                .then(HologramPageCommand.create(plugin))
                .then(HologramRenameCommand.create(plugin))
                .then(HologramTeleportCommand.create(plugin))
                .then(HologramTranslationCommand.create(plugin))
                .then(HologramViewPermissionCommand.create(plugin))
                .build();
    }

    public static RequiredArgumentBuilder<CommandSourceStack, String> nameArgument() {
        return Commands.argument("name", StringArgumentType.string());
    }

    public static RequiredArgumentBuilder<CommandSourceStack, ?> hologramArgument(final HologramPlugin plugin) {
        return Commands.argument("hologram", new HologramArgumentType(plugin));
    }
}
