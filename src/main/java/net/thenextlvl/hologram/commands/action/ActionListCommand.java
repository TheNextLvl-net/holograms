package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.atomic.AtomicInteger;

@NullMarked
final class ActionListCommand extends SimpleCommand {
    private final ActionTargetResolver.Builder resolverBuilder;

    private ActionListCommand(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        super(plugin, "list", "holograms.command.action.list");
        this.resolverBuilder = resolver;
    }

    static LiteralArgumentBuilder<CommandSourceStack> create(
            final HologramPlugin plugin,
            final HologramActionCommand.ArgumentChainFactory chainFactory,
            final ActionTargetResolver.Builder resolver
    ) {
        final var command = new ActionListCommand(plugin, resolver);
        final var chain = chainFactory.create();

        final var head = resolver == ActionTargetResolver.PAGE ? chain.line() : chain.hologram();
        head.executes(command::listAll);
        chain.tail().executes(command);

        return command.create().then(chain.build());
    }

    private int listAll(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        final var hologram = context.getArgument("hologram", Hologram.class);

        if (resolverBuilder == ActionTargetResolver.LINE) {
            return listAllLines(context, hologram);
        } else {
            final var lineNumber = context.getArgument("line", int.class);
            final var line = hologram.getLine(lineNumber - 1).orElse(null);
            if (line == null) {
                plugin.bundle().sendMessage(sender, "hologram.line.invalid");
                return 0;
            }
            if (!(line instanceof final PagedHologramLine pagedLine)) {
                plugin.bundle().sendMessage(sender, "hologram.type.paged");
                return 0;
            }
            return listAllPages(context, hologram, pagedLine, lineNumber);
        }
    }

    private int listAllLines(final CommandContext<CommandSourceStack> context, final Hologram hologram) {
        final var sender = context.getSource().getSender();
        final var count = new AtomicInteger();
        hologram.forEach(line -> {
            if (!line.hasActions()) return;
            plugin.bundle().sendMessage(sender, "hologram.action.list.header",
                    Placeholder.parsed("hologram", hologram.getName()),
                    Formatter.number("line", hologram.getLineIndex(line) + 1));
            line.forEachAction((name, action) -> {
                plugin.bundle().sendMessage(sender, "hologram.action.list",
                        Placeholder.parsed("action", name),
                        actionResolvers(sender, action));
                count.incrementAndGet();
            });
        });
        if (count.get() == 0) {
            plugin.bundle().sendMessage(sender, "hologram.action.list.empty",
                    Placeholder.unparsed("hologram", hologram.getName()));
            return 0;
        }
        return SINGLE_SUCCESS;
    }

    private int listAllPages(final CommandContext<CommandSourceStack> context, final Hologram hologram, final PagedHologramLine pagedLine, final int lineNumber) {
        final var sender = context.getSource().getSender();
        final var count = new AtomicInteger();
        pagedLine.forEachPage(line -> {
            if (line.getActions().isEmpty()) return;
            plugin.bundle().sendMessage(sender, "hologram.action.list.header.page",
                    Placeholder.parsed("hologram", hologram.getName()),
                    Formatter.number("line", lineNumber),
                    Formatter.number("page", pagedLine.getPageIndex(line) + 1));
            line.forEachAction((name, action) -> {
                plugin.bundle().sendMessage(sender, "hologram.action.list",
                        Placeholder.parsed("action", name),
                        actionResolvers(sender, action));
                count.incrementAndGet();
            });
        });
        if (count.get() == 0) {
            plugin.bundle().sendMessage(sender, "hologram.action.list.empty",
                    Placeholder.unparsed("hologram", hologram.getName()));
            return 0;
        }
        return SINGLE_SUCCESS;
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return resolverBuilder.build(context, plugin).resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var sender = context.getSource().getSender();
            if (!line.hasActions()) {
                plugin.bundle().sendMessage(sender, "hologram.action.list.empty",
                        Placeholder.unparsed("hologram", hologram.getName()));
                return 0;
            }
            final var header = pageIndex != null ? "hologram.action.list.header.page" : "hologram.action.list.header";
            plugin.bundle().sendMessage(sender, header, placeholders);
            line.forEachAction((name, action) -> {
                plugin.bundle().sendMessage(sender, "hologram.action.list",
                        TagResolver.resolver(placeholders),
                        Placeholder.parsed("action", name),
                        actionResolvers(sender, action));
            });
            return SINGLE_SUCCESS;
        });
    }

    private TagResolver actionResolvers(final Audience audience, final ClickAction<?> action) {
        return TagResolver.resolver(
                Formatter.number("chance", action.getChance()),
                Formatter.number("cooldown", action.getCooldown().toMillis() / 1000d),
                Placeholder.parsed("action_type", action.getActionType().name()),
                Placeholder.parsed("cost", plugin.economyProvider.format(audience, action.getCost())),
                Placeholder.parsed("permission", action.getPermission().orElse("null"))
        );
    }
}
