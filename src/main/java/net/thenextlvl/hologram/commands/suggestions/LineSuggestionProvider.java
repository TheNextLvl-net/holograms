package net.thenextlvl.hologram.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class LineSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    public static final LineSuggestionProvider INSTANCE = new LineSuggestionProvider();
    
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        var hologram = context.getLastChild().getArgument("hologram", Hologram.class);
        for (var index = 1; index <= hologram.getLineCount(); index++) {
            var line = String.valueOf(index);
            if (!line.contains(builder.getRemaining())) continue;
            builder.suggest(line);
        }
        return builder.buildFuture();
    }
}
