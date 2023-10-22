package net.thenextlvl.hologram.api;

import net.kyori.adventure.text.Component;
import net.thenextlvl.hologram.api.hologram.BlockHologram;
import net.thenextlvl.hologram.api.hologram.Hologram;
import net.thenextlvl.hologram.api.hologram.ItemHologram;
import net.thenextlvl.hologram.api.hologram.TextHologram;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

/**
 * A factory that creates {@link Hologram holograms}
 */
public interface HologramFactory {

    /**
     * Create a new text hologram object
     *
     * @param location the location of the hologram
     * @param text     the text of the hologram
     * @return the new text hologram
     */
    TextHologram createHologram(Location location, Component text);

    /**
     * Create a new block hologram object
     *
     * @param location the location of the hologram
     * @param block    the block of the hologram
     * @return the new block hologram
     */
    BlockHologram createHologram(Location location, BlockData block);

    /**
     * Create a new item hologram object
     *
     * @param location  the location of the hologram
     * @param itemStack the item of the hologram
     * @return the new item hologram
     */
    ItemHologram createHologram(Location location, ItemStack itemStack);
}
