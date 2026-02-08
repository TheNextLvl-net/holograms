package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.util.Tick;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.line.HologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramActionCooldownCommand extends ActionCommand {
    private HologramActionCooldownCommand(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        super(plugin, "cooldown", "holograms.command.action.cooldown", resolver);
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        final var command = new HologramActionCooldownCommand(plugin, resolver);
        return command.create().then(actionArgument(plugin)
                .then(cooldownArgument().executes(command))
                .executes(command));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> cooldownArgument() {
        return Commands.argument("cooldown", ArgumentTypes.time());
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final Hologram hologram, final HologramLine line, final ClickAction<?> action, final String actionName, final TagResolver... placeholders) {
        final var cooldown = tryGetArgument(context, "cooldown", int.class).map(Tick::of).orElse(null);
        final var success = cooldown != null && action.setCooldown(cooldown);
        final var message = cooldown == null ? "hologram.action.cooldown"
                : success ? cooldown.isZero() ? "hologram.action.cooldown.removed"
                : "hologram.action.cooldown.set"
                : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message, concat(placeholders,
                Placeholder.unparsed("action", actionName),
                Formatter.number("cooldown", (cooldown != null ? cooldown : action.getCooldown()).toMillis() / 1000d)));
        return success ? SINGLE_SUCCESS : 0;
    }
}
