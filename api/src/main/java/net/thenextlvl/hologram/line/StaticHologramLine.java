package net.thenextlvl.hologram.line;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Contract;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a line type within a hologram that is static and does not change over time.
 *
 * @see BlockHologramLine
 * @see EntityHologramLine
 * @see ItemHologramLine
 * @see TextHologramLine
 * @since 0.5.0
 */
public interface StaticHologramLine extends HologramLine {
    /**
     * Gets the class of the entity representing this line.
     *
     * @return entity class
     * @since 0.8.0
     */
    @Contract(pure = true)
    Class<? extends Entity> getEntityClass();

    /**
     * Gets the entity type of the entity representing this line.
     *
     * @return entity type
     * @since 0.8.0
     */
    @Contract(pure = true)
    EntityType getEntityType();

    /**
     * Gets whether this line is glowing.
     *
     * @return true if this line is glowing
     * @since 0.5.0
     */
    @Contract(pure = true)
    boolean isGlowing();

    /**
     * Sets whether this line is glowing.
     *
     * @param glowing true if this line should glow
     * @return {@code true} if the glowing state was successfully set, {@code false} otherwise
     * @since 1.0.0
     */
    @Contract(mutates = "this")
    boolean setGlowing(boolean glowing);

    /**
     * Gets the glow color of this line.
     *
     * @return glow color
     * @since 0.5.0
     */
    @Contract(pure = true)
    Optional<TextColor> getGlowColor();

    /**
     * Sets the glow color of this line.
     *
     * @param color new color
     * @return {@code true} if the glow color was successfully set, {@code false} otherwise
     * @since 1.0.0
     */
    @Contract(mutates = "this")
    boolean setGlowColor(@Nullable TextColor color);

    /**
     * Gets the billboard setting of this line.
     * <p>
     * The billboard setting controls the automatic rotation of the line to face the player.
     *
     * @return billboard setting
     * @since 0.10.0
     */
    @Contract(pure = true)
    Display.Billboard getBillboard();

    /**
     * Sets the billboard setting of this line.
     * <p>
     * The billboard setting controls the automatic rotation of the line to face the player.
     *
     * @param billboard new setting
     * @return {@code true} if the billboard setting was successfully set, {@code false} otherwise
     * @since 1.0.0
     */
    @Contract(mutates = "this")
    boolean setBillboard(Display.Billboard billboard);

    /**
     * Gets the parent of this line if it is part of a paged line.
     *
     * @return parent paged line
     * @since 0.7.0
     */
    @Contract(pure = true)
    Optional<PagedHologramLine> getParentLine();

    /**
     * Gets the offset of the entity.
     *
     * @return a copy of the offset
     * @since 1.0.0
     */
    @Contract(value = " -> new", pure = true)
    Vector3f getOffset();

    /**
     * Sets the offset of the entity.
     *
     * @param offset new offset
     * @return {@code true} if the offset was successfully set, {@code false} otherwise
     * @since 1.0.0
     */
    @Contract(mutates = "this")
    boolean setOffset(Vector3f offset);
}
