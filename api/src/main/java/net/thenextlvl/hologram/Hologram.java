package net.thenextlvl.hologram;

import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * An interface that represents a hologram
 * 
 * @since 0.1.0
 */
@NullMarked
public interface Hologram extends Iterable<HologramLine<?>> {
    String getName();

    Location getLocation();
    World getWorld();

    CompletableFuture<Boolean> teleportAsync(Location location);

    @Unmodifiable
    List<HologramLine<?>> getLines();

    int getLineCount();
    @Nullable HologramLine<?> getLine(int index) throws IndexOutOfBoundsException;
    int getLineIndex(HologramLine<?> line);
    boolean removeLine(HologramLine<?> line);
    boolean removeLine(int index) throws IndexOutOfBoundsException;
    boolean removeLines(Collection<HologramLine<?>> lines);
    void clearLines();
    boolean hasLine(HologramLine<?> line);

    EntityHologramLine<?> addEntityLine(EntityType entityType) throws IllegalArgumentException;
    <T extends Entity> EntityHologramLine<T> addEntityLine(Class<T> entityType) throws IllegalArgumentException;
    <T extends Entity> EntityHologramLine<T> addEntityLine(Class<T> entityType, int index) throws IllegalArgumentException;
    BlockHologramLine addBlockLine();
    BlockHologramLine addBlockLine(int index);
    ItemHologramLine addItemLine();
    ItemHologramLine addItemLine(int index);
    TextHologramLine addTextLine();
    TextHologramLine addTextLine(int index);

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
    
    @Contract(mutates = "io")
    boolean persist();

    /**
     * @since 0.2.0
     */
    Path getDataFile();
    /**
     * @since 0.2.0
     */
    Path getBackupFile();

    boolean spawn();
    void despawn();
    boolean isSpawned();
}
