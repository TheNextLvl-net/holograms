package net.thenextlvl.hologram.model;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagSerializable;
import core.nbt.tag.Tag;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Unmodifiable;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@NullMarked
public abstract class PaperHologram<E extends Display> implements Hologram<E>, TagSerializable {
    private final Set<UUID> viewers = new HashSet<>();

    private final HologramPlugin plugin;
    private final String name;

    private @Nullable E entity;
    private @Nullable Location spawnLocation;
    private @Nullable String viewPermission;
    private boolean persistent;
    private boolean visibleByDefault;

    private @Nullable Color glowColorOverride = null;
    private Display.@Nullable Brightness brightness = null;
    private Display.Billboard billboard = Display.Billboard.FIXED;
    private Transformation transformation = new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1), new AxisAngle4f());
    private float displayHeight = 0;
    private float displayWidth = 0;
    private float shadowRadius = 0;
    private float shadowStrength = 1;
    private float viewRange = 1;
    private int interpolationDelay = 0;
    private int interpolationDuration = 0;
    private int teleportDuration = 0;

    protected PaperHologram(HologramPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    @Override
    public Optional<E> getEntity() {
        return Optional.ofNullable(entity);
    }

    @Override
    public <T extends Display> Optional<T> getEntity(Class<T> type) {
        return getEntity().filter(type::isInstance).map(type::cast);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public @Nullable Location getLocation() {
        return getEntity().map(Entity::getLocation).orElse(null);
    }

    @Override
    public @Nullable Location getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public boolean setSpawnLocation(@Nullable Location location) {
        if (Objects.equals(this.spawnLocation, location)) return false;
        this.spawnLocation = location;
        return true;
    }

    @Override
    public @Nullable World getWorld() {
        return getEntity().map(Entity::getWorld).orElse(null);
    }

    @Override
    public @Nullable String getViewPermission() {
        return viewPermission;
    }

    @Override
    public boolean setViewPermission(@Nullable String permission) {
        if (Objects.equals(this.viewPermission, permission)) return false;
        this.viewPermission = permission;
        getEntity().ifPresent(entity -> plugin.getServer().getOnlinePlayers()
                .forEach(player -> updateVisibility(entity, player)));
        return true;
    }

    @Override
    public @Unmodifiable Set<UUID> getViewers() {
        return Set.copyOf(viewers);
    }

    @Override
    public boolean addViewer(UUID player) {
        if (!viewers.add(player)) return false;
        if (entity == null || isVisibleByDefault()) return true;
        var online = plugin.getServer().getPlayer(player);
        if (online != null) online.showEntity(plugin, entity);
        return true;
    }

    @Override
    public boolean addViewers(Collection<UUID> players) {
        return players.stream().map(this::addViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean removeViewer(UUID player) {
        if (!viewers.remove(player)) return false;
        if (entity == null || isVisibleByDefault()) return true;
        var online = plugin.getServer().getPlayer(player);
        if (online != null) online.hideEntity(plugin, entity);
        return true;
    }

    @Override
    public boolean removeViewers(Collection<UUID> players) {
        return players.stream().map(this::removeViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean isViewer(UUID player) {
        return viewers.contains(player);
    }

    @Override
    public boolean canSee(Player player) {
        if (entity == null || !isSpawned()) return false;
        if (!player.getWorld().equals(entity.getWorld())) return false;
        if (viewPermission != null && !player.hasPermission(viewPermission)) return false;
        return isVisibleByDefault() || isViewer(player.getUniqueId());
    }

    @Override
    public boolean isTrackedBy(Player player) {
        return getEntity().map(entity -> entity.getTrackedBy().contains(player)).orElse(false);
    }

    @Override
    public boolean isVisibleByDefault() {
        return visibleByDefault;
    }

    @Override
    public boolean setVisibleByDefault(boolean visible) {
        if (this.visibleByDefault == visible) return false;
        this.visibleByDefault = visible;
        return true;
    }

    @Override
    public boolean setPersistent(boolean persistent) {
        if (this.persistent == persistent) return false;
        this.persistent = persistent;
        return true;
    }

    @Override
    public boolean isPersistent() {
        return persistent;
    }

    @Override
    public boolean persist() {
        return false; // todo: implement
    }

    @Override
    public void delete() {
        despawn(); // todo: implement
        // backupFile.delete();
        // file.delete();
        plugin.hologramController().unregister(name);
    }

    @Override
    public boolean spawn() {
        return spawnLocation != null && spawn(spawnLocation);
    }

    @Override
    public boolean spawn(Location location) {
        if (isSpawned()) return false;
        this.spawnLocation = location;
        this.entity = location.getWorld().spawn(location, getTypeClass(), this::preSpawn);
        return true;
    }

    protected void preSpawn(E entity) {
        entity.setMetadata("Hologram", new FixedMetadataValue(plugin, true));
        entity.setPersistent(false);
        entity.setVisibleByDefault(visibleByDefault);
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

        if (viewPermission != null || !visibleByDefault) plugin.getServer().getOnlinePlayers()
                .forEach(player -> updateVisibility(entity, player));
    }

    public void updateVisibility(E entity, Player player) {
        if (canSee(player)) player.showEntity(plugin, entity);
        else player.hideEntity(plugin, entity);
    }

    @Override
    public boolean isSpawned() {
        return entity != null && entity.isValid();
    }

    @Override
    public boolean despawn() {
        if (entity == null) return false;
        entity.remove();
        entity = null;
        return true;
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
    public Tag serialize() throws ParserException {
        return null; // todo: implement
    }

    @Override
    public void deserialize(Tag tag) throws ParserException {
        // todo: implement
    }
}
