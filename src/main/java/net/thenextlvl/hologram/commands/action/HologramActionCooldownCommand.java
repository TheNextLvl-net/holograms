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
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.commands.suggestions.HologramWithActionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;
import static net.thenextlvl.hologram.commands.action.HologramActionCommand.actionArgument;

@NullMarked
final class HologramActionCooldownCommand extends ActionCommand {
    private HologramActionCooldownCommand(final HologramPlugin plugin) {
        super(plugin, "cooldown", "holograms.command.action.cooldown");
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramActionCooldownCommand(plugin);
        return command.create().then(hologramArgument(plugin)
                .suggests(new HologramWithActionSuggestionProvider<>(plugin))
                .then(actionArgument(plugin)
                        .then(cooldownArgument().executes(command))
                        .executes(command)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> cooldownArgument() {
        return Commands.argument("cooldown", ArgumentTypes.time());
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final Hologram hologram, final ClickAction<?> action, final String actionName) {
        final var cooldown = tryGetArgument(context, "cooldown", int.class).map(Tick::of).orElse(null);
        final var success = cooldown != null && action.setCooldown(cooldown);
        final var message = cooldown == null ? "hologram.action.cooldown"
                : success ? cooldown.isZero() ? "hologram.action.cooldown.removed"
                : "hologram.action.cooldown.set"
                : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("action", actionName),
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("cooldown", (cooldown != null ? cooldown : action.getCooldown()).toMillis() / 1000d));
        return success ? SINGLE_SUCCESS : 0;
    }
}
