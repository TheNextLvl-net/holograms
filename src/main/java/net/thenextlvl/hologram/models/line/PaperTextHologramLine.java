package net.thenextlvl.hologram.models.line;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Color;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

@NullMarked
public class PaperTextHologramLine extends PaperDisplayHologramLine<TextHologramLine, TextDisplay> implements TextHologramLine {
    private @Nullable Color backgroundColor;
    private @Nullable Component text = null;
    private TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;
    private boolean defaultBackground = false;
    private boolean seeThrough = false;
    private boolean shadowed = false;
    private float opacity = 0;
    private int lineWidth = Integer.MAX_VALUE;

    public PaperTextHologramLine(final PaperHologram hologram) {
        super(hologram, TextDisplay.class);
    }

    @Override
    public LineType getType() {
        return LineType.TEXT;
    }

    @Override
    public Optional<Component> getText() {
        return Optional.ofNullable(text);
    }

    @Override
    public TextHologramLine setText(@Nullable final Component text) {
        if (Objects.equals(this.text, text)) return this;
        this.text = text;
        getEntity().ifPresent(entity -> entity.text(text));
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
        getEntity().ifPresent(entity -> entity.setLineWidth(width));
        return this;
    }

    @Override
    public Optional<Color> getBackgroundColor() {
        return Optional.ofNullable(backgroundColor);
    }

    @Override
    public TextHologramLine setBackgroundColor(@Nullable final Color color) {
        this.backgroundColor = color;
        getEntity().ifPresent(entity -> entity.setBackgroundColor(color));
        return this;
    }

    @Override
    public float getTextOpacity() {
        return opacity;
    }

    @Override
    public TextHologramLine setTextOpacity(@Range(from = 0, to = 100) final float opacity) {
        this.opacity = opacity;
        getEntity().ifPresent(this::updateOpacity);
        return this;
    }

    @Override
    public boolean isShadowed() {
        return shadowed;
    }

    @Override
    public TextHologramLine setShadowed(final boolean shadow) {
        this.shadowed = shadow;
        getEntity().ifPresent(entity -> entity.setShadowed(shadow));
        return this;
    }

    @Override
    public boolean isSeeThrough() {
        return seeThrough;
    }

    @Override
    public TextHologramLine setSeeThrough(final boolean seeThrough) {
        this.seeThrough = seeThrough;
        getEntity().ifPresent(entity -> entity.setSeeThrough(seeThrough));
        return this;
    }

    @Override
    public boolean isDefaultBackground() {
        return defaultBackground;
    }

    @Override
    public TextHologramLine setDefaultBackground(final boolean defaultBackground) {
        this.defaultBackground = defaultBackground;
        getEntity().ifPresent(entity -> entity.setDefaultBackground(defaultBackground));
        return this;
    }

    @Override
    public TextDisplay.TextAlignment getAlignment() {
        return alignment;
    }

    @Override
    public TextHologramLine setAlignment(final TextDisplay.TextAlignment alignment) {
        this.alignment = alignment;
        getEntity().ifPresent(entity -> entity.setAlignment(alignment));
        return this;
    }

    @Override
    public double getHeight() {
        final var deserialize = text != null ? MiniMessage.miniMessage().serialize(text) : null;
        final var lines = deserialize != null ? deserialize.split("\n|<br>|<newline>").length : 1;
        return (0.25 * transformation.getScale().y()) * lines;
    }

    @Override
    protected void preSpawn(final TextDisplay entity) {
        entity.text(text);
        entity.setAlignment(alignment);
        entity.setSeeThrough(seeThrough);
        entity.setShadowed(shadowed);
        entity.setDefaultBackground(defaultBackground);
        entity.setBackgroundColor(backgroundColor);
        entity.setLineWidth(lineWidth);
        updateOpacity(entity);

        super.preSpawn(entity);
    }

    private void updateOpacity(final TextDisplay entity) {
        entity.setTextOpacity((byte) Math.round(25f + ((100f - opacity) * 2.3f)));
    }
}
