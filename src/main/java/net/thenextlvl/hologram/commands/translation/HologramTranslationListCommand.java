package net.thenextlvl.hologram.commands.translation;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.locale.LanguageTags;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.translation.HologramTranslationCommand.keyArgument;

@NullMarked
public final class HologramTranslationListCommand extends SimpleCommand {
    private HologramTranslationListCommand(HologramPlugin plugin) {
        super(plugin, "list", "holograms.command.translation.list");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramTranslationListCommand(plugin);
        return command.create()
                .then(keyArgument(plugin).executes(command))
                .executes(command);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource().getSender();
        tryGetArgument(context, "key", String.class).ifPresentOrElse(key -> {
            var translations = plugin.translations().getTranslations(key);
            plugin.bundle().sendMessage(source, "hologram.translation.header",
                    Placeholder.parsed("key", key),
                    Formatter.booleanChoice("plural", translations.size() != 1),
                    Formatter.number("amount", translations.size()));
            translations.forEach((locale, translation) -> {
                plugin.bundle().sendMessage(source, "hologram.translation",
                        Placeholder.parsed("locale", LanguageTags.getLanguageName(locale)),
                        Placeholder.unparsed("translation", translation));
            });
        }, () -> {
            var translations = plugin.translations().getTranslationKeys()
                    .sorted()
                    .map(key -> plugin.bundle().component("hologram.translation.entry", source, Placeholder.parsed("key", key)))
                    .toList();
            plugin.bundle().sendMessage(source, "hologram.translation.list",
                    Formatter.joining("translations", translations),
                    Formatter.number("amount", plugin.translations().size()));
        });
        return SINGLE_SUCCESS;
    }
}
