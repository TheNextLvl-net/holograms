package net.thenextlvl.hologram.economy;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;

@NullMarked
public interface EconomyProvider {
    default String format(final Audience audience, final double amount) {
        return format(audience.get(Identity.LOCALE).orElse(Locale.US), amount);
    }

    default String format(final Locale locale, final double amount) {
        return String.format(locale, "%.2f", amount);
    }

    boolean withdraw(Player player, double amount);
}
