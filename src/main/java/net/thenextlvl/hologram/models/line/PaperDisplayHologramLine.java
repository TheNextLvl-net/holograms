package net.thenextlvl.hologram.models.line;

import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.hologram.line.DisplayHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public abstract class PaperDisplayHologramLine<E extends Display> extends PaperStaticHologramLine<E> implements DisplayHologramLine {
    protected volatile @Nullable Brightness brightness = null;
    protected volatile Transformation transformation = new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1), new AxisAngle4f());
    protected volatile float displayHeight = 0;
    protected volatile float displayWidth = 0;
    protected volatile float shadowRadius = 0;
    protected volatile float shadowStrength = 1;
    protected volatile float viewRange = 1;
    protected volatile int interpolationDelay = 0;
    protected volatile int interpolationDuration = 0;
    protected volatile int teleportDuration = 0;

    public PaperDisplayHologramLine(final PaperHologram hologram, @Nullable final PagedHologramLine parentLine, final EntityType entityType) {
        super(hologram, parentLine, entityType);
    }

    @Override
    public Vector3f getOffset() {
        return new Vector3f(transformation.getTranslation());
    }

    @Override
    public DisplayHologramLine setOffset(final Vector3f offset) {
        return setTransformation(new Transformation(
                offset,
                transformation.getLeftRotation(),
                transformation.getScale(),
                transformation.getRightRotation()
        ));
    }

    @Override
    public Transformation getTransformation() {
        return new Transformation(
                transformation.getTranslation(),
                transformation.getLeftRotation(),
                transformation.getScale(),
                transformation.getRightRotation()
        );
    }

    @Override
    public DisplayHologramLine setTransformation(final Transformation transformation) {
        return set(this.transformation, transformation, () -> {
            this.transformation = new Transformation(
                    transformation.getTranslation(),
                    transformation.getLeftRotation(),
                    transformation.getScale(),
                    transformation.getRightRotation()
            );
        }, true);
    }

    @Override
    public DisplayHologramLine setTransformationMatrix(final Matrix4f transformationMatrix) {
        return setTransformation(new Transformation(
                transformationMatrix.getTranslation(new Vector3f()),
                transformationMatrix.getRotation(new AxisAngle4f()),
                transformationMatrix.getScale(new Vector3f()),
                transformationMatrix.getRotation(new AxisAngle4f())
        ));
    }

    @Override
    public int getInterpolationDuration() {
        return interpolationDuration;
    }

    @Override
    public DisplayHologramLine setInterpolationDuration(final int duration) {
        return set(this.interpolationDuration, duration, () -> {
            this.interpolationDuration = duration;
            forEachEntity(entity -> entity.setInterpolationDuration(duration));
        }, false);
    }

    @Override
    public int getTeleportDuration() {
        return teleportDuration;
    }

    @Override
    public DisplayHologramLine setTeleportDuration(final int duration) {
        return set(this.teleportDuration, duration, () -> {
            this.teleportDuration = duration;
            forEachEntity(entity -> entity.setTeleportDuration(duration));
        }, false);
    }

    @Override
    public float getViewRange() {
        return viewRange;
    }

    @Override
    public DisplayHologramLine setViewRange(final float range) {
        return set(this.viewRange, range, () -> {
            this.viewRange = range;
            forEachEntity(entity -> entity.setViewRange(range));
        }, false);
    }

    @Override
    public float getShadowRadius() {
        return shadowRadius;
    }

    @Override
    public DisplayHologramLine setShadowRadius(final float radius) {
        return set(this.shadowRadius, radius, () -> {
            this.shadowRadius = radius;
            forEachEntity(entity -> entity.setShadowRadius(radius));
        }, false);
    }

    @Override
    public float getShadowStrength() {
        return shadowStrength;
    }

    @Override
    public DisplayHologramLine setShadowStrength(final float strength) {
        return set(this.shadowStrength, strength, () -> {
            this.shadowStrength = strength;
            forEachEntity(entity -> entity.setShadowStrength(strength));
        }, false);
    }

    @Override
    public float getDisplayWidth() {
        return displayWidth;
    }

    @Override
    public DisplayHologramLine setDisplayWidth(final float width) {
        return set(this.displayWidth, width, () -> {
            this.displayWidth = width;
            forEachEntity(entity -> entity.setDisplayWidth(width));
        }, false);
    }

    @Override
    public float getDisplayHeight() {
        return displayHeight;
    }

    @Override
    public DisplayHologramLine setDisplayHeight(final float height) {
        return set(this.displayHeight, height, () -> {
            this.displayHeight = height;
            forEachEntity(entity -> entity.setDisplayHeight(height));
        }, true);
    }

    @Override
    public int getInterpolationDelay() {
        return interpolationDelay;
    }

    @Override
    public DisplayHologramLine setInterpolationDelay(final int ticks) {
        return set(this.interpolationDelay, ticks, () -> {
            this.interpolationDelay = ticks;
            forEachEntity(entity -> entity.setInterpolationDelay(ticks));
        }, false);
    }

    @Override
    public DisplayHologramLine setBillboard(final Display.Billboard billboard) {
        return set(this.billboard, billboard, () -> {
            this.billboard = billboard;
            forEachEntity(entity -> entity.setBillboard(billboard));
        }, false);
    }

    @Override
    protected void updateGlowColor(@Nullable final TextColor color) {
        final var override = color != null ? Color.fromRGB(color.value()) : null;
        forEachEntity(entity -> entity.setGlowColorOverride(override));
    }

    @Override
    public Optional<Display.Brightness> getBrightness() {
        return Optional.ofNullable(brightness);
    }

    @Override
    public DisplayHologramLine setBrightness(final Display.@Nullable Brightness brightness) {
        return set(this.brightness, brightness, () -> {
            this.brightness = brightness;
            forEachEntity(entity -> entity.setBrightness(brightness));
        }, false);
    }

    @Override
    protected void preSpawn(final E entity, final Player player) {
        final var glowColor = getGlowColor()
                .map(TextColor::value)
                .map(Color::fromRGB)
                .orElse(null);

        entity.setBillboard(billboard);
        entity.setBrightness(brightness);
        entity.setDisplayHeight(displayHeight);
        entity.setDisplayWidth(displayWidth);
        entity.setGlowColorOverride(glowColor);
        entity.setInterpolationDelay(interpolationDelay);
        entity.setInterpolationDuration(interpolationDuration);
        entity.setShadowRadius(shadowRadius);
        entity.setShadowStrength(shadowStrength);
        entity.setTeleportDuration(teleportDuration);
        entity.setTransformation(transformation);
        entity.setViewRange(viewRange);

        super.preSpawn(entity, player);
    }

    @Override
    public HologramLine copyFrom(final HologramLine other) {
        if (other instanceof final DisplayHologramLine line) {
            brightness = line.getBrightness().orElse(null);
            displayHeight = line.getDisplayHeight();
            displayWidth = line.getDisplayWidth();
            interpolationDelay = line.getInterpolationDelay();
            interpolationDuration = line.getInterpolationDuration();
            shadowRadius = line.getShadowRadius();
            shadowStrength = line.getShadowStrength();
            teleportDuration = line.getTeleportDuration();
            transformation = line.getTransformation();
            viewRange = line.getViewRange();
        }
        return super.copyFrom(other);
    }
}
