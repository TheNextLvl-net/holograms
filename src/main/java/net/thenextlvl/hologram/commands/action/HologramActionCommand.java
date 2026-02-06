package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.suggestions.HologramActionSuggestionProvider;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramActionCommand extends BrigadierCommand {
    private HologramActionCommand(final HologramPlugin plugin) {
        super(plugin, "action", "holograms.command.action");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        // todo: this also has to work on a page level
        return new HologramActionCommand(plugin).create()
                .then(HologramActionAddCommand.create(plugin))
                .then(HologramActionChanceCommand.create(plugin))
                .then(HologramActionCooldownCommand.create(plugin))
                .then(HologramActionListCommand.create(plugin))
                .then(HologramActionPermissionCommand.create(plugin))
                .then(HologramActionRemoveCommand.create(plugin));
    }

    static ArgumentBuilder<CommandSourceStack, ?> actionArgument(final HologramPlugin plugin) {
        return Commands.argument("action", StringArgumentType.word())
                .suggests(new HologramActionSuggestionProvider());
    }
}
