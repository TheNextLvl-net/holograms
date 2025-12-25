package net.thenextlvl.hologram.line;

import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Contract;
import org.joml.Matrix4f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents a line type within a hologram that is display-entity based.
 *
 * @since 0.1.0
 */
public interface DisplayHologramLine<E extends Display> extends HologramLine<E> {
    /**
     * Gets the transformation applied to this display.
     *
     * @return the transformation
     */
    @Contract(pure = true)
    Transformation getTransformation();

    /**
     * Sets the transformation applied to this display
     *
     * @param transformation the new transformation
     */
    @Contract(mutates = "this")
    void setTransformation(Transformation transformation);

    /**
     * Sets the raw transformation matrix applied to this display
     *
     * @param transformationMatrix the transformation matrix
     */
    @Contract(mutates = "this")
    void setTransformationMatrix(Matrix4f transformationMatrix);

    /**
     * Gets the interpolation duration of this display.
     *
     * @return interpolation duration
     */
    @Contract(pure = true)
    int getInterpolationDuration();

    /**
     * Sets the interpolation duration of this display.
     *
     * @param duration new duration
     */
    @Contract(mutates = "this")
    void setInterpolationDuration(int duration);

    /**
     * Gets the teleport duration of this display.
     * <ul>
     *     <li>0 means that updates are applied immediately.</li>
     *     <li>1 means that the display entity will move from current position to the updated one over one tick.</li>
     *     <li>Higher values spread the movement over multiple ticks.</li>
     * </ul>
     *
     * @return teleport duration
     */
    @Contract(pure = true)
    int getTeleportDuration();

    /**
     * Sets the teleport duration of this display.
     *
     * @param duration new duration
     * @throws IllegalArgumentException if duration is not between 0 and 59
     * @see #getTeleportDuration()
     */
    @Contract(mutates = "this")
    void setTeleportDuration(int duration);

    /**
     * Gets the view distance/range of this display.
     *
     * @return view range
     */
    @Contract(pure = true)
    float getViewRange();

    /**
     * Sets the view distance/range of this display.
     *
     * @param range new range
     */
    @Contract(mutates = "this")
    void setViewRange(float range);

    /**
     * Gets the shadow radius of this display.
     *
     * @return radius
     */
    @Contract(pure = true)
    float getShadowRadius();

    /**
     * Sets the shadow radius of this display.
     *
     * @param radius new radius
     */
    @Contract(mutates = "this")
    void setShadowRadius(float radius);

    /**
     * Gets the shadow strength of this display.
     *
     * @return shadow strength
     */
    @Contract(pure = true)
    float getShadowStrength();

    /**
     * Sets the shadow strength of this display.
     *
     * @param strength new strength
     */
    @Contract(mutates = "this")
    void setShadowStrength(float strength);

    /**
     * Gets the width of this display.
     *
     * @return width
     */
    @Contract(pure = true)
    float getDisplayWidth();

    /**
     * Sets the width of this display.
     *
     * @param width new width
     */
    @Contract(mutates = "this")
    void setDisplayWidth(float width);

    /**
     * Gets the height of this display.
     *
     * @return height
     */
    @Contract(pure = true)
    float getDisplayHeight();

    /**
     * Sets the height if this display.
     *
     * @param height new height
     */
    @Contract(mutates = "this")
    void setDisplayHeight(float height);

    /**
     * Gets the amount of ticks before client-side interpolation will commence.
     *
     * @return interpolation delay ticks
     */
    @Contract(pure = true)
    int getInterpolationDelay();

    /**
     * Sets the amount of ticks before client-side interpolation will commence.
     *
     * @param ticks interpolation delay ticks
     */
    @Contract(mutates = "this")
    void setInterpolationDelay(int ticks);

    /**
     * Gets the billboard setting of this entity.
     * <p>
     * The billboard setting controls the automatic rotation of the entity to
     * face the player.
     *
     * @return billboard setting
     */
    @Contract(pure = true)
    Display.Billboard getBillboard();

    /**
     * Sets the billboard setting of this entity.
     * <p>
     * The billboard setting controls the automatic rotation of the entity to
     * face the player.
     *
     * @param billboard new setting
     */
    @Contract(mutates = "this")
    void setBillboard(Display.Billboard billboard);

    /**
     * Gets the scoreboard team overridden glow color of this display.
     *
     * @return glow color
     */
    @Nullable
    @Contract(pure = true)
    Color getGlowColorOverride();

    /**
     * Sets the scoreboard team overridden glow color of this display.
     *
     * @param color new color
     */
    @Contract(mutates = "this")
    void setGlowColorOverride(@Nullable Color color);

    /**
     * Gets the brightness override of the entity.
     *
     * @return brightness override, if set
     */
    @Contract(pure = true)
    Display.@Nullable Brightness getBrightness();

    /**
     * Sets the brightness override of the entity.
     *
     * @param brightness new brightness override
     */
    @Contract(mutates = "this")
    void setBrightness(Display.@Nullable Brightness brightness);
}
