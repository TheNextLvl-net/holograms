package net.thenextlvl.hologram.action;

import org.jspecify.annotations.NullMarked;

import java.util.EnumSet;
import java.util.function.Consumer;

@NullMarked
public final class SimpleClickActionFactory implements ClickActionFactory {
    public static final ClickActionFactory INSTANCE = new SimpleClickActionFactory();

    @Override
    public <T> ClickAction<T> create(final ActionType<T> actionType, final EnumSet<ClickType> clickTypes, final T input) {
        return null;
    }

    @Override
    public <T> ClickAction<T> create(final ActionType<T> actionType, final EnumSet<ClickType> clickTypes, final T input, final Consumer<ClickAction<T>> configurator) {
        return null;
    }
}
