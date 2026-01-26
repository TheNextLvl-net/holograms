package net.thenextlvl.hologram.line;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a line type within a hologram that displays text content.
 *
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public interface TextHologramLine extends DisplayHologramLine {
    /**
     * Gets the displayed text.
     *
     * @param player the player for which to get the text
     * @return the displayed text
     * @since 0.4.0
     */
    @Contract(pure = true)
    Optional<Component> getText(Player player);

    /**
     * Gets the displayed text in {@link net.kyori.adventure.text.minimessage.MiniMessage} format.
     *
     * @return the displayed text
     * @since 0.4.0
     */
    @Contract(pure = true)
    Optional<String> getUnparsedText();

    /**
     * Sets the displayed text.
     *
     * @param text the new text
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setText(@Nullable Component text);

    /**
     * Sets the displayed text in {@link net.kyori.adventure.text.minimessage.MiniMessage} format.
     *
     * @param text the new text
     * @return this
     * @since 0.4.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setUnparsedText(@Nullable String text);

    /**
     * Gets the maximum line width before wrapping.
     *
     * @return the line width
     * @since 0.1.0
     */
    @Contract(pure = true)
    int getLineWidth();

    /**
     * Sets the maximum line width before wrapping.
     *
     * @param width new line width
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setLineWidth(int width);

    /**
     * Gets the text background color.
     *
     * @return the background color
     * @since 0.3.0
     */
    @Contract(pure = true)
    Optional<Color> getBackgroundColor();

    /**
     * Sets the text background color.
     *
     * @param color new background color
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setBackgroundColor(@Nullable Color color);

    /**
     * Gets the text opacity.
     *
     * @return opacity or -1 if not set
     * @since 0.1.0
     */
    @Contract(pure = true)
    float getTextOpacity();

    /**
     * Sets the text opacity.
     *
     * @param opacity new opacity or -1 if default
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setTextOpacity(@Range(from = 0, to = 100) float opacity);

    /**
     * Gets if the text is shadowed.
     *
     * @return shadow status
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isShadowed();

    /**
     * Sets if the text is shadowed.
     *
     * @param shadow if shadowed
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setShadowed(boolean shadow);

    /**
     * Gets if the text is see through.
     *
     * @return see through status
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isSeeThrough();

    /**
     * Sets if the text is see through.
     *
     * @param seeThrough if see through
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setSeeThrough(boolean seeThrough);

    /**
     * Gets if the text has its default background.
     *
     * @return default background
     * @since 0.1.0
     */
    @Contract(pure = true)
    boolean isDefaultBackground();

    /**
     * Sets if the text has its default background.
     *
     * @param defaultBackground if default
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setDefaultBackground(boolean defaultBackground);

    /**
     * Gets the text alignment for this display.
     *
     * @return text alignment
     * @since 0.1.0
     */
    @Contract(pure = true)
    TextAlignment getAlignment();

    /**
     * Sets the text alignment for this display.
     *
     * @param alignment new alignment
     * @return this
     * @since 0.3.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    TextHologramLine setAlignment(TextAlignment alignment);
}
