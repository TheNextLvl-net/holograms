package net.thenextlvl.hologram.commands.translation;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.locale.LanguageTags;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;

import static net.thenextlvl.hologram.commands.translation.HologramTranslationCommand.translationKeyArgument;

@NullMarked
public final class HologramTranslationListCommand extends SimpleCommand {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.plainText();
    private static final int PREVIEW_LENGTH = 40;

    private HologramTranslationListCommand(final HologramPlugin plugin) {
        super(plugin, "list", "holograms.command.translation.list");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramTranslationListCommand(plugin);
        return command.create()
                .then(translationKeyArgument(plugin).executes(command))
                .executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var source = context.getSource().getSender();
        tryGetArgument(context, "translation key", String.class).ifPresentOrElse(key -> {
            final var translations = plugin.translations().getTranslations(key);
            plugin.bundle().sendMessage(source, "hologram.translation.header",
                    Placeholder.parsed("key", key),
                    Formatter.booleanChoice("plural", translations.size() != 1),
                    Formatter.number("amount", translations.size()));
            final var entries = new ArrayList<>(translations.entrySet());
            for (var index = 0; index < entries.size(); index++) {
                final var entry = entries.get(index);
                plugin.bundle().sendMessage(source, "hologram.translation",
                        Placeholder.parsed("tree", index + 1 == entries.size() ? "└" : "├"),
                        Placeholder.parsed("locale", LanguageTags.getLanguageName(entry.getKey())),
                        Placeholder.component("translation", getTranslationPreview(key, entry.getKey(), entry.getValue(), source)));
            }
        }, () -> {
            final var translations = plugin.translations().getTranslationKeys()
                    .sorted()
                    .toList();
            plugin.bundle().sendMessage(source, "hologram.translation.list",
                    Formatter.number("amount", plugin.translations().size()));
            for (var index = 0; index < translations.size(); index++) {
                final var key = translations.get(index);
                plugin.bundle().sendMessage(source, "hologram.translation.entry",
                        Placeholder.parsed("tree", index + 1 == translations.size() ? "└" : "├"),
                        Placeholder.parsed("key", key),
                        Placeholder.parsed("command", "/hologram translation list "
                                + StringArgumentType.escapeIfRequired(key)));
            }
        });
        return SINGLE_SUCCESS;
    }

    private Component getTranslationPreview(final String key, final java.util.Locale locale, final String translation,
                                            final net.kyori.adventure.audience.Audience audience) {
        final var command = "/holo translation add "
                + StringArgumentType.escapeIfRequired(key) + " "
                + StringArgumentType.escapeIfRequired(LanguageTags.getLanguageName(locale)) + " "
                + translation.replace("\n", "\\n");
        return Component.text(trim(PLAIN_TEXT.serialize(MINI_MESSAGE.deserialize(translation)).replace('\n', ' ')))
                .clickEvent(ClickEvent.suggestCommand(command))
                .hoverEvent(HoverEvent.showText(getTranslationHover(audience, locale, translation, command)));
    }

    private Component getTranslationHover(final net.kyori.adventure.audience.Audience audience, final java.util.Locale locale,
                                          final String translation, final String command) {
        final var hover = plugin.bundle().component("hologram.translation.hover", audience,
                Placeholder.component("preview", MINI_MESSAGE.deserialize(translation)));
        if (command.length() <= 256) return hover;
        return hover.appendNewline().appendNewline()
                .append(plugin.bundle().component("hologram.translation.too-long", audience,
                        Placeholder.unparsed("file", plugin.getTranslationsPath()
                                .resolve("custom")
                                .resolve(locale + ".properties")
                                .toString())));
    }

    private String trim(final String text) {
        return text.length() > PREVIEW_LENGTH ? text.substring(0, PREVIEW_LENGTH) + "…" : text;
    }
}
