package net.thenextlvl.hologram.line;

import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface EntityHologramLine<E extends Entity> extends HologramLine<E> {
    double getScale();

    void setScale(double scale);
}
