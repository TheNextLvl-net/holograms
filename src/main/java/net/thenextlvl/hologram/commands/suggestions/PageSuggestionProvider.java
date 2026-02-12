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
        hologram.getLine(lineIndex, PagedHologramLine.class).ifPresent(line -> {
            for (var index = 0; index < line.getPageCount(); index++) {
                final var page = String.valueOf(index + 1);
                if (!page.contains(builder.getRemaining())) continue;
                line.getPage(index).ifPresent(p -> builder.suggest(page, getTooltip(p)));
            }
        });
        return builder.buildFuture();
    }
}
