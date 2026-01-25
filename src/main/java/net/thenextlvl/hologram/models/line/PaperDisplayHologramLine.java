package net.thenextlvl.hologram.models.line;

import net.thenextlvl.hologram.line.DisplayHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

@NullMarked
public abstract class PaperDisplayHologramLine<T extends DisplayHologramLine<T, E>, E extends Display> extends PaperHologramLine<E> implements DisplayHologramLine<T, E> {
    protected volatile @Nullable Color glowColorOverride = null;
    protected volatile Display.@Nullable Brightness brightness = null;
    protected volatile Display.Billboard billboard = Display.Billboard.CENTER;
    protected volatile Transformation transformation = new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1), new AxisAngle4f());
    protected volatile float displayHeight = 0;
    protected volatile float displayWidth = 0;
    protected volatile float shadowRadius = 0;
    protected volatile float shadowStrength = 1;
    protected volatile float viewRange = 1;
    protected volatile int interpolationDelay = 0;
    protected volatile int interpolationDuration = 0;
    protected volatile int teleportDuration = 4;

    public PaperDisplayHologramLine(final PaperHologram hologram, final Class<E> entityClass) {
        super(hologram, entityClass);
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
    public T setTransformation(final Transformation transformation) {
        if (Objects.equals(this.transformation, transformation)) return getSelf();
        this.transformation = new Transformation(
                new Vector3f(transformation.getTranslation()),
                new AxisAngle4f(transformation.getLeftRotation()),
                new Vector3f(transformation.getScale()),
                new AxisAngle4f(transformation.getRightRotation())
        );
        getEntities().values().forEach(entity -> entity.setTransformation(transformation));
        getHologram().updateHologram();
        return getSelf();
    }

    @Override
    public T setTransformationMatrix(final Matrix4f transformationMatrix) {
        this.transformation = new Transformation(
                transformationMatrix.getTranslation(new Vector3f()),
                transformationMatrix.getRotation(new AxisAngle4f()),
                transformationMatrix.getScale(new Vector3f()),
                transformationMatrix.getRotation(new AxisAngle4f())
        );
        getEntities().values().forEach(entity -> entity.setTransformationMatrix(transformationMatrix));
        getHologram().updateHologram();
        return getSelf();
    }

    @Override
    public int getInterpolationDuration() {
        return interpolationDuration;
    }

    @Override
    public T setInterpolationDuration(final int duration) {
        if (duration == this.interpolationDuration) return getSelf();
        this.interpolationDuration = duration;
        getEntities().values().forEach(entity -> entity.setInterpolationDuration(duration));
        return getSelf();
    }

    @Override
    public int getTeleportDuration() {
        return teleportDuration;
    }

    @Override
    public T setTeleportDuration(final int duration) {
        if (duration == this.teleportDuration) return getSelf();
        this.teleportDuration = duration;
        getEntities().values().forEach(entity -> entity.setTeleportDuration(duration));
        return getSelf();
    }

    @Override
    public float getViewRange() {
        return viewRange;
    }

    @Override
    public T setViewRange(final float range) {
        if (range == this.viewRange) return getSelf();
        this.viewRange = range;
        getEntities().values().forEach(entity -> entity.setViewRange(range));
        return getSelf();
    }

    @Override
    public float getShadowRadius() {
        return shadowRadius;
    }

    @Override
    public T setShadowRadius(final float radius) {
        if (radius == this.shadowRadius) return getSelf();
        this.shadowRadius = radius;
        getEntities().values().forEach(entity -> entity.setShadowRadius(radius));
        return getSelf();
    }

    @Override
    public float getShadowStrength() {
        return shadowStrength;
    }

    @Override
    public T setShadowStrength(final float strength) {
        if (strength == this.shadowStrength) return getSelf();
        this.shadowStrength = strength;
        getEntities().values().forEach(entity -> entity.setShadowStrength(strength));
        return getSelf();
    }

    @Override
    public float getDisplayWidth() {
        return displayWidth;
    }

    @Override
    public T setDisplayWidth(final float width) {
        if (width == this.displayWidth) return getSelf();
        this.displayWidth = width;
        getEntities().values().forEach(entity -> entity.setDisplayWidth(width));
        return getSelf();
    }

    @Override
    public float getDisplayHeight() {
        return displayHeight;
    }

    @Override
    public T setDisplayHeight(final float height) {
        if (height == this.displayHeight) return getSelf();
        this.displayHeight = height;
        getEntities().values().forEach(entity -> entity.setDisplayHeight(height));
        getHologram().updateHologram();
        return getSelf();
    }

    @Override
    public int getInterpolationDelay() {
        return interpolationDelay;
    }

    @Override
    public T setInterpolationDelay(final int ticks) {
        if (ticks == this.interpolationDelay) return getSelf();
        this.interpolationDelay = ticks;
        getEntities().values().forEach(entity -> entity.setInterpolationDelay(ticks));
        return getSelf();
    }

    @Override
    public Display.Billboard getBillboard() {
        return billboard;
    }

    @Override
    public T setBillboard(final Display.Billboard billboard) {
        if (Objects.equals(this.billboard, billboard)) return getSelf();
        this.billboard = billboard;
        getEntities().values().forEach(entity -> entity.setBillboard(billboard));
        return getSelf();
    }

    @Override
    public Optional<Color> getGlowColorOverride() {
        return Optional.ofNullable(glowColorOverride);
    }

    @Override
    public T setGlowColorOverride(@Nullable final Color color) {
        if (Objects.equals(this.glowColorOverride, color)) return getSelf();
        this.glowColorOverride = color;
        getEntities().values().forEach(entity -> entity.setGlowColorOverride(color));
        return getSelf();
    }

    @Override
    public Optional<Display.Brightness> getBrightness() {
        return Optional.ofNullable(brightness);
    }

    @Override
    public T setBrightness(final Display.@Nullable Brightness brightness) {
        if (Objects.equals(this.brightness, brightness)) return getSelf();
        this.brightness = brightness;
        getEntities().values().forEach(entity -> entity.setBrightness(brightness));
        return getSelf();
    }

    @SuppressWarnings("unchecked")
    private T getSelf() {
        return (T) this;
    }

    @Override
    protected void preSpawn(final E entity, final Player player) {
        entity.setTransformation(transformation);
        entity.setDisplayWidth(displayWidth);
        entity.setDisplayHeight(displayHeight);
        entity.setShadowRadius(shadowRadius);
        entity.setShadowStrength(shadowStrength);
        entity.setViewRange(viewRange);
        entity.setInterpolationDuration(interpolationDuration);
        entity.setInterpolationDelay(interpolationDelay);
        entity.setTeleportDuration(teleportDuration);
        entity.setBillboard(billboard);
        entity.setGlowColorOverride(glowColorOverride);
        entity.setBrightness(brightness);

        super.preSpawn(entity, player);
    }
}
