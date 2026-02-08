package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public sealed interface ActionTargetResolver permits ActionTargetResolver.Line, ActionTargetResolver.Page {
    Builder LINE = Line::new;
    Builder PAGE = Page::new;

    CommandContext<CommandSourceStack> context();

    HologramPlugin plugin();

    int resolve(Resolved resolved) throws CommandSyntaxException;

    record Line(CommandContext<CommandSourceStack> context, HologramPlugin plugin) implements ActionTargetResolver {
        @Override
        public int resolve(final Resolved handler) throws CommandSyntaxException {
            final var hologram = context.getArgument("hologram", Hologram.class);
            final var lineIndex = context.getArgument("line", int.class) - 1;
            final var line = hologram.getLine(lineIndex).orElse(null);

            if (line == null) {
                plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.line.invalid");
                return 0;
            }

            return handler.resolved(hologram, line, lineIndex, null,
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Formatter.number("line", lineIndex + 1));
        }
    }

    record Page(CommandContext<CommandSourceStack> context, HologramPlugin plugin) implements ActionTargetResolver {
        @Override
        public int resolve(final Resolved handler) throws CommandSyntaxException {
            final var hologram = context.getArgument("hologram", Hologram.class);
            final var lineIndex = context.getArgument("line", int.class) - 1;
            final var pageIndex = context.getArgument("page", int.class) - 1;

            final var sender = context.getSource().getSender();
            final var line = hologram.getLine(lineIndex).orElse(null);

            if (line == null) {
                plugin.bundle().sendMessage(sender, "hologram.line.invalid");
                return 0;
            }
            if (!(line instanceof final PagedHologramLine pagedLine)) {
                plugin.bundle().sendMessage(sender, "hologram.type.paged");
                return 0;
            }

            final var page = pagedLine.getPage(pageIndex).orElse(null);

            if (page == null) {
                plugin.bundle().sendMessage(sender, "hologram.line.invalid");
                return 0;
            }

            return handler.resolved(hologram, page, lineIndex, pageIndex,
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Formatter.number("line", lineIndex + 1),
                    Formatter.number("page", pageIndex + 1));
        }
    }

    @FunctionalInterface
    interface Builder {
        ActionTargetResolver build(CommandContext<CommandSourceStack> context, HologramPlugin plugin);
    }

    @FunctionalInterface
    interface Resolved {
        int resolved(Hologram hologram, HologramLine line, int lineIndex, @Nullable Integer pageIndex, TagResolver... placeholders) throws CommandSyntaxException;
    }
}
