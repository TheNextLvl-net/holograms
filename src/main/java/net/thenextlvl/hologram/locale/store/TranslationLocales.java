package net.thenextlvl.hologram.locale.store;

import net.kyori.adventure.internal.properties.AdventureProperties;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.function.Supplier;

@NullMarked
final class TranslationLocales {
    private static final Supplier<@Nullable Locale> GLOBAL;

    static {
        final @Nullable String property = AdventureProperties.DEFAULT_TRANSLATION_LOCALE.value();
        if (property == null || property.isEmpty()) {
            GLOBAL = () -> Locale.US;
        } else if (property.equals("system")) {
            GLOBAL = Locale::getDefault;
        } else {
            final Locale locale = Translator.parseLocale(property);
            GLOBAL = () -> locale;
        }
    }

    private TranslationLocales() {
    }

    static @Nullable Locale global() {
        return GLOBAL.get();
    }
}
