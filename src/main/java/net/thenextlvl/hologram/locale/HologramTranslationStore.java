package net.thenextlvl.hologram.locale;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.translation.Translator;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.locale.store.MutableMiniMessageTranslationStore;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NullMarked
public final class HologramTranslationStore extends MutableMiniMessageTranslationStore {
    private final TranslatableComponentRenderer<Locale> renderer = TranslatableComponentRenderer.usingTranslationSource(this);
    private final HologramPlugin plugin;
    private final Path path;

    public HologramTranslationStore(final HologramPlugin plugin) {
        super(Key.key("holograms", "translations"), MiniMessage.miniMessage());
        this.path = plugin.getTranslationsPath().resolve("custom");
        this.plugin = plugin;

        read();
        registerDefaults();
    }

    public String translate(final Player player, final String string, final int depth) {
        if (depth > 10) {
            plugin.getComponentLogger().warn("Too many recursive translations for {}", string);
            return string;
        }
        final var allTranslations = plugin.translations().getAllTranslations(player.locale());
        var translated = string;
        for (final var entry : allTranslations.entrySet()) {
            final var key = Pattern.quote(entry.getKey());
            final var value = Matcher.quoteReplacement(entry.getValue());
            translated = translated.replaceAll("(?<!\\\\)<lang:" + key + ">", value);
            translated = translated.replaceAll("(?<!\\\\)<translate:" + key + ">", value);
            translated = translated.replaceAll("(?<!\\\\)<tr:" + key + ">", value);
        }
        if (translated.equals(string)) return string;
        return translate(player, translated, depth + 1);
    }

    public void registerDefaults() {
        for (var i = 0; i < Tips.TIPS_ENGLISH.size(); ++i) {
            registerIfMissing("hologram.tip." + (i + 1), Locale.US, Tips.TIPS_ENGLISH.get(i));
        }

        for (var i = 0; i < Tips.TIPS_GERMAN.size(); ++i) {
            registerIfMissing("hologram.tip." + (i + 1), Locale.GERMANY, Tips.TIPS_GERMAN.get(i));
        }

        registerIfMissing("hologram.tip.page", Locale.US, "<italic><gray>Tip <page>/<pages></gray></italic>");
        registerIfMissing("hologram.tip.page", Locale.GERMANY, "<italic><gray>Tipp <page>/<pages></gray></italic>");
    }

    private void registerIfMissing(final String key, final Locale locale, final String value) {
        if (contains(key, locale)) return;
        register(key, locale, value);
        save(locale);
    }

    public TranslatableComponentRenderer<Locale> getRenderer() {
        return renderer;
    }

    public boolean read() {
        if (!Files.isDirectory(this.path)) return false;
        try (final var files = Files.list(this.path)) {
            unregisterAll();
            files.filter(path -> path.getFileName().toString().endsWith(".properties")).forEach(this::readLocale);
            return true;
        } catch (final IOException e) {
            plugin.getComponentLogger().warn("Failed to read custom translations", e);
            return false;
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
