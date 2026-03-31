package net.thenextlvl.hologram.economy;

import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

@NullMarked
public final class VaultEconomyProvider implements EconomyProvider {
    private final Plugin plugin;

    public VaultEconomyProvider(final Plugin plugin) {
        this.plugin = plugin;
    }

    private Optional<Economy> getEconomy() {
        return Optional.ofNullable(plugin.getServer().getServicesManager().load(Economy.class));
    }

    @Override
    public Component format(final Locale locale, @Nullable final String currency, final double amount) {
        return Component.text(getEconomy().map(economy -> economy.format(amount))
                .orElseGet(() -> String.format(locale, "%.2f", amount)));
    }

    @Override
    public boolean withdraw(final Player player, @Nullable final String currency, final double amount) {
        return getEconomy().map(economy -> {
            return economy.withdrawPlayer(player, amount).transactionSuccess();
        }).orElse(false);
    }
}
