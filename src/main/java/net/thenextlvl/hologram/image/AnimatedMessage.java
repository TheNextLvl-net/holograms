package net.thenextlvl.hologram.image;

import net.kyori.adventure.text.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

        final var gifWidth = reader.getWidth(0);
        final var gifHeight = reader.getHeight(0);
        final var canvas = new BufferedImage(gifWidth, gifHeight, BufferedImage.TYPE_INT_ARGB);

        var totalDurationMs = 0L;
        for (var i = 0; i < frameCount; i++) {
            final var frame = reader.read(i);
            final var frameOffset = getFrameOffset(reader, i);
            final var graphics = canvas.createGraphics();
            graphics.drawImage(frame, frameOffset[0], frameOffset[1], null);
            graphics.dispose();

            final var snapshot = new BufferedImage(gifWidth, gifHeight, BufferedImage.TYPE_INT_ARGB);
            snapshot.createGraphics().drawImage(canvas, 0, 0, null);

            images[i] = ImageMessage.read(snapshot, height);
            totalDurationMs += getFrameDuration(reader, i);
        }

        final var avgFrameDelay = frameCount > 0 ? totalDurationMs / frameCount : 100;
        return new AnimatedMessage(Duration.ofMillis(avgFrameDelay), images);
    }

    private static int[] getFrameOffset(final javax.imageio.ImageReader reader, final int frameIndex) throws IOException {
        final var metadata = reader.getImageMetadata(frameIndex);
        final var root = metadata.getAsTree("javax_imageio_gif_image_1.0");
        final var children = root.getChildNodes();
        for (var i = 0; i < children.getLength(); i++) {
            final var node = children.item(i);
            if ("ImageDescriptor".equals(node.getNodeName())) {
                final var attrs = node.getAttributes();
                final var left = Integer.parseInt(attrs.getNamedItem("imageLeftPosition").getNodeValue());
                final var top = Integer.parseInt(attrs.getNamedItem("imageTopPosition").getNodeValue());
                return new int[]{left, top};
            }
        }
        return new int[]{0, 0};
    }

    private static int getFrameDuration(final javax.imageio.ImageReader reader, final int frameIndex) throws IOException {
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
