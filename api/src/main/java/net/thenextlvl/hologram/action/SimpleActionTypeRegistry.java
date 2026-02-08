package net.thenextlvl.hologram.action;

import org.jetbrains.annotations.Unmodifiable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

final class SimpleActionTypeRegistry implements ActionTypeRegistry {
    public static final ActionTypeRegistry INSTANCE = new SimpleActionTypeRegistry();

    private final Set<ActionType<?>> actionTypes = new HashSet<>(Set.of(
            ActionTypes.types().connect(),
            ActionTypes.types().cyclePage(),
            ActionTypes.types().playSound(),
            ActionTypes.types().runCommand(),
            ActionTypes.types().runConsoleCommand(),
            ActionTypes.types().sendActionbar(),
            ActionTypes.types().sendMessage(),
            ActionTypes.types().sendTitle(),
            ActionTypes.types().setPage(),
            ActionTypes.types().teleport(),
            ActionTypes.types().transfer()
    ));

    @Override
    public boolean register(final ActionType<?> type) {
        return !isRegistered(type.name()) && actionTypes.add(type);
    }

    @Override
    public boolean isRegistered(final ActionType<?> type) {
        return actionTypes.contains(type);
    }

    @Override
    public boolean isRegistered(final String name) {
        return actionTypes.stream().anyMatch(actionType -> actionType.name().equals(name));
    }

    @Override
    public boolean unregister(final ActionType<?> type) {
        return actionTypes.remove(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<ActionType<T>> getByName(final String name) {
        return actionTypes.stream()
                .filter(actionType -> actionType.name().equals(name))
                .map(actionType -> (ActionType<T>) actionType)
                .findAny();
    }

    @Override
    public @Unmodifiable Set<ActionType<?>> getActionTypes() {
        return Set.copyOf(actionTypes);
    }
}
