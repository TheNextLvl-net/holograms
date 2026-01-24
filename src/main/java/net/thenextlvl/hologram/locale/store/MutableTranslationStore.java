package net.thenextlvl.hologram.locale.store;

import net.kyori.adventure.internal.Internals;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.TranslationStore;
import net.kyori.adventure.util.TriState;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@NullMarked
public abstract class MutableTranslationStore<T> implements Examinable, TranslationStore<T> {
    private final Key name;
    private final Map<String, Translation> translations = new ConcurrentHashMap<>();
    private volatile Locale defaultLocale = Locale.US;

    protected MutableTranslationStore(final Key name) {
        this.name = name;
    }

    protected @Nullable T translationValue(final String key, final Locale locale) {
        final Translation translation = this.translations.get(key);
        if (translation == null) return null;
        return translation.translate(locale);
    }

    @Override
    public final boolean contains(final String key) {
        return this.translations.containsKey(key);
    }

    @Override
    public final boolean contains(final String key, final Locale locale) {
        final Translation translation = this.translations.get(key);
        if (translation == null) return false;
        return translation.translations.get(locale) != null;
    }

    @Override
    public final boolean canTranslate(final String key, final Locale locale) {
        final Translation translation = this.translations.get(key);
        if (translation == null) return false;
        return translation.translate(locale) != null;
    }

    @Override
    public final void defaultLocale(final Locale locale) {
        this.defaultLocale = locale;
    }

    public void unregister(final String key, final Locale locale) {
        this.translations.computeIfPresent(key, (k, translation) -> {
            translation.translations.remove(locale);
            return translation.translations.isEmpty() ? null : translation;
        });
    }

    public int size() {
        return this.translations.size();
    }

    public final boolean override(final String key, final Locale locale, final T translation) {
        return this.translations.computeIfAbsent(key, Translation::new).override(locale, translation);
    }

    @Override
    public final void register(final String key, final Locale locale, final T translation) {
        this.translations.computeIfAbsent(key, Translation::new).register(locale, translation);
    }

    @Override
    public final void registerAll(final Locale locale, final Map<String, T> translations) {
        this.registerAll(locale, translations.keySet(), translations::get);
    }

    @Override
    public final void registerAll(final Locale locale, final Set<String> keys, final Function<String, T> function) {
        IllegalArgumentException firstError = null;
        int errorCount = 0;
        for (final String key : keys) {
            try {
                this.register(key, locale, function.apply(key));
            } catch (final IllegalArgumentException e) {
                if (firstError == null) {
                    firstError = e;
                }
                errorCount++;
            }
        }
        if (firstError != null) {
            if (errorCount == 1) {
                throw firstError;
            } else if (errorCount > 1) {
                throw new IllegalArgumentException(String.format("Invalid key (and %d more)", errorCount - 1), firstError);
            }
        }
    }

    @Override
    public final void unregister(final String key) {
        this.translations.remove(key);
    }

    @Override
    public final Key name() {
        return this.name;
    }

    @Override
    public final TriState hasAnyTranslations() {
        return TriState.byBoolean(!this.translations.isEmpty());
    }

    @Override
    public final Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("translations", this.translations));
    }

    @Override
    public final boolean equals(final Object other) {
        if (this == other) return true;
        if (!(other instanceof MutableTranslationStore<?> that)) return false;

        return this.name.equals(that.name);
    }

    @Override
    public final int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public final String toString() {
        return Internals.toString(this);
    }

    public Stream<String> getTranslationKeys() {
        return translations.keySet().stream();
    }

    public Map<Locale, T> getTranslations(String key) {
        var translation = translations.get(key);
        return translation == null ? Map.of() : translation.translations;
    }

    public Map<String, T> getAllTranslations(Locale locale) {
        var map = new HashMap<String, T>();
        translations.values().forEach(translation -> {
            var t = translation.translations.get(locale);
            if (t != null) map.put(translation.key, t);
        });
        return map;
        
    }

    public final class Translation implements Examinable {
        private final String key;
        private final Map<Locale, T> translations;

        private Translation(final String key) {
            this.key = key;
            this.translations = new ConcurrentHashMap<>();
        }

        private @Nullable T translate(final Locale locale) {
            T format = this.translations.get(locale);
            if (format == null) {
                format = this.translations.get(new Locale.Builder().setLanguage(locale.getLanguage()).build()); // try without country
                if (format == null) {
                    format = this.translations.get(MutableTranslationStore.this.defaultLocale); // try local default locale
                    if (format == null) {
                        format = this.translations.get(TranslationLocales.global()); // try global default locale
                    }
                }
            }
            return format;
        }

        private void register(final Locale locale, final T translation) {
            if (this.translations.putIfAbsent(locale, translation) != null) {
                throw new IllegalArgumentException(String.format("Translation already exists: %s for %s", this.key, locale));
            }
        }

        private boolean override(final Locale locale, final T translation) {
            T put = this.translations.put(locale, translation);
            return !translation.equals(put);
        }

        @Override
        public Stream<? extends ExaminableProperty> examinableProperties() {
            return Stream.of(
                    ExaminableProperty.of("key", this.key),
                    ExaminableProperty.of("translations", this.translations)
            );
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) return true;
            if (!(other instanceof MutableTranslationStore<?>.Translation that)) return false;
            return this.key.equals(that.key) && this.translations.equals(that.translations);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.key, this.translations);
        }

        @Override
        public String toString() {
            return Internals.toString(this);
        }
    }

    public abstract static class StringBased<T> extends MutableTranslationStore<T> implements TranslationStore.StringBased<T> {
        private static final Pattern SINGLE_QUOTE_PATTERN = Pattern.compile("'");

        protected StringBased(final Key name) {
            super(name);
        }

        protected abstract T parse(final String string, final Locale locale);

        @Override
        public final void registerAll(final Locale locale, final Path path, final boolean escapeSingleQuotes) {
            try (final BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                this.registerAll(locale, new PropertyResourceBundle(reader), escapeSingleQuotes);
            } catch (final IOException e) {
                // ignored
            }
        }

        @Override
        public final void registerAll(final Locale locale, final ResourceBundle bundle, final boolean escapeSingleQuotes) {
            this.registerAll(locale, bundle.keySet(), key -> {
                final String format = bundle.getString(key);
                return this.parse(
                        escapeSingleQuotes
                                ? SINGLE_QUOTE_PATTERN.matcher(format).replaceAll("''")
                                : format,
                        locale
                );
            });
        }
    }
}
