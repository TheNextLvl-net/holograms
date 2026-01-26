package net.thenextlvl.hologram.image;

import net.kyori.adventure.text.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class SimpleAnimatedComponent implements ImageComponent.Animated {
    private final Component[] images;
    private final Duration frameDelay;

    private SimpleAnimatedComponent(final Duration frameDelay, final Component... images) {
        this.frameDelay = frameDelay;
        this.images = images;
    }

    public static SimpleAnimatedComponent read(final Path file, final int height) throws IOException, NoSuchElementException {
        final var reader = ImageIO.getImageReadersBySuffix("GIF").next();
        final var in = ImageIO.createImageInputStream(file.toFile());
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

            images[i] = SimpleImageComponent.read(snapshot, height);
            totalDurationMs += getFrameDuration(reader, i);
        }

        final var avgFrameDelay = frameCount > 0 ? totalDurationMs / frameCount : 100;
        return new SimpleAnimatedComponent(Duration.ofMillis(avgFrameDelay), images);
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

    @Override
    public Component[] getFrames() {
        return images.clone();
    }

    public Duration getFrameDelay() {
        return frameDelay;
    }

    @Override
    public Iterator<Component> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < images.length;
            }

            @Override
            public Component next() {
                return images[i++];
            }
        };
    }
}
