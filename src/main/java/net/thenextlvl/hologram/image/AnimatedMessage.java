package net.thenextlvl.hologram.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class AnimatedMessage {
    private final ImageMessage[] images;
    private int index = 0;

    public AnimatedMessage(final ImageMessage... images) {
        this.images = images;
    }

    public AnimatedMessage(final File gifFile, final int height, final char imgChar) {
        final List<BufferedImage> frames = getFrames(gifFile);
        images = new ImageMessage[frames.size()];
        for (int i = 0; i < frames.size(); i++) {
            images[i] = new ImageMessage(frames.get(i), height, imgChar);
        }
    }

    public List<BufferedImage> getFrames(final File input) {
        final var images = new ArrayList<BufferedImage>();
        try {
            final var reader = ImageIO.getImageReadersBySuffix("GIF").next();
            final var in = ImageIO.createImageInputStream(input);
            reader.setInput(in);
            for (int i = 0, count = reader.getNumImages(true); i < count; i++) {
                final var image = reader.read(i);
                images.add(image);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        return images;
    }

    public ImageMessage current() {
        return images[index];
    }

    public ImageMessage next() {
        if (++index >= images.length) index = 0;
        return images[index];
    }

    public ImageMessage previous() {
        if (--index <= 0) index = images.length - 1;
        return images[index];
    }

    public ImageMessage getIndex(final int index) {
        return images[index];
    }
}
