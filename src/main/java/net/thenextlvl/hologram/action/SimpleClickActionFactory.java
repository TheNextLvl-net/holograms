package net.thenextlvl.hologram.action;

import net.thenextlvl.hologram.HologramPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.EnumSet;
import java.util.function.Consumer;

@NullMarked
public final class SimpleClickActionFactory implements ClickActionFactory {
    private final HologramPlugin plugin;

    public SimpleClickActionFactory(final HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public <T> ClickAction<T> create(final ActionType<T> actionType, final EnumSet<ClickType> clickTypes, final T input) {
        return new SimpleClickAction<>(plugin, actionType, clickTypes, input);
    }

    @Override
    public <T> ClickAction<T> create(final ActionType<T> actionType, final EnumSet<ClickType> clickTypes, final T input, final Consumer<ClickAction<T>> configurator) {
        final var action = create(actionType, clickTypes, input);
        configurator.accept(action);
        return action;
    }
}
