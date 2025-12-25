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
public interface Hologram extends Iterable<HologramLine<?>> {
    @Contract(pure = true)
    String getName();

    /**
     * @since 0.2.0
     */
    @Contract(mutates = "this,io")
    boolean setName(String name);

    @Contract(pure = true)
    Location getLocation();
    @Contract(pure = true)
    World getWorld();
    
    @Contract(mutates = "this,io")
    CompletableFuture<Boolean> teleportAsync(Location location);

    @Unmodifiable
    @Contract(pure = true)
    List<HologramLine<?>> getLines();

    @Contract(pure = true)
    int getLineCount();
    @Contract(pure = true)
    @Nullable HologramLine<?> getLine(int index) throws IndexOutOfBoundsException;
    @Contract(pure = true)
    int getLineIndex(HologramLine<?> line);
    @Contract(mutates = "this")
    boolean removeLine(HologramLine<?> line);
    @Contract(mutates = "this")
    boolean removeLine(int index) throws IndexOutOfBoundsException;
    @Contract(mutates = "this")
    boolean removeLines(Collection<HologramLine<?>> lines);
    @Contract(mutates = "this")
    void clearLines();
    @Contract(pure = true)
    boolean hasLine(HologramLine<?> line);

    @Contract(value = "_ -> new", mutates = "this")
    EntityHologramLine<?> addEntityLine(EntityType entityType) throws IllegalArgumentException;
    @Contract(value = "_ -> new", mutates = "this")
    <T extends Entity> EntityHologramLine<T> addEntityLine(Class<T> entityType) throws IllegalArgumentException;
    @Contract(value = "_, _ -> new", mutates = "this")
    <T extends Entity> EntityHologramLine<T> addEntityLine(Class<T> entityType, int index) throws IllegalArgumentException;
    @Contract(value = " -> new", mutates = "this")
    BlockHologramLine addBlockLine();
    @Contract(value = "_ -> new", mutates = "this")
    BlockHologramLine addBlockLine(int index);
    @Contract(value = " -> new", mutates = "this")
    ItemHologramLine addItemLine();
    @Contract(value = "_ -> new", mutates = "this")
    ItemHologramLine addItemLine(int index);
    @Contract(value = " -> new", mutates = "this")
    TextHologramLine addTextLine();
    @Contract(value = "_ -> new", mutates = "this")
    TextHologramLine addTextLine(int index);

    @Nullable
    @Contract(pure = true)
    String getViewPermission();

    @Contract(mutates = "this")
    boolean setViewPermission(@Nullable String permission);

    @Unmodifiable
    @Contract(pure = true)
    Set<UUID> getViewers();

    @Contract(mutates = "this")
    boolean addViewer(UUID player);
    @Contract(mutates = "this")
    boolean addViewers(Collection<UUID> players);
    @Contract(mutates = "this")
    boolean removeViewer(UUID player);
    @Contract(mutates = "this")
    boolean removeViewers(Collection<UUID> players);
    @Contract(pure = true)
    boolean isViewer(UUID player);
    @Contract(pure = true)
    boolean canSee(Player player);
    @Contract(pure = true)
    boolean isTrackedBy(Player player);

    @Contract(pure = true)
    boolean isVisibleByDefault();
    @Contract(mutates = "this")
    boolean setVisibleByDefault(boolean visible);

    @Contract(mutates = "this")
    boolean setPersistent(boolean persistent);
    @Contract(pure = true)
    boolean isPersistent();
    
    @Contract(mutates = "io")
    boolean persist();

    /**
     * @since 0.2.0
     */
    @Contract(pure = true)
    Path getDataFile();
    /**
     * @since 0.2.0
     */
    @Contract(pure = true)
    Path getBackupFile();

    @Contract(mutates = "this")
    boolean spawn();
    @Contract(mutates = "this")
    void despawn();
    @Contract(pure = true)
    boolean isSpawned();
}
