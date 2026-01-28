package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@FunctionalInterface
public interface LineTargetResolver {
    @Nullable
    LineEditTarget resolve(CommandContext<CommandSourceStack> context, HologramPlugin plugin);

    LineTargetResolver LINE = (context, plugin) -> {
        var hologram = context.getArgument("hologram", Hologram.class);
        var lineNumber = context.getArgument("line", int.class);
        var lineIndex = lineNumber - 1;

        var line = hologram.getLine(lineIndex).orElse(null);
        if (line != null) return new LineEditTarget(hologram, lineIndex, null, line);
        plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.line.invalid",
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineNumber));
        return null;
    };

    LineTargetResolver PAGE = (context, plugin) -> {
        var hologram = context.getArgument("hologram", Hologram.class);
        var lineNumber = context.getArgument("line", int.class);
        var pageNumber = context.getArgument("page", int.class);
        var lineIndex = lineNumber - 1;
        var pageIndex = pageNumber - 1;

        var sender = context.getSource().getSender();

        var pagedLine = hologram.getLine(lineIndex, PagedHologramLine.class).orElse(null);
        if (pagedLine == null) {
            plugin.bundle().sendMessage(sender, "hologram.type.paged",
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Formatter.number("line", lineNumber));
            return null;
        }

        var page = pagedLine.getPage(pageIndex).orElse(null);
        if (page == null) {
            plugin.bundle().sendMessage(sender, "hologram.line.invalid",
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Formatter.number("line", lineNumber),
                    Formatter.number("page", pageNumber));
            return null;
        }

        return new LineEditTarget(hologram, lineIndex, pageIndex, page);
    };
}
