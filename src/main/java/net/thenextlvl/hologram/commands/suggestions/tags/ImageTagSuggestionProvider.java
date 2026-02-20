package net.thenextlvl.hologram.commands.suggestions.tags;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class ImageTagSuggestionProvider<S> implements SuggestionProvider<S> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final var remaining = builder.getRemaining();
        final var trimmed = remaining.trim();
        if (trimmed.isEmpty()) {
            builder.suggest("https://");
        } else if (trimmed.endsWith(",")) {
            final var offset = builder.createOffset(builder.getStart() + remaining.lastIndexOf(',') + 1);
            for (var i = 1; i <= 9; i++) offset.suggest(i);
            builder.add(offset);
        } else if (trimmed.contains(",") && Character.isDigit(trimmed.charAt(trimmed.length() - 1))) {
            final var beginIndex = remaining.lastIndexOf(',') + 1;
            final var offset = builder.createOffset(builder.getStart() + beginIndex);
            final var number = remaining.substring(beginIndex);
            for (var i = 0; i <= 9; i++) offset.suggest(number + i);
            builder.add(offset);
            final var end = builder.createOffset(builder.getStart() + remaining.length());
            builder.add(end.suggest(">"));
        } else if (!trimmed.endsWith("//")) {
            final var offset = builder.createOffset(builder.getStart() + remaining.length());
            if (!trimmed.contains(",")) builder.add(offset.suggest(","));
            builder.add(offset.suggest(">"));
        }
        return builder.buildFuture();
    }
}
