package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.line.HologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class ActionChanceCommand extends ActionCommand {
    private ActionChanceCommand(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        super(plugin, "chance", "holograms.command.action.chance", resolver);
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(
            final HologramPlugin plugin,
            final HologramActionCommand.ArgumentChainFactory chainFactory,
            final ActionTargetResolver.Builder resolver
    ) {
        final var command = new ActionChanceCommand(plugin, resolver);
        final var chain = chainFactory.create();
        chain.tail().then(actionArgument(plugin)
                .then(chanceArgument().executes(command))
                .executes(command));
        return command.create().then(chain.build());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> chanceArgument() {
        return Commands.argument("chance", IntegerArgumentType.integer(0, 100));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final Hologram hologram, final HologramLine line, final ClickAction<?> action, final String actionName, final TagResolver... placeholders) {
        final var chance = tryGetArgument(context, "chance", int.class).orElse(null);
        final var success = chance != null && action.setChance(chance);
        final var message = chance == null ? "hologram.action.chance"
                : success ? "hologram.action.chance.set" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                TagResolver.resolver(placeholders),
                Placeholder.unparsed("action", actionName),
                Formatter.number("chance", chance != null ? chance : action.getChance()));
        return success ? SINGLE_SUCCESS : 0;
    }
}
