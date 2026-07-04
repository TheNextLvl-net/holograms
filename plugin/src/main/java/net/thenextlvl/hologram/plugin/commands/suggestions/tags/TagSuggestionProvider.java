package net.thenextlvl.hologram.plugin.commands.suggestions.tags;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.plugin.HologramPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@NullMarked
public final class TagSuggestionProvider<S> implements SuggestionProvider<S> {
    private final Map<String, @Nullable SuggestionProvider<S>> tags = new HashMap<>();

    public TagSuggestionProvider(final HologramPlugin plugin) {
        addClosableTags(false, "font:");
        addClosableTags(false, "gradient");
        addClosableTags(false, "pride");
        addClosableTags(false, "rainbow");
        addClosableTags(false, "shadow");
        addClosableTags(false, "transition");

        addClosableTags(true, "bold", "b");
        addClosableTags(true, "italic", "i", "em");
        addClosableTags(true, "obfuscated", "obf");
        addClosableTags(true, "sprite");
        addClosableTags(true, "strikethrough", "st");
        addClosableTags(true, "underline", "u");
        addClosableTags(true, NamedTextColor.NAMES.keys().toArray(new String[0]));

        addSimpleTags("hologram");
        addSimpleTags("line", "lines");
        addSimpleTags("newline", "br");
        addSimpleTags("page", "pages");
        addSimpleTags("player", "players");

        tags.put("<head:", null);
        tags.put("<image:", new ImageTagSuggestionProvider<>());
        tags.put("<key:", null);
        tags.put("<lang:", new LangTagSuggestionProvider<>(plugin));
    }

    private void addClosableTags(final boolean autoClose, final String... tags) {
        for (final var tag : tags) {
            this.tags.put("<" + tag + (autoClose ? ">" : ""), null);
            this.tags.put("</" + tag + ">", null);
        }
    }

    private void addSimpleTags(final String... tags) {
        for (final var tag : tags) this.tags.put("<" + tag + ">", null);
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
        final var openTags = findOpenTags(remaining.substring(0, unclosed ? lastOpen : remaining.length()));
        final var closingTag = openTags.peekLast() != null ? "</" + openTags.peekLast() + ">" : null;

        for (final var entry : tags.entrySet()) {
            final var tag = entry.getKey();
            if (tag.startsWith("</") && !tag.equals(closingTag)) continue;
            if (partial == null || tag.startsWith(partial)) {
                offset.suggest(tag);
            }
        }
        builder.add(offset);

        return builder.buildFuture();
    }

    private ArrayDeque<String> findOpenTags(final String input) {
        final var closeable = tags.keySet().stream()
                .filter(tag -> tag.startsWith("</"))
                .map(tag -> tag.substring(2, tag.length() - 1))
                .collect(Collectors.toSet());
        final var openTags = new ArrayDeque<String>();

        var index = 0;
        while ((index = input.indexOf('<', index)) != -1) {
            final var end = input.indexOf('>', index);
            if (end == -1) break;

            final var name = tagName(input, index, end);
            if (name == null) {
                index = end + 1;
                continue;
            }

            if (input.startsWith("</", index)) {
                openTags.removeLastOccurrence(name);
            } else if (closeable.contains(name)) {
                openTags.addLast(name);
            }
            index = end + 1;
        }

        return openTags;
    }

    private @Nullable String tagName(final String input, final int start, final int end) {
        var nameStart = start + (input.startsWith("</", start) ? 2 : 1);
        while (nameStart < end && Character.isWhitespace(input.charAt(nameStart))) nameStart++;
        var nameEnd = nameStart;
        while (nameEnd < end && input.charAt(nameEnd) != ':' && !Character.isWhitespace(input.charAt(nameEnd)))
            nameEnd++;
        return nameStart != nameEnd ? input.substring(nameStart, nameEnd) : null;
    }
}
