package net.thenextlvl.hologram.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class HologramWithActionSuggestionProvider<T> implements SuggestionProvider<T> {
    private final HologramPlugin plugin;

    public HologramWithActionSuggestionProvider(final HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<T> context, final SuggestionsBuilder builder) {
        plugin.hologramProvider().getHolograms()
                .filter(hologram -> hologram.getLines().noneMatch(line -> line.getActions().isEmpty()))
                .map(Hologram::getName)
                .filter(name -> name.toLowerCase().contains(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
