package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.action.argument.ConnectCommand;
import net.thenextlvl.hologram.commands.action.argument.PlaySoundCommand;
import net.thenextlvl.hologram.commands.action.argument.RunConsoleCommand;
import net.thenextlvl.hologram.commands.action.argument.RunPlayerCommand;
import net.thenextlvl.hologram.commands.action.argument.SendActionbarCommand;
import net.thenextlvl.hologram.commands.action.argument.SendMessageCommand;
import net.thenextlvl.hologram.commands.action.argument.SendTitleCommand;
import net.thenextlvl.hologram.commands.action.argument.TeleportCommand;
import net.thenextlvl.hologram.commands.action.argument.TransferCommand;
import net.thenextlvl.hologram.commands.arguments.EnumArgumentType;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.models.ClickTypes;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;
import static net.thenextlvl.hologram.commands.action.HologramActionCommand.actionArgument;

@NullMarked
public final class HologramActionAddCommand extends BrigadierCommand {
    private HologramActionAddCommand(final HologramPlugin plugin) {
        super(plugin, "add", "holograms.command.action.add");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramActionAddCommand(plugin);
        final var commands = clickTypesArgument()
                .then(ConnectCommand.create(plugin))
                .then(PlaySoundCommand.create(plugin))
                .then(RunConsoleCommand.create(plugin))
                .then(RunPlayerCommand.create(plugin))
                .then(SendActionbarCommand.create(plugin))
                .then(SendMessageCommand.create(plugin))
                .then(SendTitleCommand.create(plugin))
                .then(TeleportCommand.create(plugin))
                .then(TransferCommand.create(plugin));
        return command.create().then(hologramArgument(plugin)
                .then(actionArgument(plugin).then(commands)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> clickTypesArgument() {
        return Commands.argument("click-types", new EnumArgumentType<>(ClickTypes.class));
    }
}
