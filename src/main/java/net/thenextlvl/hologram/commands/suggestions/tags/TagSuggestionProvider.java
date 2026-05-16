package net.thenextlvl.hologram.commands.suggestions.tags;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.HologramPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@NullMarked
public final class TagSuggestionProvider<S> implements SuggestionProvider<S> {
    private final HologramPlugin plugin;
    private final Map<String, @Nullable SuggestionProvider<S>> tags;

    public TagSuggestionProvider(final HologramPlugin plugin) {
        this.plugin = plugin;
        this.tags = new HashMap<>();
        tags.put("</b>", null);
        tags.put("</bold>", null);
        tags.put("</em>", null);
        tags.put("</font>", null);
        tags.put("</gradient>", null);
        tags.put("</i>", null);
        tags.put("</italic>", null);
        tags.put("</obf>", null);
        tags.put("</obfuscated>", null);
        tags.put("</pride>", null);
        tags.put("</rainbow>", null);
        tags.put("</shadow>", null);
        tags.put("</sprite>", null);
        tags.put("</st>", null);
        tags.put("</strikethrough>", null);
        tags.put("</transition>", null);
        tags.put("</u>", null);
        tags.put("</underline>", null);
        tags.put("<b>", null);
        tags.put("<bold>", null);
        tags.put("<br>", null);
        tags.put("<em>", null);
        tags.put("<font:", null);
        tags.put("<gradient", null);
        tags.put("<head:", null);
        tags.put("<hologram>", null);
        tags.put("<i>", null);
        tags.put("<image:", new ImageTagSuggestionProvider<>());
        tags.put("<italic>", null);
        tags.put("<key:", null);
        tags.put("<lang:", new LangTagSuggestionProvider<>(plugin));
        tags.put("<lines>", null);
        tags.put("<newline>", null);
        tags.put("<obf>", null);
        tags.put("<obfuscated>", null);
        tags.put("<page>", null);
        tags.put("<pages>", null);
        tags.put("<player>", null);
        tags.put("<players>", null);
        tags.put("<pride", null);
        tags.put("<rainbow", null);
        tags.put("<shadow", null);
        tags.put("<sprite", null);
        tags.put("<st>", null);
        tags.put("<strikethrough>", null);
        tags.put("<transition", null);
        tags.put("<u>", null);
        tags.put("<underline>", null);
        NamedTextColor.NAMES.keys().forEach(s -> {
            tags.put("<" + s + ">", null);
            tags.put("</" + s + ">", null);
        });
    }

    public TagSuggestionProvider(final HologramPlugin plugin, final Map<String, @Nullable SuggestionProvider<S>> tags) {
        this.plugin = plugin;
        this.tags = tags;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) throws CommandSyntaxException {
        final var remaining = builder.getRemaining();
        final var start = builder.getStart() + remaining.length();

        for (final var entry : tags.entrySet()) {
            final var tag = entry.getKey();
            final var provider = entry.getValue();
            if (provider == null) continue;

            final var index = remaining.lastIndexOf(tag);
            if (index == -1 || remaining.indexOf('>', index) != -1) continue;

            final var offset = builder.createOffset(builder.getStart() + index + tag.length());
            return provider.getSuggestions(context, offset);
        }

        final var lastOpen = remaining.lastIndexOf('<');
        final var unclosed = lastOpen != -1 && remaining.indexOf('>', lastOpen) == -1;
        final var partial = unclosed ? remaining.substring(lastOpen) : null;
        final var offset = builder.createOffset(unclosed ? builder.getStart() + lastOpen : start);

        for (final var entry : tags.entrySet()) {
            if (partial == null || entry.getKey().startsWith(partial)) {
                offset.suggest(entry.getKey());
            }
        }
        builder.add(offset);

        return builder.buildFuture();
    }
}
