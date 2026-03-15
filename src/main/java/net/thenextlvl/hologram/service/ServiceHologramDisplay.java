package net.thenextlvl.hologram.service;

import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.hologram.line.DisplayHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.service.api.hologram.HologramDisplay;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Matrix4f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record ServiceHologramDisplay(DisplayHologramLine line) implements HologramDisplay {
    @Override
    public Transformation getTransformation() {
        return line.getTransformation();
    }

    @Override
    public void setTransformation(final Transformation transformation) {
        line.setTransformation(transformation);
    }

    @Override
    public void setTransformationMatrix(final Matrix4f transformationMatrix) {
        line.setTransformationMatrix(transformationMatrix);
    }

    @Override
    public int getInterpolationDuration() {
        return line.getInterpolationDuration();
    }

    @Override
    public void setInterpolationDuration(final int duration) {
        line.setInterpolationDuration(duration);
    }

    @Override
    public int getTeleportDuration() {
        return line.getTeleportDuration();
    }

    @Override
    public void setTeleportDuration(final int duration) {
        line.setTeleportDuration(duration);
    }

    @Override
    public float getViewRange() {
        return line.getViewRange();
    }

    @Override
    public void setViewRange(final float range) {
        line.setViewRange(range);
    }

    @Override
    public float getShadowRadius() {
        return line.getShadowRadius();
    }

    @Override
    public void setShadowRadius(final float radius) {
        line.setShadowRadius(radius);
    }

    @Override
    public float getShadowStrength() {
        return line.getShadowStrength();
    }

    @Override
    public void setShadowStrength(final float strength) {
        line.setShadowStrength(strength);
    }

    @Override
    public float getDisplayWidth() {
        return line.getDisplayWidth();
    }

    @Override
    public void setDisplayWidth(final float width) {
        line.setDisplayWidth(width);
    }

    @Override
    public float getDisplayHeight() {
        return line.getDisplayHeight();
    }

    @Override
    public void setDisplayHeight(final float height) {
        line.setDisplayHeight(height);
    }

    @Override
    public int getInterpolationDelay() {
        return line.getInterpolationDelay();
    }

    @Override
    public void setInterpolationDelay(final int ticks) {
        line.setInterpolationDelay(ticks);
    }

    @Override
    public Display.Billboard getBillboard() {
        return line.getBillboard();
    }

    @Override
    public void setBillboard(final Display.Billboard billboard) {
        line.setBillboard(billboard);
    }

    @Override
    public @Nullable Color getGlowColorOverride() {
        return line.getGlowColor().map(TextColor::value).map(Color::fromRGB).orElse(null);
    }

    @Override
    public void setGlowColorOverride(@Nullable final Color color) {
        line.setGlowColor(color != null ? TextColor.color(color.asRGB()) : null);
    }

    @Override
    public Display.@Nullable Brightness getBrightness() {
        return line.getBrightness().orElse(null);
    }

    @Override
    public void setBrightness(final Display.@Nullable Brightness brightness) {
        line.setBrightness(brightness);
    }

    @Override
    public int getLineWidth() {
        return line instanceof final TextHologramLine textLine ? textLine.getLineWidth() : 0;
    }

    @Override
    public void setLineWidth(final int width) {
        if (line instanceof final TextHologramLine textLine) textLine.setLineWidth(width);
    }

    @Override
    public @Nullable Color getBackgroundColor() {
        return line instanceof final TextHologramLine textLine ? textLine.getBackgroundColor().orElse(null) : null;
    }

    @Override
    public void setBackgroundColor(@Nullable final Color color) {
        if (line instanceof final TextHologramLine textLine) textLine.setBackgroundColor(color);
    }

    @Override
    public byte getTextOpacity() {
        if (!(line instanceof final TextHologramLine textLine)) return (byte) -1;
        final var percent = textLine.getTextOpacity();
        return (byte) Math.round(25f + ((100f - percent) * 2.3f));
    }

    @Override
    public void setTextOpacity(final byte opacity) {
        if (!(line instanceof final TextHologramLine textLine)) return;
        final var percent = Math.round(100f - ((Byte.toUnsignedInt(opacity) - 25f) / 2.3f));
        textLine.setTextOpacity(Math.clamp(percent, 0, 100));
    }

    @Override
    public boolean isShadowed() {
        return line instanceof final TextHologramLine textLine && textLine.isShadowed();
    }

    @Override
    public void setShadowed(final boolean shadow) {
        if (line instanceof final TextHologramLine textLine) textLine.setShadowed(shadow);
    }

    @Override
    public boolean isSeeThrough() {
        return line instanceof final TextHologramLine textLine && textLine.isSeeThrough();
    }

    @Override
    public void setSeeThrough(final boolean seeThrough) {
        if (line instanceof final TextHologramLine textLine) textLine.setSeeThrough(seeThrough);
    }

    @Override
    public boolean isDefaultBackground() {
        return line instanceof final TextHologramLine textLine && textLine.isDefaultBackground();
    }

    @Override
    public void setDefaultBackground(final boolean defaultBackground) {
        if (line instanceof final TextHologramLine textLine) textLine.setDefaultBackground(defaultBackground);
    }

    @Override
    public TextDisplay.TextAlignment getAlignment() {
        return line instanceof final TextHologramLine textLine ? textLine.getAlignment() : TextDisplay.TextAlignment.CENTER;
    }

    @Override
    public void setAlignment(final TextDisplay.TextAlignment alignment) {
        if (line instanceof final TextHologramLine textLine) textLine.setAlignment(alignment);
    }
}
