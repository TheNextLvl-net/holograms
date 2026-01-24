package net.thenextlvl.hologram.commands.translation;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.arguments.LocaleArgumentType;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.locale.LanguageTags;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;

import static net.thenextlvl.hologram.commands.translation.HologramTranslationCommand.translationKeyArgument;

@NullMarked
public final class HologramTranslationAddCommand extends SimpleCommand {
    
    private HologramTranslationAddCommand(final HologramPlugin plugin) {
        super(plugin, "add", "holograms.command.translation.add");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramTranslationAddCommand(plugin);
        final var locale = Commands.argument("locale", new LocaleArgumentType(plugin, false));
        final var translation = Commands.argument("translation", StringArgumentType.greedyString());
        return command.create().then(translationKeyArgument(plugin)
                .then(locale.then(translation.executes(command))));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var sender = context.getSource().getSender();
        final var locale = context.getArgument("locale", Locale.class);
        final var key = context.getArgument("translation key", String.class);
        final var translation = context.getArgument("translation", String.class);

        final var success = plugin.translations().override(key, locale, translation);
        if (success) {
            plugin.updateHologramTextLines(null);
            plugin.translations().save(locale);
        }

        final var message = success ? "hologram.translation.added" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("locale", LanguageTags.getLanguageName(locale)),
                Placeholder.parsed("key", key));
        return success ? SINGLE_SUCCESS : 0;
    }
}
