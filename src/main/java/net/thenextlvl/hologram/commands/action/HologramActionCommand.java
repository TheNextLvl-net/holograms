package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramActionCommand {
    public static RequiredArgumentBuilder<CommandSourceStack, Integer> create(
            final HologramPlugin plugin,
            final RequiredArgumentBuilder<CommandSourceStack, Integer> line,
            final ActionTargetResolver.Builder resolver
    ) {
        return line.then(ActionAddCommand.create(plugin, resolver))
                .then(ActionChanceCommand.create(plugin, resolver))
                .then(ActionCooldownCommand.create(plugin, resolver))
                .then(ActionListCommand.create(plugin, resolver))
                .then(ActionPermissionCommand.create(plugin, resolver))
                .then(ActionRemoveCommand.create(plugin, resolver));
    }
}
