package net.thenextlvl.hologram.plugin.commands.action;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.plugin.HologramPlugin;
import net.thenextlvl.hologram.plugin.commands.action.argument.CurrencyArgumentType;
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
        final var currency = Commands.argument("currency", new CurrencyArgumentType(plugin));
        chain.tail().then(actionArgument(plugin)
                .then(cost.executes(command).then(currency.executes(command)))
                .executes(command));
        return command.create().then(chain.build());
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final Hologram hologram, final HologramLine line, final ClickAction<?> action, final String actionName, final TagResolver... placeholders) {
        final var sender = context.getSource().getSender();

        final var cost = tryGetArgument(context, "cost", double.class).orElse(null);
        final var currency = tryGetArgument(context, "currency", String.class).orElse(null);


        final var format = plugin.economyProvider.format(sender, action.getCurrency().orElse(null), cost != null ? cost : action.getCost());
        if (cost == null) {
            plugin.bundle().sendMessage(sender, "hologram.action.cost",
                    TagResolver.resolver(placeholders),
                    Placeholder.component("cost", format),
                    Placeholder.unparsed("action", actionName));
            return SINGLE_SUCCESS;
        }

        final var success = action.setCost(cost) | action.setCurrency(currency);
        final var message = success ? "hologram.action.cost.set" : "nothing.changed";

        plugin.bundle().sendMessage(sender, message,
                TagResolver.resolver(placeholders),
                Placeholder.component("cost", format),
                Placeholder.unparsed("action", actionName));
        return success ? SINGLE_SUCCESS : 0;
    }
}
