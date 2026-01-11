package net.thenextlvl.hologram.models.line;

import net.thenextlvl.hologram.line.DisplayHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Color;
import org.bukkit.entity.Display;
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
    protected @Nullable Color glowColorOverride = null;
    protected Display.@Nullable Brightness brightness = null;
    protected Display.Billboard billboard = Display.Billboard.CENTER;
    protected Transformation transformation = new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1), new AxisAngle4f());
    protected float displayHeight = 0;
    protected float displayWidth = 0;
    protected float shadowRadius = 0;
    protected float shadowStrength = 1;
    protected float viewRange = 1;
    protected int interpolationDelay = 0;
    protected int interpolationDuration = 0;
    protected int teleportDuration = 4;

    public PaperDisplayHologramLine(PaperHologram hologram, Class<E> entityClass) {
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
    public T setTransformation(Transformation transformation) {
        if (Objects.equals(this.transformation, transformation)) return getSelf();
        this.transformation = new Transformation(
                transformation.getTranslation(),
                transformation.getLeftRotation(),
                transformation.getScale(),
                transformation.getRightRotation()
        );
        getEntity().ifPresent(entity -> entity.setTransformation(transformation));
        getHologram().updateHologram();
        return getSelf();
    }

    @Override
    public T setTransformationMatrix(Matrix4f transformationMatrix) {
        this.transformation = new Transformation(
                transformationMatrix.getTranslation(new Vector3f()),
                transformationMatrix.getRotation(new AxisAngle4f()),
                transformationMatrix.getScale(new Vector3f()),
                transformationMatrix.getRotation(new AxisAngle4f())
        );
        getEntity().ifPresent(entity -> entity.setTransformationMatrix(transformationMatrix));
        getHologram().updateHologram();
        return getSelf();
    }

    @Override
    public int getInterpolationDuration() {
        return interpolationDuration;
    }

    @Override
    public T setInterpolationDuration(int duration) {
        if (duration == this.interpolationDuration) return getSelf();
        this.interpolationDuration = duration;
        getEntity().ifPresent(entity -> entity.setInterpolationDuration(duration));
        return getSelf();
    }

    @Override
    public int getTeleportDuration() {
        return teleportDuration;
    }

    @Override
    public T setTeleportDuration(int duration) {
        if (duration == this.teleportDuration) return getSelf();
        this.teleportDuration = duration;
        getEntity().ifPresent(entity -> entity.setTeleportDuration(duration));
        return getSelf();
    }

    @Override
    public float getViewRange() {
        return viewRange;
    }

    @Override
    public T setViewRange(float range) {
        if (range == this.viewRange) return getSelf();
        this.viewRange = range;
        getEntity().ifPresent(entity -> entity.setViewRange(range));
        return getSelf();
    }

    @Override
    public float getShadowRadius() {
        return shadowRadius;
    }

    @Override
    public T setShadowRadius(float radius) {
        if (radius == this.shadowRadius) return getSelf();
        this.shadowRadius = radius;
        getEntity().ifPresent(entity -> entity.setShadowRadius(radius));
        return getSelf();
    }

    @Override
    public float getShadowStrength() {
        return shadowStrength;
    }

    @Override
    public T setShadowStrength(float strength) {
        if (strength == this.shadowStrength) return getSelf();
        this.shadowStrength = strength;
        getEntity().ifPresent(entity -> entity.setShadowStrength(strength));
        return getSelf();
    }

    @Override
    public float getDisplayWidth() {
        return displayWidth;
    }

    @Override
    public T setDisplayWidth(float width) {
        if (width == this.displayWidth) return getSelf();
        this.displayWidth = width;
        getEntity().ifPresent(entity -> entity.setDisplayWidth(width));
        return getSelf();
    }

    @Override
    public float getDisplayHeight() {
        return displayHeight;
    }

    @Override
    public T setDisplayHeight(float height) {
        if (height == this.displayHeight) return getSelf();
        this.displayHeight = height;
        getEntity().ifPresent(entity -> entity.setDisplayHeight(height));
        getHologram().updateHologram();
        return getSelf();
    }

    @Override
    public int getInterpolationDelay() {
        return interpolationDelay;
    }

    @Override
    public T setInterpolationDelay(int ticks) {
        if (ticks == this.interpolationDelay) return getSelf();
        this.interpolationDelay = ticks;
        getEntity().ifPresent(entity -> entity.setInterpolationDelay(ticks));
        return getSelf();
    }

    @Override
    public Display.Billboard getBillboard() {
        return billboard;
    }

    @Override
    public T setBillboard(Display.Billboard billboard) {
        if (Objects.equals(this.billboard, billboard)) return getSelf();
        this.billboard = billboard;
        getEntity().ifPresent(entity -> entity.setBillboard(billboard));
        return getSelf();
    }

    @Override
    public Optional<Color> getGlowColorOverride() {
        return Optional.ofNullable(glowColorOverride);
    }

    @Override
    public T setGlowColorOverride(@Nullable Color color) {
        if (Objects.equals(this.glowColorOverride, color)) return getSelf();
        this.glowColorOverride = color;
        getEntity().ifPresent(entity -> entity.setGlowColorOverride(color));
        return getSelf();
    }

    @Override
    public Optional<Display.Brightness> getBrightness() {
        return Optional.ofNullable(brightness);
    }

    @Override
    public T setBrightness(Display.@Nullable Brightness brightness) {
        if (Objects.equals(this.brightness, brightness)) return getSelf();
        this.brightness = brightness;
        getEntity().ifPresent(entity -> entity.setBrightness(brightness));
        return getSelf();
    }

    @SuppressWarnings("unchecked")
    private T getSelf() {
        return (T) this;
    }

    @Override
    protected void preSpawn(E entity) {
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

        super.preSpawn(entity);
    }
}
