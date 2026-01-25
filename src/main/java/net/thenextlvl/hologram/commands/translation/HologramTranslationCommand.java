package net.thenextlvl.hologram.commands.translation;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.suggestions.TranslationKeySuggestionProvider;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramTranslationCommand extends BrigadierCommand {
    private HologramTranslationCommand(final HologramPlugin plugin) {
        super(plugin, "translation", "holograms.command.translation");
    }

    public static LiteralCommandNode<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramTranslationCommand(plugin);
        return command.create()
                .then(HologramTranslationAddCommand.create(plugin))
                .then(HologramTranslationListCommand.create(plugin))
                .then(HologramTranslationReloadCommand.create(plugin))
                .then(HologramTranslationRemoveCommand.create(plugin))
                .build();
    }

    static RequiredArgumentBuilder<CommandSourceStack, String> translationKeyArgument(final HologramPlugin plugin) {
        return Commands.argument("translation key", StringArgumentType.word())
                .suggests(new TranslationKeySuggestionProvider<>(plugin));
    }
}
