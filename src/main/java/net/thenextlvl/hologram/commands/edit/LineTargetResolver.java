package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.DisplayHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.StaticHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public sealed interface LineTargetResolver permits LineTargetResolver.Line, LineTargetResolver.Page {
    Builder LINE = Line::new;
    Builder PAGE = Page::new;

    CommandContext<CommandSourceStack> context();

    HologramPlugin plugin();

    <T extends HologramLine> int resolve(Resolved<T> resolved, LineType<T> lineType);

    record Line(CommandContext<CommandSourceStack> context, HologramPlugin plugin) implements LineTargetResolver {
        @Override
        public <T extends HologramLine> int resolve(final LineTargetResolver.Resolved<T> handler, final LineTargetResolver.LineType<T> lineType) {
            final var hologram = context.getArgument("hologram", Hologram.class);
            final var lineIndex = context.getArgument("line", int.class) - 1;
            final var line = hologram.getLine(lineIndex).orElse(null);

            if (line == null) {
                plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.line.invalid");
                return 0;
            }
            if (!lineType.clazz.isInstance(line)) {
                plugin.bundle().sendMessage(context.getSource().getSender(), lineType.errorKey);
                return 0;
            }

            return handler.resolved(hologram, lineType.clazz.cast(line), lineIndex, null,
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Formatter.number("line", lineIndex + 1));
        }
    }


    record Page(CommandContext<CommandSourceStack> context, HologramPlugin plugin) implements LineTargetResolver {
        @Override
        public <T extends HologramLine> int resolve(final LineTargetResolver.Resolved<T> handler, final LineTargetResolver.LineType<T> lineType) {
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
            if (!lineType.clazz.isInstance(page)) {
                plugin.bundle().sendMessage(sender, lineType.errorKey);
                return 0;
            }

            return handler.resolved(hologram, lineType.clazz.cast(page), lineIndex, pageIndex,
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Formatter.number("line", lineIndex + 1),
                    Formatter.number("page", pageIndex + 1));
        }
    }

    @FunctionalInterface
    interface Builder {
        LineTargetResolver build(CommandContext<CommandSourceStack> context, HologramPlugin plugin);
    }

    @FunctionalInterface
    interface Resolved<T extends HologramLine> {
        int resolved(Hologram hologram, T line, int lineIndex, @Nullable Integer pageIndex, TagResolver... placeholders);
    }

    record LineType<T extends HologramLine>(Class<T> clazz, String errorKey) {
        public static final LineType<BlockHologramLine> BLOCK = new LineType<>(BlockHologramLine.class, "hologram.type.block");
        public static final LineType<DisplayHologramLine> DISPLAY = new LineType<>(DisplayHologramLine.class, "hologram.type.display");
        public static final LineType<EntityHologramLine> ENTITY = new LineType<>(EntityHologramLine.class, "hologram.type.entity");
        public static final LineType<HologramLine> ANY = new LineType<>(HologramLine.class, "hologram.type.single");
        public static final LineType<ItemHologramLine> ITEM = new LineType<>(ItemHologramLine.class, "hologram.type.item");
        public static final LineType<PagedHologramLine> PAGED = new LineType<>(PagedHologramLine.class, "hologram.type.paged");
        public static final LineType<StaticHologramLine> STATIC = new LineType<>(StaticHologramLine.class, "hologram.type.static");
        public static final LineType<TextHologramLine> TEXT = new LineType<>(TextHologramLine.class, "hologram.type.text");
    }
}
