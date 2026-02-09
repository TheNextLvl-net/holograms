package net.thenextlvl.hologram.action;

import net.kyori.adventure.key.KeyPattern;
import net.thenextlvl.hologram.line.HologramLine;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

/**
 * Represents a type of action that can be performed on a hologram line.
 *
 * @since 0.6.0
 */
public sealed interface ActionType<T> permits SimpleActionType {
    /**
     * Gets the type of input this action type requires.
     *
     * @return input type
     * @since 0.6.0
     */
    @Contract(pure = true)
    Class<T> type();

    /**
     * Gets the name of this action type.
     *
     * @return action type name
     * @since 0.6.0
     */
    @KeyPattern.Value
    @Contract(pure = true)
    String name();

    /**
     * Gets the action of this action type.
     *
     * @return action
     * @since 0.6.0
     */
    @Contract(pure = true)
    Action<T> action();

    /**
     * Represents an action that is performed on a hologram line.
     *
     * @since 0.6.0
     */
    @FunctionalInterface
    interface Action<T> {
        void invoke(HologramLine line, Player player, T input);
    }

    /**
     * Creates a new action type with the given name, type, and action.
     *
     * @param name   action type name
     * @param type   action type input type
     * @param action action
     * @param <T>    action type input type
     * @return new action type
     * @since 0.6.0
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static <T> ActionType<T> create(@KeyPattern.Value final String name, final Class<T> type, final Action<T> action) {
        return new SimpleActionType<>(name, type, action);
    }
}
