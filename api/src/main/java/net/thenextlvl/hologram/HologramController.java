package net.thenextlvl.hologram;

import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Represents a controller responsible for managing, creating, and querying holograms and their associated lines.
 *
 * @since 0.1.0
 */
@NullMarked
public interface HologramController {
    /**
     * Get the data path for the given world
     *
     * @param world the world to get the data path for
     * @return the data path for the given world
     * @since 0.2.0
     */
    Path getDataPath(World world);

    /**
     * Get a hologram by its entity
     *
     * @param entity the entity of the hologram
     * @return the hologram that contains the given entity
     * @since 0.2.0
     */
    Optional<Hologram> getHologram(Entity entity);

    /**
     * Get a hologram line by its entity
     *
     * @param entity the entity of the hologram line
     * @param <E>    the type of the entity
     * @return the hologram line with the given entity
     * @since 0.1.0
     */
    <E extends Entity> Optional<HologramLine<E>> getHologramLine(E entity);

    /**
     * Get a block hologram line by its block display
     *
     * @param display the block display of the hologram line
     * @return the hologram line with the given display
     * @since 0.1.0
     */
    Optional<BlockHologramLine> getHologramLine(BlockDisplay display);

    /**
     * Get an item hologram line by its item display
     *
     * @param display the item display of the hologram line
     * @return the hologram line with the given display
     * @since 0.1.0
     */
    Optional<ItemHologramLine> getHologramLine(ItemDisplay display);

    /**
     * Get a text hologram line by its text display
     *
     * @param display the text display of the hologram line
     * @return the hologram line with the given display
     * @since 0.1.0
     */
    Optional<TextHologramLine> getHologramLine(TextDisplay display);

    /**
     * Get a hologram by its name
     *
     * @param name the name of the hologram
     * @return the hologram with the given name
     * @since 0.1.0
     */
    Optional<Hologram> getHologram(String name);

    /**
     * Get a hologram line by its UUID
     *
     * @param uuid the UUID of the hologram line
     * @return the hologram line with the given UUID
     * @since 0.1.0
     */
    Optional<HologramLine<?>> getHologramLine(UUID uuid);

    /**
     * Get all holograms
     *
     * @return all holograms
     * @since 0.2.0
     */
    Stream<Hologram> getHolograms();

    /**
     * Get all holograms in the given chunk
     *
     * @param chunk the chunk to check
     * @return all holograms in the given chunk
     * @since 0.2.0
     */
    Stream<Hologram> getHolograms(Chunk chunk);

    /**
     * Get all holograms visible to the given player
     *
     * @param player the player to check
     * @return all holograms visible to the given player
     * @since 0.2.0
     */
    Stream<Hologram> getHolograms(Player player);

    /**
     * Get all holograms in the given world
     *
     * @param world the world to check
     * @return all holograms in the given world
     * @since 0.2.0
     */
    Stream<Hologram> getHolograms(World world);

    /**
     * Get all holograms nearby the given location
     *
     * @param location the location to check
     * @param radius   the radius to check
     * @return all holograms nearby the given location
     * @throws IllegalArgumentException if the radius is smaller than 1
     * @throws IllegalArgumentException if the world of the location is {@code null}
     * @since 0.1.0
     */
    @Unmodifiable
    Collection<? extends Hologram> getHologramNearby(Location location, double radius) throws IllegalArgumentException;

    /**
     * Get the names of all holograms
     *
     * @return the names of all holograms
     * @since 0.1.0
     */
    Stream<String> getHologramNames();

    /**
     * Check if a hologram with the given name exists
     *
     * @param name the name of the hologram
     * @return {@code true} if the hologram exists, {@code false} otherwise
     * @since 0.1.0
     */
    boolean hologramExists(String name);

    /**
     * Check if the given entity is part of a hologram
     *
     * @param entity the entity to check
     * @return {@code true} if the entity is part of a hologram, {@code false} otherwise
     * @since 0.1.0
     */
    boolean isHologramPart(Entity entity);

    /**
     * Create a hologram with the given name and location
     *
     * @param name     the name of the hologram
     * @param location the location of the hologram
     * @return the created hologram
     * @throws IllegalStateException if a hologram with the given name already exists
     * @since 0.1.0
     */
    Hologram createHologram(String name, Location location) throws IllegalStateException;

    /**
     * Spawn a hologram with the given name and location
     *
     * @param name     the name of the hologram
     * @param location the location of the hologram
     * @param preSpawn a consumer to apply to the hologram before spawning
     * @return the spawned hologram
     * @throws IllegalStateException if a hologram with the given name already exists
     * @since 0.1.0
     */
    Hologram spawnHologram(String name, Location location, Consumer<Hologram> preSpawn) throws IllegalStateException;

    /**
     * Delete a hologram
     *
     * @param hologram the hologram to delete
     * @return {@code true} if the hologram was deleted, {@code false} otherwise
     * @since 0.2.0
     */
    boolean deleteHologram(Hologram hologram);
}
