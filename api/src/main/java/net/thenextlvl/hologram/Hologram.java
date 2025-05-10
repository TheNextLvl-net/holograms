package net.thenextlvl.hologram;

import net.thenextlvl.hologram.display.HologramDisplay;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * An interface that represents a hologram
 */
@NullMarked
public interface Hologram<E extends Display> extends HologramDisplay {
    Optional<E> getEntity();
    <T extends Display> Optional<T> getEntity(Class<T> type);

    Class<E> getTypeClass();
    EntityType getType();

    String getName();

    @Nullable
    Location getLocation();

    @Nullable
    Location getSpawnLocation();
    boolean setSpawnLocation(@Nullable Location location);

    @Nullable
    World getWorld();

    @Nullable
    String getViewPermission();

    boolean setViewPermission(@Nullable String permission);

    @Unmodifiable
    Set<UUID> getViewers();

    boolean addViewer(UUID player);
    boolean addViewers(Collection<UUID> players);
    boolean removeViewer(UUID player);
    boolean removeViewers(Collection<UUID> players);
    boolean isViewer(UUID player);
    boolean canSee(Player player);
    boolean isTrackedBy(Player player);

    boolean isVisibleByDefault();
    boolean setVisibleByDefault(boolean visible);

    boolean setPersistent(boolean persistent);
    boolean isPersistent();
    boolean persist();

    void delete();

    boolean spawn();
    boolean spawn(Location location);
    boolean isSpawned();
    boolean despawn();
}
