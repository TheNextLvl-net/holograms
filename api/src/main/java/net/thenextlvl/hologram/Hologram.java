package net.thenextlvl.hologram;

import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * An interface that represents a hologram
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface Hologram extends Iterable<HologramLine> {
    /**
     * Returns the name of the hologram.
     *
     * @return hologram name
     * @since 0.1.0
     */
    @Contract(pure = true)
    String getName();

    /**
     * Sets the name of the hologram.
     * <p>
     * Calling this method involves renaming the hologram's data files.
     *
     * @param name new hologram name
     * @return {@code true} if the name was changed, {@code false} otherwise
     * @since 0.3.0
     */
    @Contract(mutates = "this,io")
    boolean setName(String name);

    /**
     * Returns the location of the hologram.
     *
     * @return the location of the hologram
     * @since 0.3.0
     */
    @Contract(value = " -> new", pure = true)
    Location getLocation();

    /**
     * Returns the world of the hologram.
     *
     * @return the world of the hologram
     * @since 0.3.0
     */
    @Contract(pure = true)
    World getWorld();

    /**
     * Teleports the hologram to the given location.
     * <p>
     * Calling this method may involve moving the hologram's data files.
     *
     * @param location new hologram location
     * @return {@link CompletableFuture} that completes with {@code true}
     * if the hologram was teleported, {@code false} otherwise
     * @since 0.3.0
     */
    @Contract(mutates = "this,io")
    CompletableFuture<Boolean> teleportAsync(Location location);

    /**
     * Returns all lines of this hologram.
     *
     * @return a stream of hologram lines
     * @since 0.4.0
     */
    @Contract(pure = true)
    Stream<HologramLine> getLines();

    /**
     * Returns the number of lines in this hologram.
     *
     * @return number of lines
     * @since 0.1.0
     */
    @Contract(pure = true)
    int getLineCount();

    /**
     * Returns the line at the given index.
     *
     * @param index line index
     * @return line at the given index
     * @since 0.4.0
     */
    @Contract(pure = true)
    Optional<HologramLine> getLine(int index);

    /**
     * Returns the line at the given index if the line is of the given type.
     *
     * @param index line index
     * @param type  line type
     * @param <T>   line type
     * @return an optional containing the line at the given index of the given type
     * @since 0.4.0
     */
    @Contract(pure = true)
    <T extends HologramLine> Optional<T> getLine(int index, Class<T> type);

    /**
     * Returns the index of the given line.
     *
     * @param line the line to find the index of
     * @return index of the given line or {@code -1} if the line is not found
     * @see java.util.List#indexOf(Object)
     * @since 0.4.0
     */
    @Contract(pure = true)
    int getLineIndex(HologramLine line);

    /**
     * Removes the given line from this hologram.
     *
     * @param line the line to remove
     * @return {@code true} if the line was removed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean removeLine(HologramLine line);

    /**
     * Removes the line at the given index from this hologram.
     *
     * @param index the index of the line to remove
     * @return {@code true} if the line was removed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean removeLine(int index);

    /**
     * Removes all the given lines from this hologram.
     *
     * @param lines the lines to remove
     * @return {@code true} if any lines were removed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean removeLines(Collection<HologramLine> lines);

    /**
     * Removes all lines from this hologram.
     *
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    void clearLines();

    /**
     * Checks if the given line is in this hologram.
     *
     * @param line the line to check
     * @return {@code true} if this hologram has the given line, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean hasLine(HologramLine line);

    /**
     * Moves the line at the given index to the given index.
     *
     * @param from source index
     * @param to   destination index
     * @return {@code true} if the line was moved, {@code false} otherwise
     * @since 0.3.0
     */
    @Contract(mutates = "this")
    boolean moveLine(int from, int to);

    /**
     * Swaps the lines at the given indices.
     *
     * @param line1 first line index
     * @param line2 second line index
     * @return {@code true} if the lines were swapped, {@code false} otherwise
     * @since 0.3.0
     */
    @Contract(mutates = "this")
    boolean swapLines(int line1, int line2);

    /**
     * Adds an entity line to this hologram.
     *
     * @param entityType entity type
     * @return a new entity hologram line
     * @throws IllegalArgumentException if the entity type is not spawnable
     * @since 0.3.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    EntityHologramLine addEntityLine(EntityType entityType) throws IllegalArgumentException;

    /**
     * Adds an entity line to this hologram.
     *
     * @param entityType entity type
     * @return a new entity hologram line
     * @throws IllegalArgumentException if the entity type is not spawnable
     * @since 0.1.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    EntityHologramLine addEntityLine(Class<? extends Entity> entityType) throws IllegalArgumentException;

    /**
     * Adds an entity line to this hologram at the given index.
     *
     * @param entityType entity type
     * @param index      line index
     * @return a new entity hologram line
     * @throws IllegalArgumentException  if the entity type is not spawnable
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @since 0.3.0
     */
    @Contract(value = "_, _ -> new", mutates = "this")
    EntityHologramLine addEntityLine(EntityType entityType, int index) throws IllegalArgumentException, IndexOutOfBoundsException;

    /**
     * Adds an entity line to this hologram at the given index.
     *
     * @param entityType entity type
     * @param index      line index
     * @return a new entity hologram line
     * @throws IllegalArgumentException  if the entity type is not spawnable
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @since 0.1.0
     */
    @Contract(value = "_, _ -> new", mutates = "this")
    EntityHologramLine addEntityLine(Class<? extends Entity> entityType, int index) throws IllegalArgumentException, IndexOutOfBoundsException;

    /**
     * Adds a block line to this hologram.
     *
     * @return a new block hologram line
     * @since 0.1.0
     */
    @Contract(value = " -> new", mutates = "this")
    BlockHologramLine addBlockLine();

    /**
     * Adds a block line to this hologram at the given index.
     *
     * @param index line index
     * @return a new block hologram line
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @since 0.1.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    BlockHologramLine addBlockLine(int index) throws IndexOutOfBoundsException;

    /**
     * Adds an item line to this hologram.
     *
     * @return a new item hologram line
     * @since 0.1.0
     */
    @Contract(value = " -> new", mutates = "this")
    ItemHologramLine addItemLine();

    /**
     * Adds an item line to this hologram at the given index.
     *
     * @param index line index
     * @return a new item hologram line
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @since 0.1.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    ItemHologramLine addItemLine(int index) throws IndexOutOfBoundsException;

    /**
     * Adds a text line to this hologram.
     *
     * @return a new text hologram line
     * @since 0.1.0
     */
    @Contract(value = " -> new", mutates = "this")
    TextHologramLine addTextLine();

    /**
     * Adds a text line to this hologram at the given index.
     *
     * @param index line index
     * @return a new text hologram line
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @since 0.1.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    TextHologramLine addTextLine(int index) throws IndexOutOfBoundsException;

    /**
     * Adds a paged line to this hologram.
     * A paged line can contain multiple pages of different line types
     * that cycle through at a configurable interval.
     *
     * @return a new paged hologram line
     * @since 0.5.0
     */
    @Contract(value = " -> new", mutates = "this")
    PagedHologramLine addPagedLine();

    /**
     * Adds a paged line to this hologram at the given index.
     *
     * @param index line index
     * @return a new paged hologram line
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @since 0.5.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    PagedHologramLine addPagedLine(int index) throws IndexOutOfBoundsException;

    /**
     * Sets a paged line at the given index.
     *
     * @param index line index
     * @return a new paged hologram line
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @since 0.5.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    PagedHologramLine setPagedLine(int index) throws IndexOutOfBoundsException;

    /**
     * Sets an entity line at the given index.
     *
     * @param entityType entity type
     * @param index      line index
     * @return a new entity hologram line
     * @throws IllegalArgumentException  if the entity type is not spawnable
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @since 0.3.0
     */
    @Contract(value = "_, _ -> new", mutates = "this")
    EntityHologramLine setEntityLine(EntityType entityType, int index) throws IllegalArgumentException, IndexOutOfBoundsException;

    /**
     * Sets an entity line at the given index.
     *
     * @param entityType entity type
     * @param index      line index
     * @return a new entity hologram line
     * @throws IllegalArgumentException  if the entity type is not spawnable
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @since 0.3.0
     */
    @Contract(value = "_, _ -> new", mutates = "this")
    EntityHologramLine setEntityLine(Class<? extends Entity> entityType, int index) throws IllegalArgumentException, IndexOutOfBoundsException;

    /**
     * Sets a block line at the given index.
     *
     * @param index line index
     * @return a new block hologram line
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @since 0.3.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    BlockHologramLine setBlockLine(int index) throws IndexOutOfBoundsException;

    /**
     * Sets an item line at the given index.
     *
     * @param index line index
     * @return a new item hologram line
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @since 0.3.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    ItemHologramLine setItemLine(int index) throws IndexOutOfBoundsException;

    /**
     * Sets a text line at the given index.
     *
     * @param index line index
     * @return a new text hologram line
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @since 0.3.0
     */
    @Contract(value = "_ -> new", mutates = "this")
    TextHologramLine setTextLine(int index) throws IndexOutOfBoundsException;

    /**
     * Returns the view permission of this hologram.
     *
     * @return view permission
     * @since 0.4.0
     */
    @Contract(pure = true)
    Optional<String> getViewPermission();

    /**
     * Sets the view permission of this hologram.
     *
     * @param permission view permission
     * @return {@code true} if the view permission was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setViewPermission(@Nullable String permission);

    /**
     * Returns the viewers of this hologram.
     *
     * @return viewers
     * @since 0.1.0
     */
    @Unmodifiable
    @Contract(pure = true)
    Set<UUID> getViewers();

    /**
     * Adds a viewer to this hologram.
     *
     * @param player viewer
     * @return {@code true} if the viewer was added, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean addViewer(UUID player);

    /**
     * Adds viewers to this hologram.
     *
     * @param players viewers
     * @return {@code true} if any viewers were added, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean addViewers(Collection<UUID> players);

    /**
     * Removes a viewer from this hologram.
     *
     * @param player viewer
     * @return {@code true} if the viewer was removed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean removeViewer(UUID player);

    /**
     * Removes viewers from this hologram.
     *
     * @param players viewers
     * @return {@code true} if any viewers were removed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean removeViewers(Collection<UUID> players);

    /**
     * Checks if this hologram has the given viewer.
     *
     * @param player viewer
     * @return {@code true} if this hologram has the given viewer, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isViewer(UUID player);

    /**
     * Checks if the given player can see this hologram.
     * <p>
     * This method is different to {@link #isSpawned(Player)} in that it checks
     * if the player has the permission to see the hologram.
     *
     * @param player player
     * @return {@code true} if the given player can see this hologram, {@code false} otherwise
     * @see #getViewPermission()
     * @see #isVisibleByDefault()
     * @see #isViewer(UUID)
     * @see #isSpawned(Player)
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean canSee(Player player);

    /**
     * Checks if this hologram is visible by default.
     *
     * @return {@code true} if this hologram is visible by default, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isVisibleByDefault();

    /**
     * Sets whether this hologram is visible by default.
     *
     * @param visible {@code true} if this hologram should be visible by default, {@code false} otherwise
     * @return {@code true} if the visibility was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setVisibleByDefault(boolean visible);

    /**
     * Sets whether this hologram is persistent.
     *
     * @param persistent {@code true} if this hologram should be persistent, {@code false} otherwise
     * @return {@code true} if the persistence was changed, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(mutates = "this")
    boolean setPersistent(boolean persistent);

    /**
     * Checks if this hologram is persistent.
     *
     * @return {@code true} if this hologram is persistent, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isPersistent();

    /**
     * Persists this hologram.
     *
     * @return {@code true} if the hologram was persisted, {@code false} otherwise
     * @since 0.3.0
     */
    @Contract(mutates = "io")
    boolean persist();

    /**
     * Returns the data file of this hologram.
     *
     * @return data file
     * @since 0.3.0
     */
    @Contract(pure = true)
    Path getDataFile();

    /**
     * Returns the backup file of this hologram.
     *
     * @return backup file
     * @since 0.3.0
     */
    @Contract(pure = true)
    Path getBackupFile();

    /**
     * Spawns this hologram for all players.
     *
     * @since 0.4.0
     */
    @Contract(mutates = "this")
    void spawn();

    /**
     * Spawns this hologram for the given player.
     *
     * @param player the player to spawn for
     * @return {@code true} if the hologram was spawned for the given player, {@code false} otherwise
     * @since 0.4.0
     */
    @Contract(mutates = "this")
    boolean spawn(Player player);

    /**
     * Despawns this hologram for all players.
     *
     * @since 0.4.0
     */
    @Contract(mutates = "this")
    void despawn();

    /**
     * Despawns this hologram for the given player.
     *
     * @param player the player to despawn for
     * @return {@code true} if the hologram was despawned for the given player, {@code false} otherwise
     * @since 0.4.0
     */
    @Contract(mutates = "this")
    boolean despawn(Player player);

    /**
     * Checks if this hologram is spawned for the given player.
     *
     * @param player the player to check for
     * @return {@code true} if this hologram is spawned for the given player, {@code false} otherwise
     * @since 0.4.0
     */
    @Contract(pure = true)
    boolean isSpawned(Player player);
}
