package net.thenextlvl.hologram.image;

import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public final class ImageTagResolver implements TagResolver {
    public static final int DEFAULT_HEIGHT = 8;
    public static final Duration CACHE_TTL = Duration.ofMinutes(30);

    public static final ImageTagResolver INSTANCE = new ImageTagResolver();

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    private ImageTagResolver() {
    }

    private record CacheEntry(BufferedImage image, Instant expiry) {
        boolean isExpired() {
            return Instant.now().isAfter(expiry);
        }
    }

    @Override
    public Tag resolve(final String name, final ArgumentQueue arguments, final Context ctx) throws ParsingException {
        if (!arguments.hasNext()) {
            throw ctx.newException("The image tag requires a source argument");
        }

        final var source = arguments.pop().value();
        final var height = arguments.hasNext() ? parseHeight(arguments.pop().value(), ctx) : DEFAULT_HEIGHT;
        final var cacheKey = source + ":" + height;

        final var cached = cache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return Tag.inserting(ImageMessage.read(cached.image(), height));
        }

        try {
            final var image = resolveImage(source, height);
            cache.put(cacheKey, new CacheEntry(image, Instant.now().plus(CACHE_TTL)));
            return Tag.inserting(ImageMessage.read(image, height));
        } catch (final IOException e) {
            throw ctx.newException("Failed to load image: " + e.getMessage());
        }
    }

    @Override
    public boolean has(final String name) {
        return "image".equals(name);
    }

    @SuppressWarnings("HttpUrlsUsage")
    private BufferedImage resolveImage(final String source, final int height) throws IOException {
        if (source.startsWith("http://") || source.startsWith("https://")) {
            final var image = ImageIO.read(URI.create(source).toURL());
            if (image == null) throw new IOException("Could not read image from URL: " + source);
            return image;
        }
        return ImageIO.read(Files.newInputStream(Path.of(source)));
    }

    private int parseHeight(final String value, final Context ctx) throws ParsingException {
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            throw ctx.newException("Invalid height value: " + value);
        }
    }
}
