package net.thenextlvl.hologram.line;

import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

/**
 * Represents a line type within a hologram that displays an item.
 *
 * @since 0.1.0
 */
public interface ItemHologramLine extends DisplayHologramLine<ItemHologramLine, ItemDisplay> {
    /**
     * Gets the displayed item stack.
     *
     * @return the displayed item stack
     */
    @Contract(pure = true)
    ItemStack getItemStack();

    /**
     * Sets the displayed item stack.
     *
     * @param item the new item stack
     * @return this
     */
    @Contract(value = "_ -> this", mutates = "this")
    ItemHologramLine setItemStack(@Nullable ItemStack item);

    /**
     * Gets the item display transform for this entity.
     * <p>
     * Defaults to {@link ItemDisplay.ItemDisplayTransform#FIXED}.
     *
     * @return item display transform
     */
    @Contract(pure = true)
    ItemDisplay.ItemDisplayTransform getItemDisplayTransform();

    /**
     * Sets the item display transform for this entity.
     * <p>
     * Defaults to {@link ItemDisplay.ItemDisplayTransform#FIXED}.
     *
     * @param display new display
     * @return this
     */
    @Contract(value = "_ -> this", mutates = "this")
    ItemHologramLine setItemDisplayTransform(ItemDisplay.ItemDisplayTransform display);
}
