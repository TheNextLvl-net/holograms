package net.thenextlvl.hologram.api;

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
    void unload(Hologram hologram);

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
    void update(Hologram hologram) throws NullPointerException;

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
     * Checks if the hologram is loaded for the player
     *
     * @param hologram the hologram
     * @param player   the player
     * @return true if the hologram is loaded for the player
     * @see HologramLoader#getViewers(Hologram)
     */
    boolean isLoaded(Hologram hologram, Player player);

    /**
     * Checks if the player could possibly see the hologram
     *
     * @param player   the player
     * @param hologram the hologram
     * @return true if the hologram can be seen by the player
     */
    boolean canSee(Player player, Hologram hologram);

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
    Collection<? extends Hologram> getHolograms(Player player);
}
