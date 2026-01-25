package net.thenextlvl.hologram.image;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageMessage {
    private final TextComponent[] lines;

    public ImageMessage(final BufferedImage image, final int height, final char imgChar) {
        final var chatColors = toChatColorArray(image, height);
        lines = toImgMessage(chatColors);
    }

    public ImageMessage(final TextColor[][] chatColors, final char imgChar) {
        lines = toImgMessage(chatColors, imgChar);
    }

    private Color[][] toChatColorArray(final BufferedImage image, final int height) {
        final var ratio = (double) image.getHeight() / image.getWidth();
        final var resized = resizeImage(image, (int) (height / ratio), height);

        final var chatImg = new Color[resized.getWidth()][resized.getHeight()];
        for (var x = 0; x < resized.getWidth(); x++) {
            for (var y = 0; y < resized.getHeight(); y++) {
                final var argb = resized.getRGB(x, y);
                chatImg[x][y] = Color.fromARGB(argb);
            }
        }
        return chatImg;
    }

    private TextComponent[] toImgMessage(final Color[][] colors) {
        final var lines = new TextComponent[colors[0].length];
        for (var y = 0; y < colors[0].length; y++) {
            final var line = Component.text();
            for (final var colorData : colors) {
                final var data = colorData[y];
                final var character = getCharByAlpha(data.getAlpha());
                line.append(Component.text(character, TextColor.color(data.asRGB())));
            }
            lines[y] = line.build();
        }
        return lines;
    }

    private TextComponent[] toImgMessage(final TextColor[][] colors, final char imgChar) {
        final var lines = new TextComponent[colors[0].length];
        for (var y = 0; y < colors[0].length; y++) {
            final var line = Component.text();
            for (final var textColors : colors) {
                final var color = textColors[y];
                line.append(Component.text(color != null ? imgChar : ' ', textColors[y]));
            }
            lines[y] = line.build();
        }
        return lines;
    }

    private char getCharByAlpha(final int alpha) {
        if (alpha == 0) return ' ';
        return switch ((alpha * 4) / 256) {
            case 0 -> ImageChar.LIGHT_SHADE;
            case 1 -> ImageChar.MEDIUM_SHADE;
            case 2 -> ImageChar.DARK_SHADE;
            default -> ImageChar.BLOCK;
        };
    }

    private BufferedImage resizeImage(final BufferedImage originalImage, final int width, final int height) {
        final var af = new AffineTransform();
        final var sx = width / (double) originalImage.getWidth();
        final var sy = height / (double) originalImage.getHeight();
        af.scale(sx, sy);

        final var operation = new AffineTransformOp(af, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return operation.filter(originalImage, null);
    }

    public TextComponent[] getLines() {
        return lines;
    }

    public void sendToPlayer(final Player player) {
        for (final TextComponent line : lines) {
            player.sendMessage(line);
        }
    }
}
