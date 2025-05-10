package net.thenextlvl.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@NullMarked
public interface HologramController {
    /**
     * Get a hologram by its display
     *
     * @param display the display of the hologram
     * @param <E>     the type of the display
     * @return the hologram with the given display
     */
    <E extends Display> Optional<Hologram<E>> getHologram(E display);

    /**
     * Get a hologram by its display
     *
     * @param display the display of the hologram
     * @return the hologram with the given display
     */
    Optional<BlockHologram> getHologram(BlockDisplay display);

    /**
     * Get a hologram by its display
     *
     * @param display the display of the hologram
     * @return the hologram with the given display
     */
    Optional<ItemHologram> getHologram(ItemDisplay display);

    /**
     * Get a hologram by its display
     *
     * @param display the display of the hologram
     * @return the hologram with the given display
     */
    Optional<TextHologram> getHologram(TextDisplay display);

    /**
     * Get a hologram by its name
     *
     * @param name the name of the hologram
     * @return the hologram with the given name
     */
    Optional<Hologram<?>> getHologram(String name);

    /**
     * Get a hologram by its unique id
     *
     * @param uuid the unique id of the hologram
     * @return the hologram with the given unique id
     */
    Optional<Hologram<?>> getHologram(UUID uuid);

    /**
     * Get all holograms
     *
     * @return all holograms
     */
    @Unmodifiable
    Collection<? extends Hologram<?>> getHolograms();

    /**
     * Get all holograms visible to the given player
     *
     * @param player the player to check
     * @return all holograms visible to the given player
     */
    @Unmodifiable
    Collection<? extends Hologram<?>> getHolograms(Player player);

    /**
     * Get all holograms in the given world
     *
     * @param world the world to check
     * @return all holograms in the given world
     */
    @Unmodifiable
    Collection<? extends Hologram<?>> getHolograms(World world);

    /**
     * Get all holograms nearby the given location
     *
     * @param location the location to check
     * @param radius   the radius to check
     * @return all holograms nearby the given location
     * @throws IllegalArgumentException if the radius is smaller than 1
     * @throws IllegalArgumentException if the world of the location is {@code null}
     */
    @Unmodifiable
    Collection<? extends Hologram<?>> getHologramNearby(Location location, double radius) throws IllegalArgumentException;

    /**
     * Get the names of all holograms
     *
     * @return the names of all holograms
     */
    @Unmodifiable
    Set<String> getHologramNames();

    /**
     * Check if a hologram with the given name exists
     *
     * @param name the name of the hologram
     * @return {@code true} if the hologram exists, {@code false} otherwise
     */
    boolean hologramExists(String name);

    /**
     * Check if the given entity is a hologram
     *
     * @param entity the entity to check
     * @return {@code true} if the entity is a hologram, {@code false} otherwise
     */
    boolean isHologram(Entity entity);

    /**
     * Create a new text hologram
     *
     * @param name the name of the hologram
     * @return the new text hologram
     * @throws IllegalStateException if a hologram with the same name already exists
     */
    TextHologram createTextHologram(String name) throws IllegalStateException;

    /**
     * Create a new block hologram
     *
     * @param name the name of the hologram
     * @return the new text hologram
     * @throws IllegalStateException if a hologram with the same name already exists
     */
    BlockHologram createBlockHologram(String name) throws IllegalStateException;

    /**
     * Create a new item hologram
     *
     * @param name the name of the hologram
     * @return the new text hologram
     * @throws IllegalStateException if a hologram with the same name already exists
     */
    ItemHologram createItemHologram(String name) throws IllegalStateException;

    /**
     * Create a new text hologram
     *
     * @param name the name of the hologram
     * @param text the text of the hologram
     * @return the new text hologram
     * @throws IllegalStateException if a hologram with the same name already exists
     */
    TextHologram createHologram(String name, Component text) throws IllegalStateException;

    /**
     * Create a new block hologram
     *
     * @param name  the name of the hologram
     * @param block the block of the hologram
     * @return the new block hologram
     * @throws IllegalStateException if a hologram with the same name already exists
     */
    BlockHologram createHologram(String name, BlockData block) throws IllegalStateException;

    /**
     * Create a new item hologram
     *
     * @param name      the name of the hologram
     * @param itemStack the item of the hologram
     * @return the new item hologram
     * @throws IllegalStateException if a hologram with the same name already exists
     */
    ItemHologram createHologram(String name, ItemStack itemStack) throws IllegalStateException;

    /**
     * Spawn a new text hologram
     *
     * @param name     the name of the hologram
     * @param location the location of the hologram
     * @param text     the text of the hologram
     * @return the new text hologram
     * @throws IllegalStateException if a hologram with the same name already exists
     */
    TextHologram spawnHologram(String name, Location location, Component text) throws IllegalStateException;

    /**
     * Spawn a new block hologram
     *
     * @param name     the name of the hologram
     * @param location the location of the hologram
     * @param block    the block of the hologram
     * @return the new block hologram
     * @throws IllegalStateException if a hologram with the same name already exists
     */
    BlockHologram spawnHologram(String name, Location location, BlockData block) throws IllegalStateException;

    /**
     * Spawn a new item hologram
     *
     * @param name      the name of the hologram
     * @param location  the location of the hologram
     * @param itemStack the item of the hologram
     * @return the new item hologram
     * @throws IllegalStateException if a hologram with the same name already exists
     */
    ItemHologram spawnHologram(String name, Location location, ItemStack itemStack) throws IllegalStateException;
}
