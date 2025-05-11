package net.thenextlvl.hologram.line;

import net.thenextlvl.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public interface HologramLine<E extends Entity> {
    Hologram getHologram();

    Optional<E> getEntity();

    <T> Optional<T> getEntity(Class<T> type);

    Class<E> getTypeClass();

    EntityType getEntityType();

    LineType getType();

    Location getLocation();

    World getWorld();

    void despawn();
    void spawn();
}
