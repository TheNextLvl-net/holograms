package net.thenextlvl.hologram.economy;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

@NullMarked
public interface EconomyProvider {
    default Component format(final Audience audience, @Nullable final String currency, final double amount) {
        return format(audience.get(Identity.LOCALE).orElse(Locale.US), currency, amount);
    }

    Component format(final Locale locale, @Nullable final String currency, final double amount);

    boolean withdraw(Player player, @Nullable final String currency, double amount);
}
