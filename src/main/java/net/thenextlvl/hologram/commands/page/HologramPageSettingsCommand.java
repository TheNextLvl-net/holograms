package net.thenextlvl.hologram.commands.page;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.page.settings.HologramPageSettingsIntervalCommand;
import net.thenextlvl.hologram.commands.page.settings.HologramPageSettingsPauseCommand;
import net.thenextlvl.hologram.commands.page.settings.HologramPageSettingsRandomCommand;
import net.thenextlvl.hologram.commands.suggestions.LineSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
public final class HologramPageSettingsCommand extends BrigadierCommand {
    private HologramPageSettingsCommand(final HologramPlugin plugin) {
        super(plugin, "settings", "holograms.command.page.settings");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageSettingsCommand(plugin);
        final var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.INSTANCE);

        return command.create().then(hologramArgument(plugin).then(line
                .then(HologramPageSettingsIntervalCommand.create(plugin))
                .then(HologramPageSettingsPauseCommand.create(plugin))
                .then(HologramPageSettingsRandomCommand.create(plugin))));
    }
}
