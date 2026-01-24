package net.thenextlvl.hologram.commands.translation;

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

import static net.thenextlvl.hologram.commands.translation.HologramTranslationCommand.keyArgument;

@NullMarked
public final class HologramTranslationRemoveCommand extends SimpleCommand {
    private HologramTranslationRemoveCommand(HologramPlugin plugin) {
        super(plugin, "remove", "holograms.command.translation.remove");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramTranslationRemoveCommand(plugin);
        var locale = Commands.argument("locale", new LocaleArgumentType(plugin, true));
        return command.create().then(keyArgument(plugin).then(locale.executes(command)));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var sender = context.getSource().getSender();
        var locale = context.getArgument("locale", Locale.class);
        var key = context.getArgument("key", String.class);

        var success = plugin.translations().contains(key, locale);
        if (success) {
            plugin.translations().unregister(key, locale);
            plugin.updateHologramTextLines(null);
            plugin.translations().save(locale);
        }

        var message = success ? "hologram.translation.removed" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("locale", LanguageTags.getLanguageName(locale)),
                Placeholder.parsed("key", key));
        return success ? SINGLE_SUCCESS : 0;
    }
}
