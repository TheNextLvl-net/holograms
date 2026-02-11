package net.thenextlvl.hologram.economy;

import net.thenextlvl.hologram.HologramPlugin;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class EmptyEconomyProvider implements EconomyProvider {
    private final HologramPlugin plugin;
    
    public EmptyEconomyProvider(final HologramPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean withdraw(final Player player, final double amount) {
        if (amount == 0) return true;
        return false;
    }
}
