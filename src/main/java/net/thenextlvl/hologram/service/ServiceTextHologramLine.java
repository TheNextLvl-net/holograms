package net.thenextlvl.hologram.service;

import net.kyori.adventure.text.Component;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public final class ServiceTextHologramLine extends ServiceDisplayHologramLine<TextHologramLine> implements net.thenextlvl.service.api.hologram.line.TextHologramLine {
    public ServiceTextHologramLine(final ServiceHologram hologram, final TextHologramLine line) {
        super(hologram, line);
    }

    @Override
    public Optional<Component> getText(final Player player) {
        return line.getText(player);
    }

    @Override
    public Optional<String> getUnparsedText() {
        return line.getUnparsedText();
    }

    @Override
    public boolean setText(@Nullable final Component text) {
        return line.setText(text);
    }

    @Override
    public boolean setUnparsedText(@Nullable final String text) {
        return line.setUnparsedText(text);
    }

    @Override
    public int getLineWidth() {
        return line.getLineWidth();
    }

    @Override
    public boolean setLineWidth(final int width) {
        return line.setLineWidth(width);
    }

    @Override
    public Optional<Color> getBackgroundColor() {
        return line.getBackgroundColor();
    }

    @Override
    public boolean setBackgroundColor(@Nullable final Color color) {
        return line.setBackgroundColor(color);
    }

    @Override
    public int getTextOpacity() {
        return line.getTextOpacity();
    }

    @Override
    public boolean setTextOpacity(final int opacity) {
        return line.setTextOpacity(opacity);
    }

    @Override
    public boolean isShadowed() {
        return line.isShadowed();
    }

    @Override
    public boolean setShadowed(final boolean shadow) {
        return line.setShadowed(shadow);
    }

    @Override
    public boolean isSeeThrough() {
        return line.isSeeThrough();
    }

    @Override
    public boolean setSeeThrough(final boolean seeThrough) {
        return line.setSeeThrough(seeThrough);
    }

    @Override
    public boolean isDefaultBackground() {
        return line.isDefaultBackground();
    }

    @Override
    public boolean setDefaultBackground(final boolean defaultBackground) {
        return line.setDefaultBackground(defaultBackground);
    }

    @Override
    public TextDisplay.TextAlignment getAlignment() {
        return line.getAlignment();
    }

    @Override
    public boolean setAlignment(final TextDisplay.TextAlignment alignment) {
        return line.setAlignment(alignment);
    }
}
