package net.thenextlvl.hologram.plugin.commands;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CommandItems {
    private CommandItems() {
        throw new UnsupportedOperationException();
    }

    public static ItemStack getHeldItem(final CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof final Player player))
            throw new IllegalStateException("Command executor must be a player");

        final var item = player.getInventory().getItemInMainHand();
        if (!item.isEmpty()) return item;
        return player.getInventory().getItemInOffHand();
    }
}
