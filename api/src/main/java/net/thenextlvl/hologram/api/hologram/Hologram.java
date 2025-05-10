package net.thenextlvl.hologram.api.hologram;

import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * An interface that represents a hologram
 */
@NullMarked
public interface Hologram extends Display {
    @Nullable
    String getViewPermission();

    @Unmodifiable
    Set<UUID> getViewers();

    boolean addViewer(UUID player);

    boolean addViewers(Collection<UUID> players);

    boolean canSee(Player player);

    boolean isTrackedBy(Player player);

    boolean isViewer(UUID player);

    boolean removeViewer(UUID player);

    boolean removeViewers(Collection<UUID> players);

    boolean setViewPermission(@Nullable String permission);

    void delete();
}
