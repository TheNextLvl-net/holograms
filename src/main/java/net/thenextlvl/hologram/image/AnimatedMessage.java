package net.thenextlvl.hologram.image;

import net.kyori.adventure.text.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class AnimatedMessage {
    private final Component[] images;
    private int index = 0;

    private AnimatedMessage(final Component... images) {
        this.images = images;
    }

    public static AnimatedMessage readGif(final File gifFile, final int height) throws IOException {
        final var frames = getFrames(gifFile);
        final var images = new Component[frames.size()];
        for (var i = 0; i < frames.size(); i++) {
            images[i] = ImageMessage.read(frames.get(i), height);
        }
        return new AnimatedMessage(images);
    }

    private static List<BufferedImage> getFrames(final File input) throws IOException {
        final var images = new ArrayList<BufferedImage>();
        final var reader = ImageIO.getImageReadersBySuffix("GIF").next();
        final var in = ImageIO.createImageInputStream(input);
        reader.setInput(in);
        for (int i = 0, count = reader.getNumImages(true); i < count; i++) {
            final var image = reader.read(i);
            images.add(image);
        }
        return images;
    }

    public Component current() {
        return images[index];
    }

    public Component next() {
        if (++index >= images.length) index = 0;
        return images[index];
    }

    public Component previous() {
        if (--index <= 0) index = images.length - 1;
        return images[index];
    }

    public Component getIndex(final int index) {
        return images[index];
    }
}
