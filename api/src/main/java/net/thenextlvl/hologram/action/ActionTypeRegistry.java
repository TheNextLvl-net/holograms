package net.thenextlvl.hologram.action;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;

/**
 * @since 0.6.0
 */
public sealed interface ActionTypeRegistry permits SimpleActionTypeRegistry {
    @Contract(pure = true)
    static ActionTypeRegistry registry() {
        return SimpleActionTypeRegistry.INSTANCE;
    }

    @Contract(mutates = "this")
    boolean register(ActionType<?> type);

    @Contract(pure = true)
    boolean isRegistered(ActionType<?> type);

    @Contract(pure = true)
    boolean isRegistered(String name);

    @Contract(mutates = "this")
    boolean unregister(ActionType<?> type);

    @Contract(pure = true)
    <T> Optional<ActionType<T>> getByName(String name);

    @Unmodifiable
    @Contract(pure = true)
    Set<ActionType<?>> getActionTypes();
}
