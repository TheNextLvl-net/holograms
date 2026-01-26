package net.thenextlvl.hologram.image;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.NoSuchElementException;

/**
 * A utility class for creating and reading image components for holograms.
 *
 * @since 0.5.0
 */
public final class ImageComponent {
    /**
     * Reads an image component from a {@link BufferedImage}.
     * <p>
     * The image will be scaled to the specified height with the proper aspect ratio.
     *
     * @param image  The image to read.
     * @param height The height of the image component.
     * @return The image component.
     * @since 0.5.0
     */
    @CheckReturnValue
    @Contract(value = "_, _ -> new")
    public static Component read(final BufferedImage image, final int height) {
        return SimpleImageComponent.read(image, height);
    }

    /**
     * Reads an animated image component from a file.
     * <p>
     * The animation will be scaled to the specified height with the proper aspect ratio.
     *
     * @param file   The file to read.
     * @param height The height of the image component.
     * @return The animated image component.
     * @throws IOException            If an I/O error occurs.
     * @throws NoSuchElementException If the file does not contain any frames.
     * @since 0.5.0
     */
    @CheckReturnValue
    @Contract(value = "_, _ -> new")
    public static Animated readAnimated(final Path file, final int height) throws IOException, NoSuchElementException {
        return SimpleAnimatedComponent.read(file, height);
    }

    /**
     * Represents an animated image component that can be displayed in a hologram.
     *
     * @since 0.5.0
     */
    public sealed interface Animated extends Iterable<Component> permits SimpleAnimatedComponent {
        /**
         * Gets the frames of the animated image component.
         *
         * @return The frames of the animated image component.
         * @since 0.5.0
         */
        @Contract(value = " -> new", pure = true)
        Component[] getFrames();

        /**
         * Gets the frame delay of the animated image component.
         *
         * @return The frame delay of the animated image component.
         * @since 0.5.0
         */
        @Contract(value = " -> new", pure = true)
        Duration getFrameDelay();
    }
}
