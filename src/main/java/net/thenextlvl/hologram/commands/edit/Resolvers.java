package net.thenextlvl.hologram.commands.edit;

import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * Provides standard resolver implementations for line and page editing.
 */
@NullMarked
public final class Resolvers {
    private Resolvers() {
    }

    /**
     * Resolver for direct hologram line editing.
     * Expects "hologram" and "line" arguments in the command context.
     */
    public static final LineTargetResolver LINE = (context, plugin) -> {
        var hologram = context.getArgument("hologram", Hologram.class);
        var lineNumber = context.getArgument("line", int.class);
        var lineIndex = lineNumber - 1;

        var line = hologram.getLine(lineIndex);
        if (line.isEmpty()) {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.line.invalid",
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Formatter.number("line", lineNumber));
            return Optional.empty();
        }

        return Optional.of(new LineEditTarget(hologram, lineIndex, null, line.get()));
    };

    /**
     * Resolver for page editing within a paged hologram line.
     * Expects "hologram", "line", and "page" arguments in the command context.
     */
    public static final LineTargetResolver PAGE = (context, plugin) -> {
        var hologram = context.getArgument("hologram", Hologram.class);
        var lineNumber = context.getArgument("line", int.class);
        var pageNumber = context.getArgument("page", int.class);
        var lineIndex = lineNumber - 1;
        var pageIndex = pageNumber - 1;

        var sender = context.getSource().getSender();

        var pagedLine = hologram.getLine(lineIndex, PagedHologramLine.class);
        if (pagedLine.isEmpty()) {
            plugin.bundle().sendMessage(sender, "hologram.type.paged",
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Formatter.number("line", lineNumber));
            return Optional.empty();
        }

        var page = pagedLine.get().getPage(pageIndex);
        if (page.isEmpty()) {
            plugin.bundle().sendMessage(sender, "hologram.page.invalid",
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Formatter.number("line", lineNumber),
                    Formatter.number("page", pageNumber));
            return Optional.empty();
        }

        return Optional.of(new LineEditTarget(hologram, lineIndex, pageIndex, page.get()));
    };

    /**
     * Creates a resolver with additional type checking for the line.
     *
     * @param base         the base resolver to use
     * @param requiredType the required line type
     * @param wrongTypeKey the message key for wrong type errors
     * @return a resolver that validates the line type
     */
    public static LineTargetResolver typed(final LineTargetResolver base, final Class<?> requiredType, final String wrongTypeKey) {
        return (context, plugin) -> {
            final var target = base.resolve(context, plugin);
            if (target.isEmpty()) return target;

            final var result = target.get();
            if (!requiredType.isInstance(result.line())) {
                final var sender = context.getSource().getSender();
                if (result.isPage()) {
                    plugin.bundle().sendMessage(sender, wrongTypeKey,
                            Placeholder.unparsed("hologram", result.hologram().getName()),
                            Formatter.number("line", result.displayLineIndex()),
                            Formatter.number("page", result.displayPageIndex()));
                } else {
                    plugin.bundle().sendMessage(sender, wrongTypeKey,
                            Placeholder.unparsed("hologram", result.hologram().getName()),
                            Formatter.number("line", result.displayLineIndex()));
                }
                return Optional.empty();
            }

            return target;
        };
    }
}
