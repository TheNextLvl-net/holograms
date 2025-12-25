package net.thenextlvl.hologram.line;

import net.thenextlvl.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * Represents a line within a hologram.
 *
 * @since 0.1.0
 */
public interface HologramLine<E extends Entity> {
    @Contract(pure = true)
    Hologram getHologram();

    @Contract(pure = true)
    Optional<E> getEntity();

    @Contract(pure = true)
    <T> Optional<T> getEntity(Class<T> type);

    @Contract(pure = true)
    Class<E> getTypeClass();

    @Contract(pure = true)
    EntityType getEntityType();

    @Contract(pure = true)
    LineType getType();

    @Contract(pure = true)
    Location getLocation();

    @Contract(pure = true)
    World getWorld();

    @Contract(mutates = "this")
    void despawn();
    @Contract(mutates = "this")
    void spawn();
}
