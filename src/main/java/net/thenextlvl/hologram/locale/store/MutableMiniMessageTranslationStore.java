package net.thenextlvl.hologram.locale.store;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslator;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.text.MessageFormat;
import java.util.Locale;

@NullMarked
public abstract class MutableMiniMessageTranslationStore extends MutableTranslationStore.StringBased<String> {
    private final Translator translator;

    protected MutableMiniMessageTranslationStore(final Key name, final MiniMessage miniMessage) {
        super(name);
        this.translator = new Translator(miniMessage);
    }

    @Override
    protected String parse(final String string, final Locale locale) {
        return string;
    }

    @Override
    public @Nullable MessageFormat translate(final String key, final Locale locale) {
        return null;
    }

    @Override
    public @Nullable Component translate(final TranslatableComponent component, final Locale locale) {
        return this.translator.translate(component, locale);
    }

    private final class Translator extends MiniMessageTranslator {

        private Translator(final MiniMessage miniMessage) {
            super(miniMessage);
        }

        @Override
        protected @Nullable String getMiniMessageString(final String key, final Locale locale) {
            return MutableMiniMessageTranslationStore.this.translationValue(key, locale);
        }

        @Override
        public Key name() {
            return MutableMiniMessageTranslationStore.this.name();
        }

        @Override
        public TriState hasAnyTranslations() {
            return MutableMiniMessageTranslationStore.this.hasAnyTranslations();
        }
    }
}
