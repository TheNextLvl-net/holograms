package net.thenextlvl.hologram.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.locale.LanguageTags;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class LocaleArgumentType implements CustomArgumentType.Converted<Locale, String> {
    private final HologramPlugin plugin;
    private final boolean filter;

    public LocaleArgumentType(HologramPlugin plugin, boolean filter) {
        this.plugin = plugin;
        this.filter = filter;
    }

    @Override
    public Locale convert(String nativeType) {
        var locale = LanguageTags.getLocale(nativeType);
        if (locale != null) return locale;
        throw new NullPointerException("Unknown language: " + nativeType);
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var key = context.getLastChild().getArgument("translation key", String.class);
        LanguageTags.getLanguages()
                .filter(entry -> !filter || plugin.translations().contains(key, entry.getKey()))
                .map(entry -> StringArgumentType.escapeIfRequired(entry.getValue()))
                .filter(name -> name.toLowerCase().contains(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
