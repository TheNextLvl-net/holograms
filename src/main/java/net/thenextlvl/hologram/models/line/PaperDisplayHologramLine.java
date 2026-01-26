package net.thenextlvl.hologram.models.line;

import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.hologram.line.DisplayHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Brightness;
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
public abstract class PaperDisplayHologramLine<E extends Display> extends PaperStaticHologramLine<E> implements DisplayHologramLine {
    protected volatile @Nullable Brightness brightness = null;
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
    public DisplayHologramLine setTransformation(final Transformation transformation) {
        if (Objects.equals(this.transformation, transformation)) return this;
        this.transformation = new Transformation(
                new Vector3f(transformation.getTranslation()),
                new AxisAngle4f(transformation.getLeftRotation()),
                new Vector3f(transformation.getScale()),
                new AxisAngle4f(transformation.getRightRotation())
        );
        forEachEntity(entity -> entity.setTransformation(transformation));
        getHologram().updateHologram();
        return this;
    }

    @Override
    public DisplayHologramLine setTransformationMatrix(final Matrix4f transformationMatrix) {
        this.transformation = new Transformation(
                transformationMatrix.getTranslation(new Vector3f()),
                transformationMatrix.getRotation(new AxisAngle4f()),
                transformationMatrix.getScale(new Vector3f()),
                transformationMatrix.getRotation(new AxisAngle4f())
        );
        forEachEntity(entity -> entity.setTransformationMatrix(transformationMatrix));
        getHologram().updateHologram();
        return this;
    }

    @Override
    public int getInterpolationDuration() {
        return interpolationDuration;
    }

    @Override
    public DisplayHologramLine setInterpolationDuration(final int duration) {
        if (duration == this.interpolationDuration) return this;
        this.interpolationDuration = duration;
        forEachEntity(entity -> entity.setInterpolationDuration(duration));
        return this;
    }

    @Override
    public int getTeleportDuration() {
        return teleportDuration;
    }

    @Override
    public DisplayHologramLine setTeleportDuration(final int duration) {
        if (duration == this.teleportDuration) return this;
        this.teleportDuration = duration;
        forEachEntity(entity -> entity.setTeleportDuration(duration));
        return this;
    }

    @Override
    public float getViewRange() {
        return viewRange;
    }

    @Override
    public DisplayHologramLine setViewRange(final float range) {
        if (range == this.viewRange) return this;
        this.viewRange = range;
        forEachEntity(entity -> entity.setViewRange(range));
        return this;
    }

    @Override
    public float getShadowRadius() {
        return shadowRadius;
    }

    @Override
    public DisplayHologramLine setShadowRadius(final float radius) {
        if (radius == this.shadowRadius) return this;
        this.shadowRadius = radius;
        forEachEntity(entity -> entity.setShadowRadius(radius));
        return this;
    }

    @Override
    public float getShadowStrength() {
        return shadowStrength;
    }

    @Override
    public DisplayHologramLine setShadowStrength(final float strength) {
        if (strength == this.shadowStrength) return this;
        this.shadowStrength = strength;
        forEachEntity(entity -> entity.setShadowStrength(strength));
        return this;
    }

    @Override
    public float getDisplayWidth() {
        return displayWidth;
    }

    @Override
    public DisplayHologramLine setDisplayWidth(final float width) {
        if (width == this.displayWidth) return this;
        this.displayWidth = width;
        forEachEntity(entity -> entity.setDisplayWidth(width));
        return this;
    }

    @Override
    public float getDisplayHeight() {
        return displayHeight;
    }

    @Override
    public DisplayHologramLine setDisplayHeight(final float height) {
        if (height == this.displayHeight) return this;
        this.displayHeight = height;
        forEachEntity(entity -> entity.setDisplayHeight(height));
        getHologram().updateHologram();
        return this;
    }

    @Override
    public int getInterpolationDelay() {
        return interpolationDelay;
    }

    @Override
    public DisplayHologramLine setInterpolationDelay(final int ticks) {
        if (ticks == this.interpolationDelay) return this;
        this.interpolationDelay = ticks;
        forEachEntity(entity -> entity.setInterpolationDelay(ticks));
        return this;
    }

    @Override
    public Display.Billboard getBillboard() {
        return billboard;
    }

    @Override
    public DisplayHologramLine setBillboard(final Display.Billboard billboard) {
        if (Objects.equals(this.billboard, billboard)) return this;
        this.billboard = billboard;
        forEachEntity(entity -> entity.setBillboard(billboard));
        return this;
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
        if (Objects.equals(this.brightness, brightness)) return this;
        this.brightness = brightness;
        forEachEntity(entity -> entity.setBrightness(brightness));
        return this;
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
}
