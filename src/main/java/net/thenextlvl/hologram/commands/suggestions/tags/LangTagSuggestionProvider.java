package net.thenextlvl.hologram.commands.suggestions.tags;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.thenextlvl.hologram.HologramPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class LangTagSuggestionProvider<S> implements SuggestionProvider<S> {
    private final HologramPlugin plugin;

    public LangTagSuggestionProvider(final HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final var remaining = builder.getRemaining();
        final var suggestions = plugin.translations().getTranslationKeys()
                .filter(key -> key.startsWith(remaining.trim()))
                .map(key -> {
                    final var dot = key.indexOf('.', remaining.length());
                    return dot != -1 ? key.substring(0, dot + 1) : key + ">";
                })
                .distinct()
                .toList();
        if (suggestions.isEmpty()) {
            builder.add(builder.createOffset(builder.getStart() + remaining.length()).suggest(">"));
        } else {
            suggestions.forEach(builder::suggest);
        }
        return builder.buildFuture();
    }
}
