package net.thenextlvl.hologram.api.display;

import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public interface EntityDisplay {

    /**
     * Gets the transformation applied to this display.
     *
     * @return the transformation
     */
    Transformation getTransformation();

    /**
     * Sets the transformation applied to this display.
     *
     * @param transformation the new transformation
     */
    void setTransformation(Transformation transformation);

    /**
     * Sets the raw transformation matrix applied to this display.
     *
     * @param transformationMatrix the transformation matrix
     */
    void setTransformationMatrix(Matrix4f transformationMatrix);

    /**
     * Gets the raw transformation matrix applied to this display.
     *
     * @return the transformation matrix
     */
    Matrix4f getTransformationMatrix();

    /**
     * Gets the interpolation duration of this display.
     *
     * @return interpolation duration
     */
    int getInterpolationDuration();

    /**
     * Sets the interpolation duration of this display.
     *
     * @param duration new duration
     */
    void setInterpolationDuration(int duration);

    /**
     * Gets the view distance/range of this display.
     *
     * @return view range
     */
    float getViewRange();

    /**
     * Sets the view distance/range of this display.
     *
     * @param range new range
     */
    void setViewRange(float range);

    /**
     * Gets the shadow radius of this display.
     *
     * @return radius
     */
    float getShadowRadius();

    /**
     * Sets the shadow radius of this display.
     *
     * @param radius new radius
     */
    void setShadowRadius(float radius);

    /**
     * Gets the shadow strength of this display.
     *
     * @return shadow strength
     */
    float getShadowStrength();

    /**
     * Sets the shadow strength of this display.
     *
     * @param strength new strength
     */
    void setShadowStrength(float strength);

    /**
     * Gets the width of this display.
     *
     * @return width
     */
    float getDisplayWidth();

    /**
     * Sets the width of this display.
     *
     * @param width new width
     */
    void setDisplayWidth(float width);

    /**
     * Gets the height of this display.
     *
     * @return height
     */
    float getDisplayHeight();

    /**
     * Sets the height if this display.
     *
     * @param height new height
     */
    void setDisplayHeight(float height);

    /**
     * Gets the amount of ticks before client-side interpolation will commence.
     *
     * @return interpolation delay ticks
     */
    int getInterpolationDelay();

    /**
     * Sets the amount of ticks before client-side interpolation will commence.
     *
     * @param ticks interpolation delay ticks
     */
    void setInterpolationDelay(int ticks);

    /**
     * Gets the billboard setting of this entity.
     * The billboard setting controls the automatic rotation of the entity to
     * face the player.
     *
     * @return billboard setting
     */
    Billboard getBillboard();

    /**
     * Sets the billboard setting of this entity.
     * The billboard setting controls the automatic rotation of the entity to
     * face the player.
     *
     * @param billboard new setting
     */
    void setBillboard(Billboard billboard);

    /**
     * Gets the scoreboard team overridden glow color of this display.
     *
     * @return glow color
     */
    @Nullable Color getGlowColorOverride();

    /**
     * Sets the scoreboard team overridden glow color of this display.
     *
     * @param color new color
     */
    void setGlowColorOverride(@Nullable Color color);

    /**
     * Gets the brightness override of the entity.
     *
     * @return brightness override, if set
     */
    @Nullable Brightness getBrightness();

    /**
     * Sets the brightness override of the entity.
     *
     * @param brightness new brightness override
     */
    void setBrightness(@Nullable Brightness brightness);
}
