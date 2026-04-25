package net.thenextlvl.hologram.economy;

import net.kyori.adventure.text.Component;
import net.thenextlvl.hologram.HologramPlugin;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

@NullMarked
public final class EmptyEconomyProvider implements EconomyProvider {
    private final HologramPlugin plugin;

    public EmptyEconomyProvider(final HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Component format(final Locale locale, @Nullable final String currency, final double amount) {
        return Component.text(String.format(locale, "%.2f", amount));
    }

    @Override
    public boolean withdraw(final Player player, @Nullable final String currency, final double amount) {
        if (amount == 0) return true;
        plugin.getComponentLogger().warn("No economy provider installed, cannot withdraw money");
        return false;
    }
}
