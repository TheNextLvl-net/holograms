package net.thenextlvl.hologram.economy;

import net.kyori.adventure.text.Component;
import net.thenextlvl.service.economy.EconomyController;
import net.thenextlvl.service.economy.currency.Currency;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

@NullMarked
public final class ServiceEconomyProvider implements EconomyProvider {
    private final Plugin plugin;

    public ServiceEconomyProvider(final Plugin plugin) {
        this.plugin = plugin;
    }

    private Optional<EconomyController> getController() {
        return Optional.ofNullable(plugin.getServer().getServicesManager().load(EconomyController.class));
    }

    private Optional<Currency> getCurrency(@Nullable final String name) {
        return getController().map(controller -> getCurrency(controller, name));
    }

    private Currency getCurrency(final EconomyController controller, @Nullable final String name) {
        final var currencyController = controller.getCurrencyController();
        if (name == null) return currencyController.getDefaultCurrency();
        return currencyController.getCurrency(name).orElseGet(currencyController::getDefaultCurrency);
    }

    @Override
    public Component format(final Locale locale, @Nullable final String currency, final double amount) {
        return getCurrency(currency).map(c -> c.format(amount, locale))
                .orElseGet(() -> Component.text(String.format(locale, "%.2f", amount)));
    }

    @Override
    public boolean withdraw(final Player player, @Nullable final String currency, final double amount) {
        return getController().flatMap(controller -> controller.getAccount(player).map(account -> {
            return account.withdraw(amount, getCurrency(controller, currency)).successful();
        })).orElse(false);
    }
}
