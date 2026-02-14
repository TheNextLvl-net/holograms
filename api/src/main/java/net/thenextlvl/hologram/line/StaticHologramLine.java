package net.thenextlvl.hologram.line;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Contract;
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
     * @since 0.5.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    StaticHologramLine setGlowing(boolean glowing);

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
     * @since 0.5.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    StaticHologramLine setGlowColor(@Nullable TextColor color);

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
     * @return this
     * @since 0.10.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    StaticHologramLine setBillboard(Display.Billboard billboard);

    /**
     * Gets the parent of this line if it is part of a paged line.
     *
     * @return parent paged line
     * @since 0.7.0
     */
    @Contract(pure = true)
    Optional<PagedHologramLine> getParentLine();
}
