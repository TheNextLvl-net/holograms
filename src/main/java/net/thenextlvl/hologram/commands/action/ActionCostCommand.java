package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.line.HologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class ActionCostCommand extends ActionCommand {
    private ActionCostCommand(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        super(plugin, "cost", "holograms.command.action.cost", resolver);
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(
            final HologramPlugin plugin,
            final HologramActionCommand.ArgumentChainFactory chainFactory,
            final ActionTargetResolver.Builder resolver
    ) {
        final var command = new ActionCostCommand(plugin, resolver);
        final var chain = chainFactory.create();
        final var cost = Commands.argument("cost", DoubleArgumentType.doubleArg(0));
        chain.tail().then(actionArgument(plugin)
                .then(cost.executes(command))
                .executes(command));
        return command.create().then(chain.build());
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final Hologram hologram, final HologramLine line, final ClickAction<?> action, final String actionName, final TagResolver... placeholders) {
        final var cost = tryGetArgument(context, "cost", double.class);
        final var success = cost.map(action::setCost).orElse(false);
        final var message = success ? "hologram.action.cost.set" : cost.isEmpty()
                ? "hologram.action.cost" : "nothing.changed";
        final var sender = context.getSource().getSender();
        final var formatted = plugin.economyProvider.format(sender, cost.orElse(action.getCost()));
        plugin.bundle().sendMessage(sender, message,
                TagResolver.resolver(placeholders),
                Placeholder.unparsed("action", actionName),
                Placeholder.parsed("cost", formatted));
        return SINGLE_SUCCESS;
    }
}
