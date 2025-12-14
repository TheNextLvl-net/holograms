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

@NullMarked
public abstract class PaperDisplayHologramLine<E extends Display> extends PaperHologramLine<E> implements DisplayHologramLine<E> {
    private @Nullable Color glowColorOverride = null;
    private Display.@Nullable Brightness brightness = null;
    private Display.Billboard billboard = Display.Billboard.CENTER;
    private Transformation transformation = new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1), new AxisAngle4f());
    private float displayHeight = 0;
    private float displayWidth = 0;
    private float shadowRadius = 0;
    private float shadowStrength = 1;
    private float viewRange = 1;
    private int interpolationDelay = 0;
    private int interpolationDuration = 0;
    private int teleportDuration = 0;

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
    public void setTransformation(Transformation transformation) {
        this.transformation = new Transformation(
                transformation.getTranslation(),
                transformation.getLeftRotation(),
                transformation.getScale(),
                transformation.getRightRotation()
        );
        getEntity().ifPresent(entity -> entity.setTransformation(transformation));
    }

    @Override
    public void setTransformationMatrix(Matrix4f transformationMatrix) {
        this.transformation = new Transformation(
                transformationMatrix.getTranslation(new Vector3f()),
                transformationMatrix.getRotation(new AxisAngle4f()),
                transformationMatrix.getScale(new Vector3f()),
                transformationMatrix.getRotation(new AxisAngle4f())
        );
        getEntity().ifPresent(entity -> entity.setTransformationMatrix(transformationMatrix));
    }

    @Override
    public int getInterpolationDuration() {
        return interpolationDuration;
    }

    @Override
    public void setInterpolationDuration(int duration) {
        this.interpolationDuration = duration;
        getEntity().ifPresent(entity -> entity.setInterpolationDuration(duration));
    }

    @Override
    public int getTeleportDuration() {
        return teleportDuration;
    }

    @Override
    public void setTeleportDuration(int duration) {
        this.teleportDuration = duration;
        getEntity().ifPresent(entity -> entity.setTeleportDuration(duration));
    }

    @Override
    public float getViewRange() {
        return viewRange;
    }

    @Override
    public void setViewRange(float range) {
        this.viewRange = range;
        getEntity().ifPresent(entity -> entity.setViewRange(range));
    }

    @Override
    public float getShadowRadius() {
        return shadowRadius;
    }

    @Override
    public void setShadowRadius(float radius) {
        this.shadowRadius = radius;
        getEntity().ifPresent(entity -> entity.setShadowRadius(radius));
    }

    @Override
    public float getShadowStrength() {
        return shadowStrength;
    }

    @Override
    public void setShadowStrength(float strength) {
        this.shadowStrength = strength;
        getEntity().ifPresent(entity -> entity.setShadowStrength(strength));
    }

    @Override
    public float getDisplayWidth() {
        return displayWidth;
    }

    @Override
    public void setDisplayWidth(float width) {
        this.displayWidth = width;
        getEntity().ifPresent(entity -> entity.setDisplayWidth(width));
    }

    @Override
    public float getDisplayHeight() {
        return displayHeight;
    }

    @Override
    public void setDisplayHeight(float height) {
        this.displayHeight = height;
        getEntity().ifPresent(entity -> entity.setDisplayHeight(height));
    }

    @Override
    public int getInterpolationDelay() {
        return interpolationDelay;
    }

    @Override
    public void setInterpolationDelay(int ticks) {
        this.interpolationDelay = ticks;
        getEntity().ifPresent(entity -> entity.setInterpolationDelay(ticks));
    }

    @Override
    public Display.Billboard getBillboard() {
        return billboard;
    }

    @Override
    public void setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
        getEntity().ifPresent(entity -> entity.setBillboard(billboard));
    }

    @Override
    public @Nullable Color getGlowColorOverride() {
        return glowColorOverride;
    }

    @Override
    public void setGlowColorOverride(@Nullable Color color) {
        this.glowColorOverride = color;
        getEntity().ifPresent(entity -> entity.setGlowColorOverride(color));
    }

    @Override
    public Display.@Nullable Brightness getBrightness() {
        return brightness;
    }

    @Override
    public void setBrightness(Display.@Nullable Brightness brightness) {
        this.brightness = brightness;
        getEntity().ifPresent(entity -> entity.setBrightness(brightness));
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
