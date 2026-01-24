package net.thenextlvl.hologram.locale;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.translation.Translator;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.locale.store.MutableMiniMessageTranslationStore;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

@NullMarked
public final class HologramTranslationStore extends MutableMiniMessageTranslationStore {
    private final TranslatableComponentRenderer<Locale> renderer = TranslatableComponentRenderer.usingTranslationSource(this);
    private final HologramPlugin plugin;
    private final Path path;

    public HologramTranslationStore(final HologramPlugin plugin) {
        super(Key.key("holograms", "translations"), MiniMessage.miniMessage());
        this.path = plugin.getTranslationsPath().resolve("custom");
        this.plugin = plugin;
    }

    public TranslatableComponentRenderer<Locale> getRenderer() {
        return renderer;
    }

    public void read() {
        try (final var files = Files.list(this.path)) {
            files.filter(path -> path.getFileName().toString().endsWith(".properties")).forEach(this::readLocale);
        } catch (final IOException e) {
            plugin.getComponentLogger().warn("Failed to read custom translations", e);
        }
    }

    private void readLocale(final Path path) {
        try (final var reader = Files.newBufferedReader(path)) {
            final var properties = new Properties();
            properties.load(reader);
            final var string = path.getFileName().toString();
            final var substring = string.substring(0, string.length() - ".properties".length());
            final var locale = Translator.parseLocale(substring);
            if (locale == null) plugin.getComponentLogger().warn("Invalid locale for {}", substring);
            else properties.forEach((key, value) -> register(key.toString(), locale, value.toString()));
        } catch (final IOException e) {
            plugin.getComponentLogger().warn("Failed to read custom translations from {}", path, e);
        }
    }

    public void save(final Locale locale) {
        saveLocale(locale, getAllTranslations(locale));
    }

    private void saveLocale(final Locale locale, final Map<String, String> translations) {
        try {
            Files.createDirectories(this.path);
            final var path = this.path.resolve(locale + ".properties");
            final var properties = new Properties();
            translations.forEach(properties::setProperty);
            final var comment = LanguageTags.getLanguageName(locale);
            properties.store(Files.newBufferedWriter(path), comment);
        } catch (final IOException e) {
            plugin.getComponentLogger().warn("Failed to save translations for locale {}", locale, e);
        }
    }
}
