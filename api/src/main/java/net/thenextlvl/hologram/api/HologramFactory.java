package net.thenextlvl.hologram.api;

import net.thenextlvl.hologram.api.line.BlockLine;
import net.thenextlvl.hologram.api.line.HologramLine;
import net.thenextlvl.hologram.api.line.ItemLine;
import net.thenextlvl.hologram.api.line.TextLine;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A factory that creates {@link Hologram holograms} and {@link HologramLine hologram lines}
 */
public interface HologramFactory {
    /**
     * Create a new hologram object
     *
     * @param location the location of the hologram
     * @param lines the lines of the hologram
     * @return the new hologram
     */
    Hologram createHologram(Location location, Collection<? extends HologramLine> lines);

    /**
     * Create a new hologram object
     *
     * @param location the location of the hologram
     * @param lines the lines of the hologram
     * @return the new hologram
     */
    Hologram createHologram(Location location, HologramLine... lines);

    /**
     * Creates a new line of the type block
     *
     * @param function provides the block-display and returns the offset
     * @return the new line
     */
    BlockLine createBlockLine(Function<BlockDisplay, Number> function);

    /**
     * Creates a new line of the type block
     *
     * @param consumer provides the block-display
     * @return the new line
     */
    BlockLine createBlockLine(Consumer<BlockDisplay> consumer);

    /**
     * Creates a new line of the type item
     *
     * @param function provides the item-display and returns the offset
     * @return the new line
     */
    ItemLine createItemLine(Function<ItemDisplay, Number> function);

    /**
     * Creates a new line of the type item
     *
     * @param consumer provides the item-display
     * @return the new line
     */
    ItemLine createItemLine(Consumer<ItemDisplay> consumer);

    /**
     * Creates a new line of the type text
     *
     * @param function provides the text-display and returns the offset
     * @return the new line
     */
    TextLine createTextLine(Function<TextDisplay, Number> function);

    /**
     * Creates a new line of the type text
     *
     * @param consumer provides the text-display
     * @return the new line
     */
    TextLine createTextLine(Consumer<TextDisplay> consumer);
}
