package net.thenextlvl.hologram.plugin.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.plugin.HologramPlugin;
import net.thenextlvl.hologram.plugin.commands.arguments.HologramArgumentType;
import net.thenextlvl.hologram.plugin.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.dialog.DialogSupport;
import net.thenextlvl.hologram.plugin.commands.line.HologramLineCommand;
import net.thenextlvl.hologram.plugin.commands.page.HologramPageCommand;
import net.thenextlvl.hologram.plugin.commands.suggestions.PermissionSuggestionProvider;
import net.thenextlvl.hologram.plugin.commands.translation.HologramTranslationCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramCommand extends BrigadierCommand {
    private HologramCommand(final HologramPlugin plugin) {
        super(plugin, "hologram", "holograms.command");
    }

    public static LiteralCommandNode<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramCommand(plugin);
        return command.create()
                .then(HologramCloneCommand.create(plugin))
                .then(HologramCreateCommand.create(plugin))
                .then(HologramDeleteCommand.create(plugin))
                .then(HologramHelpCommand.create(plugin))
                .then(HologramInfoCommand.create(plugin))
                .then(HologramLineCommand.create(plugin))
                .then(HologramListCommand.create(plugin))
                .then(HologramPageCommand.create(plugin))
                .then(HologramRenameCommand.create(plugin))
                .then(HologramTeleportCommand.create(plugin))
                .then(HologramTranslationCommand.create(plugin))
                .then(HologramViewPermissionCommand.create(plugin))
                .then(Commands.literal("dialog").executes(context -> {
                    final var sender = context.getSource().getSender();
                    DialogSupport.showLast(sender);
                    return 1;
                }))
                .build();
    }

    public static RequiredArgumentBuilder<CommandSourceStack, String> nameArgument() {
        return Commands.argument("name", StringArgumentType.string());
    }

    public static RequiredArgumentBuilder<CommandSourceStack, ?> hologramArgument(final HologramPlugin plugin) {
        return hologramArgument(plugin, false);
    }

    public static RequiredArgumentBuilder<CommandSourceStack, ?> hologramArgument(final HologramPlugin plugin, final boolean pagedOnly) {
        return Commands.argument("hologram", new HologramArgumentType(plugin, pagedOnly));
    }

    public static RequiredArgumentBuilder<CommandSourceStack, String> permissionArgument(final HologramPlugin plugin) {
        return Commands.argument("permission", StringArgumentType.string())
                .suggests(new PermissionSuggestionProvider<>(plugin));
    }
}
