package net.thenextlvl.hologram.image;

import net.kyori.adventure.text.Component;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

public final class AnimatedMessage {
    private final Component[] images;
    private final Duration frameDelay;
    private int index = 0;

    private AnimatedMessage(final Duration frameDelay, final Component... images) {
        this.frameDelay = frameDelay;
        this.images = images;
    }

    public static AnimatedMessage readGif(final Path gifFile, final int height) throws IOException {
        final var reader = ImageIO.getImageReadersBySuffix("GIF").next();
        final var in = ImageIO.createImageInputStream(gifFile.toFile());
        reader.setInput(in);

        final var frameCount = reader.getNumImages(true);
        final var images = new Component[frameCount];

        var totalDelayMs = 0L;
        for (var i = 0; i < frameCount; i++) {
            images[i] = ImageMessage.read(reader.read(i), height);
            totalDelayMs += getFrameDelay(reader, i);
        }

        final var avgDelayMs = frameCount > 0 ? totalDelayMs / frameCount : 100L;

        return new AnimatedMessage(Duration.ofMillis(avgDelayMs), images);
    }

    private static int getFrameDelay(final javax.imageio.ImageReader reader, final int frameIndex) throws IOException {
        final var metadata = reader.getImageMetadata(frameIndex);
        final var root = metadata.getAsTree("javax_imageio_gif_image_1.0");
        final var children = root.getChildNodes();
        for (var i = 0; i < children.getLength(); i++) {
            final var node = children.item(i);
            if ("GraphicControlExtension".equals(node.getNodeName())) {
                final var delayAttr = node.getAttributes().getNamedItem("delayTime");
                if (delayAttr != null) {
                    return Integer.parseInt(delayAttr.getNodeValue()) * 10;
                }
            }
        }
        return 100;
    }

    public Duration getFrameDelay() {
        return frameDelay;
    }

    public boolean hasNext() {
        return index < images.length;
    }

    public Component next() {
        return images[index++];
    }

    public Component previous() {
        return images[--index];
    }
}
