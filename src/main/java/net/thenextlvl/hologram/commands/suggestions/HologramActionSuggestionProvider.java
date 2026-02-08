package net.thenextlvl.hologram.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class HologramActionSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        final var hologram = context.getLastChild().getArgument("hologram", Hologram.class);
        final var lineIndex = context.getLastChild().getArgument("line", int.class) - 1;
        final var line = hologram.getLine(lineIndex).orElse(null);
        if (line == null) return builder.buildFuture();

        final var pageIndex = tryGetArgument(context, "page");
        final HologramLine target;
        if (pageIndex != null && line instanceof final PagedHologramLine pagedLine) {
            target = pagedLine.getPage(pageIndex - 1).orElse(null);
        } else {
            target = line;
        }

        if (target == null) return builder.buildFuture();

        target.getActions().keySet().stream()
                .filter(string -> string.contains(builder.getRemaining()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    private static @Nullable Integer tryGetArgument(final CommandContext<CommandSourceStack> context, final String name) {
        try {
            return context.getLastChild().getArgument(name, Integer.class);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }
}
