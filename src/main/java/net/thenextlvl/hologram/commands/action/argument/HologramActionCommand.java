package net.thenextlvl.hologram.commands.action.argument;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ActionType;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.models.ClickTypes;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;

@NullMarked
abstract class HologramActionCommand<T> extends SimpleCommand {
    private final ActionType<T> actionType;

    protected HologramActionCommand(final HologramPlugin plugin, final ActionType<T> actionType, final String name) {
        super(plugin, name, null);
        this.actionType = actionType;
    }

    protected int addAction(final CommandContext<CommandSourceStack> context, final T input) {
        final var sender = context.getSource().getSender();
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var line = hologram.getLine(context.getArgument("line", int.class)).orElseThrow(); // todo: fixme properly
        final var actionName = context.getArgument("action", String.class);
        final var clickTypes = context.getArgument("click-types", ClickTypes.class);

        final var previous = line.getAction(actionName);
        final var cooldown = previous.map(ClickAction::getCooldown).orElse(Duration.ZERO);
        final var permission = previous.map(ClickAction::getPermission).orElse(null);
        final var chance = previous.map(ClickAction::getChance).orElse(100);

        final var success = line.addAction(actionName, ClickAction.create(actionType, clickTypes.getClickTypes(), input, action -> {
            action.setChance(chance);
            action.setCooldown(cooldown);
            action.setPermission(permission);
        }));

        final var message = success ? "hologram.action.added" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("hologram", hologram.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
