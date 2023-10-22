package net.thenextlvl.hologram.api;

import net.thenextlvl.hologram.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * A loader class that handles loading, unloading and updating of holograms
 */
public interface HologramLoader {
    /**
     * Loads the hologram for the specified player
     *
     * @param hologram the hologram to load
     * @param player   the player to load the hologram for
     * @throws IllegalArgumentException thrown if the hologram is
     *                                  {@link HologramLoader#isLoaded(Hologram, Player) already loaded} or not
     *                                  {@link HologramLoader#canSee(Player, Hologram) visible} to the player
     * @throws NullPointerException     thrown if the
     *                                  {@link Location#getWorld() world} of the hologram is null
     */
    void load(Hologram hologram, Player player) throws IllegalArgumentException, NullPointerException;

    /**
     * Unloads the hologram for all the viewers
     *
     * @param hologram the hologram to unload
     * @see HologramLoader#getViewers(Hologram)
     * @see HologramLoader#unload(Hologram, Player)
     */
    default void unload(Hologram hologram) {
        getViewers(hologram).forEach(player -> unload(hologram, player));
    }

    /**
     * Unloads the hologram for the specified player
     *
     * @param hologram the hologram to unload
     * @param player   the player to unload the hologram for
     * @throws IllegalArgumentException thrown if the hologram is not
     *                                  {@link HologramLoader#isLoaded(Hologram, Player) loaded}
     * @see HologramLoader#unload(Hologram)
     */
    void unload(Hologram hologram, Player player) throws IllegalArgumentException;

    /**
     * Updates the hologram for all the viewers
     *
     * @param hologram the hologram to update
     * @throws NullPointerException thrown if the
     *                              {@link Location#getWorld() world} of the hologram is null
     * @see HologramLoader#getViewers(Hologram)
     * @see HologramLoader#update(Hologram, Player)
     */
    default void update(Hologram hologram) throws NullPointerException {
        getViewers(hologram).forEach(player -> update(hologram, player));
    }

    /**
     * Updates the hologram for the specified player
     *
     * @param hologram the hologram to update
     * @param player   the player to update the hologram for
     * @throws IllegalArgumentException thrown if the hologram is
     *                                  {@link HologramLoader#isLoaded(Hologram, Player) not loaded} or not
     *                                  {@link HologramLoader#canSee(Player, Hologram) visible} to the player
     * @throws NullPointerException     thrown if the
     *                                  {@link Location#getWorld() world} of the hologram is null
     * @see HologramLoader#update(Hologram)
     */
    void update(Hologram hologram, Player player) throws IllegalArgumentException, NullPointerException;

    /**
     * Teleport the hologram to a new location
     *
     * @param hologram the hologram to teleport
     * @param location the new location of the hologram
     * @throws IllegalArgumentException thrown if the current and new
     *                                  {@link Location#getWorld() world} don't match
     * @throws NullPointerException     thrown if the
     *                                  {@link Location#getWorld() world} of the hologram is null
     */
    default void teleport(Hologram hologram, Location location) throws IllegalArgumentException, NullPointerException {
        getViewers(hologram).forEach(player -> teleport(hologram, location, player));
    }

    /**
     * Teleport the hologram for a specific player to a new location
     *
     * @param hologram the hologram to teleport
     * @param location the new location of the hologram
     * @param player   the player to teleport the hologram for
     * @throws IllegalArgumentException thrown if the hologram is
     *                                  {@link HologramLoader#isLoaded(Hologram, Player) not loaded}, not
     *                                  {@link HologramLoader#canSee(Player, Hologram) visible} to the player or
     *                                  the current and new {@link Location#getWorld() world} don't match
     * @throws NullPointerException     thrown if the
     *                                  {@link Location#getWorld() world} of the hologram is null
     */
    void teleport(Hologram hologram, Location location, Player player) throws IllegalArgumentException, NullPointerException;

    /**
     * Checks if the hologram is loaded for the player
     *
     * @param hologram the hologram
     * @param player   the player
     * @return true if the hologram is loaded for the player
     * @see HologramLoader#getViewers(Hologram)
     */
    default boolean isLoaded(Hologram hologram, Player player) {
        return getHolograms(player).contains(hologram);
    }

    /**
     * Checks if the player could possibly see the hologram
     *
     * @param player   the player
     * @param hologram the hologram
     * @return true if the hologram can be seen by the player
     */
    default boolean canSee(Player player, Hologram hologram) {
        return player.getWorld().equals(hologram.getLocation().getWorld());
    }

    /**
     * All the players the hologram is loaded for
     *
     * @param hologram the hologram
     * @return the players the hologram is loaded for
     * @see HologramLoader#isLoaded(Hologram, Player)
     */
    Collection<Player> getViewers(Hologram hologram);

    /**
     * All the holograms that are currently loaded for the player
     *
     * @param player the player
     * @return the holograms currently loaded for the player
     */
    Collection<Hologram> getHolograms(Player player);
}
