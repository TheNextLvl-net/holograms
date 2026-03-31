package net.thenextlvl.hologram.service;

import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.service.api.hologram.line.PagedHologramLine;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public final class ServiceEntityHologramLine extends ServiceHologramLine<EntityHologramLine> implements net.thenextlvl.service.api.hologram.line.EntityHologramLine {
    public ServiceEntityHologramLine(final ServiceHologram hologram, final EntityHologramLine line) {
        super(hologram, line);
    }

    @Override
    public boolean setEntityType(final EntityType entityType) {
        return line.setEntityType(entityType);
    }

    @Override
    public double getScale() {
        return line.getScale();
    }

    @Override
    public boolean setScale(final double scale) {
        return line.setScale(scale);
    }

    @Override
    public Class<? extends Entity> getEntityClass() {
        return line.getEntityClass();
    }

    @Override
    public EntityType getEntityType() {
        return line.getEntityType();
    }

    @Override
    public boolean isGlowing() {
        return line.isGlowing();
    }

    @Override
    public boolean setGlowing(final boolean glowing) {
        return line.setGlowing(glowing);
    }

    @Override
    public Optional<TextColor> getGlowColor() {
        return line.getGlowColor();
    }

    @Override
    public boolean setGlowColor(@Nullable final TextColor color) {
        return line.setGlowColor(color);
    }

    @Override
    public Display.Billboard getBillboard() {
        return line.getBillboard();
    }

    @Override
    public boolean setBillboard(final Display.Billboard billboard) {
        return line.setBillboard(billboard);
    }

    @Override
    public Optional<PagedHologramLine> getParentLine() {
        return line.getParentLine().map(line -> new ServicePagedHologramLine(hologram, line));
    }

    @Override
    public Vector3f getOffset() {
        return line.getOffset();
    }

    @Override
    public boolean setOffset(final Vector3f offset) {
        return line.setOffset(offset);
    }
}
