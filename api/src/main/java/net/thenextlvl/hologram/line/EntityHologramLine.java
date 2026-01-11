package net.thenextlvl.hologram.line;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents a line type within a hologram that displays an entity.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface EntityHologramLine<E extends Entity> extends HologramLine<E> {
    @Contract(pure = true)
    double getScale();

    @Contract(value = "_ -> this", mutates = "this")
    EntityHologramLine<E> setScale(double scale);
}
