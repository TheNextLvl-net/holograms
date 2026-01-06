package net.thenextlvl.hologram.line;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a line type within a hologram that displays text content.
 *
 * @since 0.1.0
 */
public interface TextHologramLine extends DisplayHologramLine<TextHologramLine, TextDisplay> {
    /**
     * Gets the displayed text.
     *
     * @return the displayed text
     */
    @Contract(pure = true)
    Optional<Component> getText();

    /**
     * Sets the displayed text.
     *
     * @param text the new text
     * @return this
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setText(@Nullable Component text);

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
     * @return this
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setLineWidth(int width);

    /**
     * Gets the text background color.
     *
     * @return the background color
     */
    @Contract(pure = true)
    Optional<Color> getBackgroundColor();

    /**
     * Sets the text background color.
     *
     * @param color new background color
     * @return this
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setBackgroundColor(@Nullable Color color);

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
     * @return this
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setTextOpacity(@Range(from = 0, to = 100) float opacity);

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
     * @return this
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setShadowed(boolean shadow);

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
     * @return this
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setSeeThrough(boolean seeThrough);

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
     * @return this
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setDefaultBackground(boolean defaultBackground);

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
     * @return this
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setAlignment(TextDisplay.TextAlignment alignment);
}
