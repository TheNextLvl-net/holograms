package net.thenextlvl.hologram;

import net.thenextlvl.binder.StaticBinder;
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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Represents a controller responsible for managing, creating, and querying holograms and their associated lines.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface HologramProvider {
    /**
     * Returns the hologram provider instance.
     *
     * @return the hologram provider
     * @since 0.3.0
     */
    @Contract(pure = true)
    static HologramProvider instance() {
        return StaticBinder.getInstance(HologramProvider.class.getClassLoader()).find(HologramProvider.class);
    }

    /**
     * Get the data folder for the given world
     *
     * @param world the world to get the data folder for
     * @return the data folder for the given world
     * @since 0.3.0
     */
    @Contract(pure = true)
    Path getDataFolder(World world);

    /**
     * Get a hologram by its entity
     *
     * @param entity the entity of the hologram
     * @return the hologram that contains the given entity
     * @since 0.3.0
     */
    @Contract(pure = true)
    Optional<Hologram> getHologram(Entity entity);

    /**
     * Get a hologram line by its entity
     *
     * @param entity the entity of the hologram line
     * @return the hologram line with the given entity
     * @since 0.1.0
     */
    @Contract(pure = true)
    Optional<HologramLine> getHologramLine(Entity entity);

    /**
     * Get a block hologram line by its block display
     *
     * @param display the block display of the hologram line
     * @return the hologram line with the given display
     * @since 0.1.0
     */
    @Contract(pure = true)
    Optional<BlockHologramLine> getHologramLine(BlockDisplay display);

    /**
     * Get an item hologram line by its item display
     *
     * @param display the item display of the hologram line
     * @return the hologram line with the given display
     * @since 0.1.0
     */
    @Contract(pure = true)
    Optional<ItemHologramLine> getHologramLine(ItemDisplay display);

    /**
     * Get a text hologram line by its text display
     *
     * @param display the text display of the hologram line
     * @return the hologram line with the given display
     * @since 0.1.0
     */
    @Contract(pure = true)
    Optional<TextHologramLine> getHologramLine(TextDisplay display);

    /**
     * Get a hologram by its name
     *
     * @param name the name of the hologram
     * @return the hologram with the given name
     * @since 0.1.0
     */
    @Contract(pure = true)
    Optional<Hologram> getHologram(String name);

    /**
     * Get all holograms
     *
     * @return all holograms
     * @since 0.3.0
     */
    @Contract(pure = true)
    Stream<Hologram> getHolograms();

    /**
     * Get all holograms in the given chunk
     *
     * @param chunk the chunk to check
     * @return all holograms in the given chunk
     * @since 0.3.0
     */
    @Contract(pure = true)
    Stream<Hologram> getHolograms(Chunk chunk);

    /**
     * Get all holograms visible to the given player
     *
     * @param player the player to check
     * @return all holograms visible to the given player
     * @since 0.3.0
     */
    @Contract(pure = true)
    Stream<Hologram> getHolograms(Player player);

    /**
     * Get all holograms in the given world
     *
     * @param world the world to check
     * @return all holograms in the given world
     * @since 0.3.0
     */
    @Contract(pure = true)
    Stream<Hologram> getHolograms(World world);

    /**
     * Get all holograms nearby the given location
     *
     * @param location the location to check
     * @param radius   the radius to check
     * @return all holograms nearby the given location
     * @throws IllegalArgumentException if the radius is smaller than 1
     * @throws IllegalArgumentException if the world of the location is {@code null}
     * @since 0.12.0
     */
    @Contract(pure = true)
    Stream<Hologram> getHologramsNearby(Location location, double radius) throws IllegalArgumentException;

    /**
     * Get the names of all holograms
     *
     * @return the names of all holograms
     * @since 0.1.0
     */
    @Contract(pure = true)
    Stream<String> getHologramNames();

    /**
     * Check if a hologram with the given name exists
     *
     * @param name the name of the hologram
     * @return {@code true} if the hologram exists, {@code false} otherwise
     * @since 0.3.0
     */
    @Contract(pure = true)
    boolean hasHologram(String name);

    /**
     * Check if the given hologram exists
     *
     * @param hologram the hologram to check
     * @return {@code true} if the hologram exists, {@code false} otherwise
     * @since 0.3.0
     */
    @Contract(pure = true)
    boolean hasHologram(Hologram hologram);

    /**
     * Check if the given entity is part of a hologram
     *
     * @param entity the entity to check
     * @return {@code true} if the entity is part of a hologram, {@code false} otherwise
     * @since 0.1.0
     */
    @Contract(pure = true)
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
    @Contract(mutates = "this")
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
    @Contract(mutates = "this")
    Hologram spawnHologram(String name, Location location, Consumer<Hologram> preSpawn) throws IllegalStateException;

    /**
     * Delete a hologram
     *
     * @param hologram the hologram to delete
     * @return {@code true} if the hologram was deleted, {@code false} otherwise
     * @since 0.3.0
     */
    @Contract(mutates = "this,io,param")
    boolean deleteHologram(Hologram hologram);

    /**
     * Performs the given action for each hologram.
     *
     * @param action the action to perform
     * @since 0.3.0
     */
    void forEachHologram(Consumer<Hologram> action);
}
