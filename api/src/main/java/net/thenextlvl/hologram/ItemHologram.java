package net.thenextlvl.hologram;

import net.thenextlvl.hologram.display.ItemHologramDisplay;
import org.bukkit.entity.ItemDisplay;
import org.jspecify.annotations.NullMarked;

/**
 * An interface that represents a hologram displaying an item
 */
@NullMarked
public interface ItemHologram extends Hologram<ItemDisplay>, ItemHologramDisplay {
}
