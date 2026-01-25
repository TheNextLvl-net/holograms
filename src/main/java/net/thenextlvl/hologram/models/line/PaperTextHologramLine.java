package net.thenextlvl.hologram.models.line;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.thenextlvl.hologram.image.ImageTagResolver;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

@NullMarked
public class PaperTextHologramLine extends PaperDisplayHologramLine<TextHologramLine, TextDisplay> implements TextHologramLine {
    private volatile @Nullable Color backgroundColor;
    private volatile @Nullable String text = null;
    private volatile TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;
    private volatile boolean defaultBackground = false;
    private volatile boolean seeThrough = false;
    private volatile boolean shadowed = false;
    private volatile float opacity = 0;
    private volatile int lineWidth = Integer.MAX_VALUE;

    public PaperTextHologramLine(final PaperHologram hologram) {
        super(hologram, TextDisplay.class);
    }

    @Override
    public LineType getType() {
        return LineType.TEXT;
    }

    @Override
    public Optional<Component> getText(final Player player) {
        return getUnparsedText().map(string -> {
            return getHologram().getPlugin().translations().translate(player, string, 0);
        }).map(string -> {
            final var papiFormatter = getHologram().getPlugin().papiFormatter;
            return papiFormatter != null ? papiFormatter.format(player, string) : string;
        }).map(string -> {
            final var formatter = getHologram().getPlugin().miniFormatter;
            final var builder = TagResolver.builder();

            builder.resolver(StandardTags.defaults());
            builder.resolver(ImageTagResolver.INSTANCE);
            builder.tag("hologram", Tag.inserting(Component.text(getHologram().getName())));
            builder.tag("line", Tag.inserting(Component.text(getHologram().getLineIndex(this))));
            if (formatter != null) builder.resolver(formatter.tagResolver());

            return MiniMessage.miniMessage().deserialize(string, player, builder.build());
        });
    }

    @Override
    public Optional<String> getUnparsedText() {
        return Optional.ofNullable(text);
    }

    @Override
    public TextHologramLine setText(@Nullable final Component text) {
        return setUnparsedText(text != null ? MiniMessage.miniMessage().serialize(text) : null);
    }

    @Override
    public TextHologramLine setUnparsedText(@Nullable final String text) {
        if (Objects.equals(this.text, text)) return this;
        this.text = text;
        // getEntities().values().forEach(this::updateText);
        getHologram().updateHologram();
        return this;
    }

    @Override
    public int getLineWidth() {
        return lineWidth;
    }

    @Override
    public TextHologramLine setLineWidth(final int width) {
        this.lineWidth = width;
        getEntities().values().forEach(entity -> entity.setLineWidth(width));
        return this;
    }

    @Override
    public Optional<Color> getBackgroundColor() {
        return Optional.ofNullable(backgroundColor);
    }

    @Override
    public TextHologramLine setBackgroundColor(@Nullable final Color color) {
        this.backgroundColor = color;
        getEntities().values().forEach(entity -> entity.setBackgroundColor(color));
        return this;
    }

    @Override
    public float getTextOpacity() {
        return opacity;
    }

    @Override
    public TextHologramLine setTextOpacity(@Range(from = 0, to = 100) final float opacity) {
        this.opacity = opacity;
        getEntities().values().forEach(this::updateOpacity);
        return this;
    }

    @Override
    public boolean isShadowed() {
        return shadowed;
    }

    @Override
    public TextHologramLine setShadowed(final boolean shadow) {
        this.shadowed = shadow;
        getEntities().values().forEach(entity -> entity.setShadowed(shadow));
        return this;
    }

    @Override
    public boolean isSeeThrough() {
        return seeThrough;
    }

    @Override
    public TextHologramLine setSeeThrough(final boolean seeThrough) {
        this.seeThrough = seeThrough;
        getEntities().values().forEach(entity -> entity.setSeeThrough(seeThrough));
        return this;
    }

    @Override
    public boolean isDefaultBackground() {
        return defaultBackground;
    }

    @Override
    public TextHologramLine setDefaultBackground(final boolean defaultBackground) {
        this.defaultBackground = defaultBackground;
        getEntities().values().forEach(entity -> entity.setDefaultBackground(defaultBackground));
        return this;
    }

    @Override
    public TextDisplay.TextAlignment getAlignment() {
        return alignment;
    }

    @Override
    public TextHologramLine setAlignment(final TextDisplay.TextAlignment alignment) {
        this.alignment = alignment;
        getEntities().values().forEach(entity -> entity.setAlignment(alignment));
        return this;
    }

    @Override
    public double getHeight(final Player player) {
        return (0.25 * transformation.getScale().y()) * getText(player)
                .map(MiniMessage.miniMessage()::serialize)
                .map(s -> s.chars().filter(c -> c == '\n').count() + 1)
                .orElse(1L);
    }

    @Override
    protected void preSpawn(final TextDisplay entity, final Player player) {
        updateText(player, entity);
        entity.setAlignment(alignment);
        entity.setSeeThrough(seeThrough);
        entity.setShadowed(shadowed);
        entity.setDefaultBackground(defaultBackground);
        entity.setBackgroundColor(backgroundColor);
        entity.setLineWidth(lineWidth);
        updateOpacity(entity);

        super.preSpawn(entity, player);
    }

    public void updateText(final Player player, final TextDisplay entity) {
        getText(player).ifPresentOrElse(component -> {
            entity.text(Component.empty());
            entity.text(component);
        }, () -> entity.text(null));
    }

    private void updateOpacity(final TextDisplay entity) {
        entity.setTextOpacity((byte) Math.round(25f + ((100f - opacity) * 2.3f)));
    }
}
