package net.thenextlvl.hologram.action;

import net.kyori.adventure.key.KeyPattern;
import net.thenextlvl.hologram.line.HologramLine;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

/**
 * @since 0.6.0
 */
public sealed interface ActionType<T> permits SimpleActionType {
    @Contract(pure = true)
    Class<T> type();

    @KeyPattern.Value
    @Contract(pure = true)
    String name();

    @Contract(pure = true)
    Action<T> action();

    @FunctionalInterface
    interface Action<T> {
        void invoke(HologramLine line, Player player, T input);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static <T> ActionType<T> create(@KeyPattern.Value final String name, final Class<T> type, final Action<T> action) {
        return new SimpleActionType<>(name, type, action);
    }
}
