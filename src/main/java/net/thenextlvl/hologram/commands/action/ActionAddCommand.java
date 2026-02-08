package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.action.argument.ConnectCommand;
import net.thenextlvl.hologram.commands.action.argument.CyclePageCommand;
import net.thenextlvl.hologram.commands.action.argument.PlaySoundCommand;
import net.thenextlvl.hologram.commands.action.argument.RunConsoleCommand;
import net.thenextlvl.hologram.commands.action.argument.RunPlayerCommand;
import net.thenextlvl.hologram.commands.action.argument.SendActionbarCommand;
import net.thenextlvl.hologram.commands.action.argument.SendMessageCommand;
import net.thenextlvl.hologram.commands.action.argument.SendTitleCommand;
import net.thenextlvl.hologram.commands.action.argument.SetPageCommand;
import net.thenextlvl.hologram.commands.action.argument.TeleportCommand;
import net.thenextlvl.hologram.commands.action.argument.TransferCommand;
import net.thenextlvl.hologram.commands.arguments.EnumArgumentType;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.models.ClickTypes;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.action.ActionCommand.actionArgument;

@NullMarked
public final class ActionAddCommand extends BrigadierCommand {
    private ActionAddCommand(final HologramPlugin plugin) {
        super(plugin, "add", "holograms.command.action.add");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        final var command = new ActionAddCommand(plugin);
        final var commands = clickTypesArgument()
                .then(ConnectCommand.create(plugin, resolver))
                .then(CyclePageCommand.create(plugin, resolver))
                .then(PlaySoundCommand.create(plugin, resolver))
                .then(RunConsoleCommand.create(plugin, resolver))
                .then(RunPlayerCommand.create(plugin, resolver))
                .then(SendActionbarCommand.create(plugin, resolver))
                .then(SendMessageCommand.create(plugin, resolver))
                .then(SendTitleCommand.create(plugin, resolver))
                .then(SetPageCommand.create(plugin, resolver))
                .then(TeleportCommand.create(plugin, resolver))
                .then(TransferCommand.create(plugin, resolver));
        return command.create().then(actionArgument(plugin).then(commands));
    }

    static ArgumentBuilder<CommandSourceStack, ?> clickTypesArgument() {
        return Commands.argument("click-types", new EnumArgumentType<>(ClickTypes.class));
    }
}
