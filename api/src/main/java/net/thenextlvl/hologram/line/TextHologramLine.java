package net.thenextlvl.hologram.line;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents a line type within a hologram that displays text content.
 *
 * @since 0.1.0
 */
public interface TextHologramLine extends DisplayHologramLine<TextDisplay> {
    /**
     * Gets the displayed text.
     *
     * @return the displayed text
     */
    @Nullable
    @Contract(pure = true)
    Component getText();

    /**
     * Sets the displayed text.
     *
     * @param text the new text
     */
    @Contract(mutates = "this")
    void setText(@Nullable Component text);

    /**
     * Gets the maximum line width before wrapping.
     *
     * @return the line width
     */
    @Contract(pure = true)
    int getLineWidth();

    /**
     * Sets the maximum line width before wrapping.
     *
     * @param width new line width
     */
    @Contract(mutates = "this")
    void setLineWidth(int width);

    /**
     * Gets the text background color.
     *
     * @return the background color
     */
    @Nullable
    @Contract(pure = true)
    Color getBackgroundColor();

    /**
     * Sets the text background color.
     *
     * @param color new background color
     */
    @Contract(mutates = "this")
    void setBackgroundColor(@Nullable Color color);

    /**
     * Gets the text opacity.
     *
     * @return opacity or -1 if not set
     */
    @Contract(pure = true)
    float getTextOpacity();

    /**
     * Sets the text opacity.
     *
     * @param opacity new opacity or -1 if default
     */
    @Contract(mutates = "this")
    void setTextOpacity(@Range(from = 0, to = 100) float opacity);

    /**
     * Gets if the text is shadowed.
     *
     * @return shadow status
     */
    @Contract(pure = true)
    boolean isShadowed();

    /**
     * Sets if the text is shadowed.
     *
     * @param shadow if shadowed
     */
    @Contract(mutates = "this")
    void setShadowed(boolean shadow);

    /**
     * Gets if the text is see through.
     *
     * @return see through status
     */
    @Contract(pure = true)
    boolean isSeeThrough();

    /**
     * Sets if the text is see through.
     *
     * @param seeThrough if see through
     */
    @Contract(mutates = "this")
    void setSeeThrough(boolean seeThrough);

    /**
     * Gets if the text has its default background.
     *
     * @return default background
     */
    @Contract(pure = true)
    boolean isDefaultBackground();

    /**
     * Sets if the text has its default background.
     *
     * @param defaultBackground if default
     */
    @Contract(mutates = "this")
    void setDefaultBackground(boolean defaultBackground);

    /**
     * Gets the text alignment for this display.
     *
     * @return text alignment
     */
    @Contract(pure = true)
    TextDisplay.TextAlignment getAlignment();

    /**
     * Sets the text alignment for this display.
     *
     * @param alignment new alignment
     */
    @Contract(mutates = "this")
    void setAlignment(TextDisplay.TextAlignment alignment);
}
