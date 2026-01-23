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

import static net.thenextlvl.hologram.commands.translation.HologramTranslationCommand.keyArgument;

@NullMarked
public final class HologramTranslationAddCommand extends SimpleCommand {
    private HologramTranslationAddCommand(HologramPlugin plugin) {
        super(plugin, "add", "holograms.command.translation.add");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramTranslationAddCommand(plugin);
        var locale = Commands.argument("locale", new LocaleArgumentType(plugin, false));
        var translation = Commands.argument("translation", StringArgumentType.greedyString());
        return command.create().then(keyArgument(plugin)
                .then(locale.then(translation.executes(command))));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var sender = context.getSource().getSender();
        var locale = context.getArgument("locale", Locale.class);
        var key = context.getArgument("key", String.class);
        var translation = context.getArgument("translation", String.class);

        var success = plugin.translations().override(key, locale, translation);

        var message = success ? "hologram.translation.added" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("locale", LanguageTags.getLanguageName(locale)),
                Placeholder.parsed("key", key));
        return success ? SINGLE_SUCCESS : 0;
    }
}
