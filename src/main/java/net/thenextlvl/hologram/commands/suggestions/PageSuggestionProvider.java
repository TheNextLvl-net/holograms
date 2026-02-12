package net.thenextlvl.hologram.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

import static net.thenextlvl.hologram.commands.suggestions.LineSuggestionProvider.getTooltip;

@NullMarked
public final class PageSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    public static final PageSuggestionProvider INSTANCE = new PageSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        final var hologram = context.getLastChild().getArgument("hologram", Hologram.class);
        final var lineIndex = context.getLastChild().getArgument("line", int.class) - 1;
        final var line = hologram.getLine(lineIndex, PagedHologramLine.class);

        if (line.isEmpty()) return builder.buildFuture();

        final var pagedLine = line.get();
        final var pages = pagedLine.getPages().toList(); // fixme: stream -> list -> for -> get -> that's stupid

        for (var index = 1; index <= pages.size(); index++) {
            final var page = String.valueOf(index);
            if (!page.contains(builder.getRemaining())) continue;
            builder.suggest(page, getTooltip(pages.get(index - 1)));
        }
        return builder.buildFuture();
    }
}
