package net.thenextlvl.hologram.commands.action;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class HologramActionCommand {
    @FunctionalInterface
    public interface ArgumentChainFactory {
        ArgumentChain create();
    }

    public record ArgumentChain(
            ArgumentBuilder<CommandSourceStack, ?> hologram,
            ArgumentBuilder<CommandSourceStack, ?> line,
            @Nullable ArgumentBuilder<CommandSourceStack, ?> page
    ) {
        public ArgumentBuilder<CommandSourceStack, ?> tail() {
            return page != null ? page : line;
        }

        public ArgumentBuilder<CommandSourceStack,?> build() {
            if (page != null) line.then(page);
            return hologram.then(line);
        }
    }

    public static void register(
            final HologramPlugin plugin,
            final LiteralArgumentBuilder<CommandSourceStack> parent,
            final ArgumentChainFactory chainFactory,
            final ActionTargetResolver.Builder resolver
    ) {
        parent.then(ActionAddCommand.create(plugin, chainFactory, resolver))
                .then(ActionChanceCommand.create(plugin, chainFactory, resolver))
                .then(ActionCooldownCommand.create(plugin, chainFactory, resolver))
                .then(ActionCostCommand.create(plugin, chainFactory, resolver))
                .then(ActionListCommand.create(plugin, chainFactory, resolver))
                .then(ActionPermissionCommand.create(plugin, chainFactory, resolver))
                .then(ActionRemoveCommand.create(plugin, chainFactory, resolver));
    }
}
