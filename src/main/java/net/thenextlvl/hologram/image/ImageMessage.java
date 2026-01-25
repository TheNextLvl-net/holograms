package net.thenextlvl.hologram.image;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public final class ImageMessage {
    public static final String BLOCK = "█";
    public static final String DARK_SHADE = "▓";
    public static final String MEDIUM_SHADE = "▒";
    public static final String LIGHT_SHADE = "░";

    public static Component read(final BufferedImage image, final int height) {
        return toComponent(image, height);
    }

    private static Component toComponent(final BufferedImage image, final int height) {
        final var colors = toColorMatrix(image, height);
        final var lines = new Component[colors[0].length];
        for (var y = 0; y < colors[0].length; y++) {
            final var line = Component.text();
            for (final var colorData : colors) {
                final var data = colorData[y];
                final var character = getCharByAlpha(data.getAlpha());
                line.append(Component.text(character, TextColor.color(data.asRGB())));
            }
            lines[y] = line.build();
        }
        return Component.join(JoinConfiguration.newlines(), lines);
    }

    private static Color[][] toColorMatrix(final BufferedImage image, final int height) {
        final var ratio = (double) image.getHeight() / image.getWidth();
        final var resized = resizeImage(image, (int) (height / ratio), height);

        final var matrix = new Color[resized.getWidth()][resized.getHeight()];
        for (var x = 0; x < resized.getWidth(); x++) {
            for (var y = 0; y < resized.getHeight(); y++) {
                matrix[x][y] = Color.fromARGB(resized.getRGB(x, y));
            }
        }
        return matrix;
    }

    private static String getCharByAlpha(final int alpha) {
        if (alpha == 0) return "  ";
        return switch ((alpha * 4) / 256) {
            case 0 -> LIGHT_SHADE;
            case 1 -> MEDIUM_SHADE;
            case 2 -> DARK_SHADE;
            default -> BLOCK;
        };
    }

    private static BufferedImage resizeImage(final BufferedImage originalImage, final int width, final int height) {
        final var transform = new AffineTransform();
        final var sx = width / (double) originalImage.getWidth();
        final var sy = height / (double) originalImage.getHeight();
        transform.scale(sx, sy);

        final var operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return operation.filter(originalImage, null);
    }
}
