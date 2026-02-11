package net.thenextlvl.hologram.commands.action.argument;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ActionType;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.commands.action.ActionTargetResolver;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.models.ClickTypes;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;

@NullMarked
abstract class HologramActionCommand<T> extends SimpleCommand {
    private final ActionType<T> actionType;
    protected final ActionTargetResolver.Builder resolverBuilder;

    protected HologramActionCommand(final HologramPlugin plugin, final ActionType<T> actionType, final String name, final ActionTargetResolver.Builder resolver) {
        super(plugin, name, null);
        this.actionType = actionType;
        this.resolverBuilder = resolver;
    }

    protected int addAction(final CommandContext<CommandSourceStack> context, final Hologram hologram, final HologramLine line, final T input) {
        final var sender = context.getSource().getSender();
        final var actionName = context.getArgument("action", String.class);
        final var clickTypes = context.getArgument("click-types", ClickTypes.class);

        final var previous = line.getAction(actionName);
        final var cooldown = previous.map(ClickAction::getCooldown).orElse(Duration.ZERO);
        final var permission = previous.flatMap(ClickAction::getPermission).orElse(null);
        final var chance = previous.map(ClickAction::getChance).orElse(100);
        final var cost = previous.map(ClickAction::getCost).orElse(0d);

        final var success = line.addAction(actionName, plugin.clickActionFactory().create(actionType, clickTypes.getClickTypes(), input, action -> {
            action.setChance(chance);
            action.setCooldown(cooldown);
            action.setCost(cost);
            action.setPermission(permission);
        }));

        final var message = success ? "hologram.action.added" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("hologram", hologram.getName()));
        return success ? Command.SINGLE_SUCCESS : 0;
    }
}
