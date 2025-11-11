package net.thenextlvl.hologram.line;

import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

/**
 * Represents a line type within a hologram that displays an entity.
 *
 * @since 0.1.0
 */
@NullMarked
public interface EntityHologramLine<E extends Entity> extends HologramLine<E> {
    double getScale();

    void setScale(double scale);
}
