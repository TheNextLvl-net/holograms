package net.thenextlvl.hologram.line;

import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a line type within a hologram that is display-entity based.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface DisplayHologramLine extends HologramLine {
    /**
     * Gets the transformation applied to this display.
     *
     * @return a copy of the transformation
     * @since 0.1.0
     */
    @Contract(pure = true)
    Transformation getTransformation();

    /**
     * Sets the transformation applied to this display
     *
     * @param transformation the new transformation
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    DisplayHologramLine setTransformation(Transformation transformation);

    /**
     * Sets the raw transformation matrix applied to this display
     *
     * @param transformationMatrix the transformation matrix
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    DisplayHologramLine setTransformationMatrix(Matrix4f transformationMatrix);

    /**
     * Gets the interpolation duration of this display.
     *
     * @return interpolation duration
     * @since 0.1.0
     */
    @Contract(pure = true)
    int getInterpolationDuration();

    /**
     * Sets the interpolation duration of this display.
     *
     * @param duration new duration
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    DisplayHologramLine setInterpolationDuration(int duration);

    /**
     * Gets the teleport duration of this display.
     * <ul>
     *     <li>0 means that updates are applied immediately.</li>
     *     <li>1 means that the display entity will move from current position to the updated one over one tick.</li>
     *     <li>Higher values spread the movement over multiple ticks.</li>
     * </ul>
     *
     * @return teleport duration
     * @since 0.1.0
     */
    @Contract(pure = true)
    int getTeleportDuration();

    /**
     * Sets the teleport duration of this display.
     *
     * @param duration new duration
     * @return this
     * @throws IllegalArgumentException if duration is not between 0 and 59
     * @see #getTeleportDuration()
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    DisplayHologramLine setTeleportDuration(int duration);

    /**
     * Gets the view distance/range of this display.
     *
     * @return view range
     * @since 0.1.0
     */
    @Contract(pure = true)
    float getViewRange();

    /**
     * Sets the view distance/range of this display.
     *
     * @param range new range
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    DisplayHologramLine setViewRange(float range);

    /**
     * Gets the shadow radius of this display.
     *
     * @return radius
     * @since 0.1.0
     */
    @Contract(pure = true)
    float getShadowRadius();

    /**
     * Sets the shadow radius of this display.
     *
     * @param radius new radius
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    DisplayHologramLine setShadowRadius(float radius);

    /**
     * Gets the shadow strength of this display.
     *
     * @return shadow strength
     * @since 0.1.0
     */
    @Contract(pure = true)
    float getShadowStrength();

    /**
     * Sets the shadow strength of this display.
     *
     * @param strength new strength
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    DisplayHologramLine setShadowStrength(float strength);

    /**
     * Gets the width of this display.
     *
     * @return width
     * @since 0.1.0
     */
    @Contract(pure = true)
    float getDisplayWidth();

    /**
     * Sets the width of this display.
     *
     * @param width new width
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    DisplayHologramLine setDisplayWidth(float width);

    /**
     * Gets the height of this display.
     *
     * @return height
     * @since 0.1.0
     */
    @Contract(pure = true)
    float getDisplayHeight();

    /**
     * Sets the height if this display.
     *
     * @param height new height
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    DisplayHologramLine setDisplayHeight(float height);

    /**
     * Gets the amount of ticks before client-side interpolation will commence.
     *
     * @return interpolation delay ticks
     * @since 0.1.0
     */
    @Contract(pure = true)
    int getInterpolationDelay();

    /**
     * Sets the amount of ticks before client-side interpolation will commence.
     *
     * @param ticks interpolation delay ticks
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    DisplayHologramLine setInterpolationDelay(int ticks);

    /**
     * Gets the billboard setting of this entity.
     * <p>
     * The billboard setting controls the automatic rotation of the entity to
     * face the player.
     *
     * @return billboard setting
     * @since 0.1.0
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
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    DisplayHologramLine setBillboard(Display.Billboard billboard);

    /**
     * Gets the brightness override of the entity.
     *
     * @return brightness override, if set
     * @since 0.3.0
     */
    @Contract(pure = true)
    Optional<Display.Brightness> getBrightness();

    /**
     * Sets the brightness override of the entity.
     *
     * @param brightness new brightness override
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    DisplayHologramLine setBrightness(Display.@Nullable Brightness brightness);
}
