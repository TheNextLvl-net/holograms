package net.thenextlvl.hologram.economy;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class EmptyEconomyProvider implements EconomyProvider {
    @Override
    public boolean withdraw(final Player player, final double amount) {
        return false;
    }
}
