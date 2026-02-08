package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramActionEditCommand {
    public static RequiredArgumentBuilder<CommandSourceStack, Integer> create(
            final HologramPlugin plugin,
            final RequiredArgumentBuilder<CommandSourceStack, Integer> line,
            final ActionTargetResolver.Builder resolver
    ) {
        return line.then(HologramActionAddCommand.create(plugin, resolver))
                .then(HologramActionChanceCommand.create(plugin, resolver))
                .then(HologramActionCooldownCommand.create(plugin, resolver))
                .then(HologramActionListCommand.create(plugin, resolver))
                .then(HologramActionPermissionCommand.create(plugin, resolver))
                .then(HologramActionRemoveCommand.create(plugin, resolver));
    }
}
