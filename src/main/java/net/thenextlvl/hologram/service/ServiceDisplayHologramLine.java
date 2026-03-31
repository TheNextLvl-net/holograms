package net.thenextlvl.hologram.service;

import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.hologram.line.DisplayHologramLine;
import net.thenextlvl.service.api.hologram.line.PagedHologramLine;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Transformation;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
abstract class ServiceDisplayHologramLine<L extends DisplayHologramLine> extends ServiceHologramLine<L> implements net.thenextlvl.service.api.hologram.line.DisplayHologramLine {
    protected ServiceDisplayHologramLine(final ServiceHologram hologram, final L line) {
        super(hologram, line);
    }

    @Override
    public Transformation getTransformation() {
        return line.getTransformation();
    }

    @Override
    public boolean setTransformation(final Transformation transformation) {
        return line.setTransformation(transformation);
    }

    @Override
    public boolean setTransformationMatrix(final Matrix4f transformationMatrix) {
        return line.setTransformationMatrix(transformationMatrix);
    }

    @Override
    public int getInterpolationDuration() {
        return line.getInterpolationDuration();
    }

    @Override
    public boolean setInterpolationDuration(final int duration) {
        return line.setInterpolationDuration(duration);
    }

    @Override
    public int getTeleportDuration() {
        return line.getTeleportDuration();
    }

    @Override
    public boolean setTeleportDuration(final int duration) {
        return line.setTeleportDuration(duration);
    }

    @Override
    public float getViewRange() {
        return line.getViewRange();
    }

    @Override
    public boolean setViewRange(final float range) {
        return line.setViewRange(range);
    }

    @Override
    public float getShadowRadius() {
        return line.getShadowRadius();
    }

    @Override
    public boolean setShadowRadius(final float radius) {
        return line.setShadowRadius(radius);
    }

    @Override
    public float getShadowStrength() {
        return line.getShadowStrength();
    }

    @Override
    public boolean setShadowStrength(final float strength) {
        return line.setShadowStrength(strength);
    }

    @Override
    public float getDisplayWidth() {
        return line.getDisplayWidth();
    }

    @Override
    public boolean setDisplayWidth(final float width) {
        return line.setDisplayWidth(width);
    }

    @Override
    public float getDisplayHeight() {
        return line.getDisplayHeight();
    }

    @Override
    public boolean setDisplayHeight(final float height) {
        return line.setDisplayHeight(height);
    }

    @Override
    public int getInterpolationDelay() {
        return line.getInterpolationDelay();
    }

    @Override
    public boolean setInterpolationDelay(final int ticks) {
        return line.setInterpolationDelay(ticks);
    }

    @Override
    public Optional<Display.Brightness> getBrightness() {
        return line.getBrightness();
    }

    @Override
    public boolean setBrightness(final Display.@Nullable Brightness brightness) {
        return line.setBrightness(brightness);
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
        return line.getParentLine().map(pagedLine -> new ServicePagedHologramLine(hologram, pagedLine));
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
