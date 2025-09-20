package net.thenextlvl.hologram.model.line;

import net.kyori.adventure.text.Component;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.model.PaperHologram;
import org.bukkit.Color;
import org.bukkit.entity.TextDisplay;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class PaperTextHologramLine extends PaperDisplayHologramLine<TextDisplay> implements TextHologramLine {
    private @Nullable Color backgroundColor;
    private Component text = Component.empty();
    private TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;
    private boolean defaultBackground = false;
    private boolean seeThrough = false;
    private boolean shadowed = false;
    private byte opacity = 0;
    private int lineWidth = 0;

    public PaperTextHologramLine(PaperHologram hologram) {
        super(hologram, TextDisplay.class);
    }

    @Override
    public LineType getType() {
        return LineType.TEXT;
    }

    @Override
    public Component getText() {
        return text;
    }

    @Override
    public void setText(@Nullable Component text) {
        this.text = text == null ? Component.empty() : text;
        getEntity().ifPresent(entity -> entity.text(text));
    }

    @Override
    public int getLineWidth() {
        return lineWidth;
    }

    @Override
    public void setLineWidth(int width) {
        this.lineWidth = width;
        getEntity().ifPresent(entity -> entity.setLineWidth(width));
    }

    @Override
    public @Nullable Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(@Nullable Color color) {
        this.backgroundColor = color;
        getEntity().ifPresent(entity -> entity.setBackgroundColor(color));
    }

    @Override
    public byte getTextOpacity() {
        return opacity;
    }

    @Override
    public void setTextOpacity(byte opacity) {
        this.opacity = opacity;
        getEntity().ifPresent(entity -> entity.setTextOpacity(opacity));
    }

    @Override
    public boolean isShadowed() {
        return shadowed;
    }

    @Override
    public void setShadowed(boolean shadow) {
        this.shadowed = shadow;
        getEntity().ifPresent(entity -> entity.setShadowed(shadow));
    }

    @Override
    public boolean isSeeThrough() {
        return seeThrough;
    }

    @Override
    public void setSeeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        getEntity().ifPresent(entity -> entity.setSeeThrough(seeThrough));
    }

    @Override
    public boolean isDefaultBackground() {
        return defaultBackground;
    }

    @Override
    public void setDefaultBackground(boolean defaultBackground) {
        this.defaultBackground = defaultBackground;
        getEntity().ifPresent(entity -> entity.setDefaultBackground(defaultBackground));
    }

    @Override
    public TextDisplay.TextAlignment getAlignment() {
        return alignment;
    }

    @Override
    public void setAlignment(TextDisplay.TextAlignment alignment) {
        this.alignment = alignment;
        getEntity().ifPresent(entity -> entity.setAlignment(alignment));
    }

    @Override
    protected void preSpawn(TextDisplay entity) {
        entity.text(text);
        entity.setAlignment(alignment);
        entity.setSeeThrough(seeThrough);
        entity.setShadowed(shadowed);
        entity.setDefaultBackground(defaultBackground);
        entity.setTextOpacity(opacity);
        if (backgroundColor != null) entity.setBackgroundColor(backgroundColor);
        if (lineWidth > 0) entity.setLineWidth(lineWidth);
        
        super.preSpawn(entity);
    }
}
