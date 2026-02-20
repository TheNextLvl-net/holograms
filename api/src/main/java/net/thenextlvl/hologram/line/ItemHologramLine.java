package net.thenextlvl.hologram.line;

import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

/**
 * Represents a line type within a hologram that displays an item.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface ItemHologramLine extends DisplayHologramLine {
    /**
     * Gets the displayed item stack.
     *
     * @return the displayed item stack
     * @since 0.1.0
     */
    @Contract(pure = true)
    ItemStack getItemStack();

    /**
     * Sets the displayed item stack.
     *
     * @param item the new item stack
     * @return {@code true} if the item stack was successfully set, {@code false} otherwise
     * @since 1.0.0
     */
    @Contract(mutates = "this")
    boolean setItemStack(@Nullable ItemStack item);

    /**
     * Checks if this item hologram line is a player head.
     * <p>
     * The line will render the item as the viewing player's head if true.
     * <p>
     * This takes precedence over {@link #setItemStack(ItemStack)}.
     *
     * @return {@code true} if this item hologram line is a player head
     * @since 0.11.0
     */
    @Contract(pure = true)
    boolean isPlayerHead();

    /**
     * Sets if this item hologram line is a player head.
     * <p>
     * The line will render the item as the viewing player's head if true.
     *
     * @param playerHead {@code true} if this item hologram line is a player head
     * @return {@code true} if the player head status was successfully set, {@code false} otherwise
     * @since 1.0.0
     */
    @Contract(mutates = "this")
    boolean setPlayerHead(boolean playerHead);

    /**
     * Gets the item display transform for this entity.
     * <p>
     * Defaults to {@link ItemDisplay.ItemDisplayTransform#FIXED}.
     *
     * @return item display transform
     * @since 0.1.0
     */
    @Contract(pure = true)
    ItemDisplay.ItemDisplayTransform getItemDisplayTransform();

    /**
     * Sets the item display transform for this entity.
     * <p>
     * Defaults to {@link ItemDisplay.ItemDisplayTransform#FIXED}.
     *
     * @param display new display
     * @return {@code true} if the item display transform was successfully set, {@code false} otherwise
     * @since 1.0.0
     */
    @Contract(mutates = "this")
    boolean setItemDisplayTransform(ItemDisplay.ItemDisplayTransform display);
}
