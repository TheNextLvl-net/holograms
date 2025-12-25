package net.thenextlvl.hologram.line;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * Represents a line type within a hologram that displays an entity.
 *
 * @since 0.1.0
 */
public interface EntityHologramLine<E extends Entity> extends HologramLine<E> {
    @Contract(pure = true)
    double getScale();

    @Contract(mutates = "this")
    void setScale(double scale);
}
