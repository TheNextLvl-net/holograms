package net.thenextlvl.hologram.model;

import com.google.common.base.Preconditions;
import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagSerializable;
import core.nbt.tag.Tag;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.controller.PaperHologramController;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.model.line.PaperBlockHologramLine;
import net.thenextlvl.hologram.model.line.PaperEntityHologramLine;
import net.thenextlvl.hologram.model.line.PaperHologramLine;
import net.thenextlvl.hologram.model.line.PaperItemHologramLine;
import net.thenextlvl.hologram.model.line.PaperTextHologramLine;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class PaperHologram implements Hologram, TagSerializable {
    private final List<HologramLine<?>> lines = new LinkedList<>();
    private final Set<UUID> viewers = new HashSet<>();

    private final PaperHologramController controller;
    private final String name;

    private @Nullable String viewPermission;
    private Location location;
    private boolean persistent;
    private boolean visibleByDefault;

    public PaperHologram(PaperHologramController controller, String name, Location location) {
        Preconditions.checkArgument(location.getWorld() != null, "World cannot be null");
        this.controller = controller;
        this.location = location;
        this.name = name;
    }

    @Override
    public PaperHologramController getController() {
        return controller;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public World getWorld() {
        return location.getWorld();
    }

    @Override
    public CompletableFuture<Boolean> teleportAsync(Location location) {
        return CompletableFuture.completedFuture(false); // todo: implement
    }

    @Override
    public @Unmodifiable List<HologramLine<?>> getLines() {
        return List.copyOf(lines);
    }

    @Override
    public int getLineCount() {
        return lines.size();
    }

    @Override
    public @Nullable HologramLine<?> getLine(int index) throws IndexOutOfBoundsException {
        return lines.get(index);
    }

    @Override
    public int getLineIndex(HologramLine<?> line) {
        return lines.indexOf(line);
    }

    @Override
    public boolean removeLine(HologramLine<?> line) {
        var removed = lines.remove(line);
        if (removed) line.getEntity().ifPresent(Entity::remove);
        return removed;
    }

    @Override
    public boolean removeLine(int index) {
        var removed = lines.remove(index);
        if (removed != null) removed.getEntity().ifPresent(Entity::remove);
        return removed != null;
    }

    @Override
    public boolean removeLines(Collection<HologramLine<?>> lines) {
        return lines.stream().map(this::removeLine).reduce(false, Boolean::logicalOr);
    }

    @Override
    public void clearLines() {
        lines.forEach(line -> line.getEntity().ifPresent(Entity::remove));
        lines.clear();
    }

    @Override
    public boolean hasLine(HologramLine<?> line) {
        return lines.contains(line);
    }

    @Override
    public <T extends Entity> EntityHologramLine<T> addEntityLine(Class<T> entityType) throws IllegalArgumentException {
        return addEntityLine(entityType, lines.size());
    }

    @Override
    public <T extends Entity> EntityHologramLine<T> addEntityLine(Class<T> entityType, int index) throws IllegalArgumentException {
        var hologramLine = new PaperEntityHologramLine<>(this, entityType);
        lines.add(index, hologramLine);
        return hologramLine;
    }

    @Override
    public BlockHologramLine addBlockLine() {
        return addBlockLine(lines.size());
    }

    @Override
    public BlockHologramLine addBlockLine(int index) {
        var hologramLine = new PaperBlockHologramLine(this);
        lines.add(index, hologramLine);
        return hologramLine;
    }

    @Override
    public ItemHologramLine addItemLine() {
        return addItemLine(lines.size());
    }

    @Override
    public ItemHologramLine addItemLine(int index) {
        var hologramLine = new PaperItemHologramLine(this);
        lines.add(index, hologramLine);
        return hologramLine;
    }

    @Override
    public TextHologramLine addTextLine() {
        return addTextLine(lines.size());
    }

    @Override
    public TextHologramLine addTextLine(int index) {
        var hologramLine = new PaperTextHologramLine(this);
        lines.add(index, hologramLine);
        return hologramLine;
    }

    @Override
    public @Nullable String getViewPermission() {
        return viewPermission;
    }

    @Override
    public boolean setViewPermission(@Nullable String permission) {
        if (Objects.equals(this.viewPermission, permission)) return false;
        this.viewPermission = permission;
        getLines().forEach(line -> line.getEntity().ifPresent(entity -> controller.getServer().getOnlinePlayers()
                .forEach(player -> updateVisibility(entity, player))));
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
        controller.unregister(name);
    }

    @Override
    public boolean spawn() {
        if (isSpawned()) return false;
        this.entity = location.getWorld().spawn(location, getTypeClass(), this::preSpawn);
        return true;
    }

    @Override
    public Iterator<HologramLine<?>> iterator() {
        return lines.iterator();
    }

    protected void preSpawn(Entity entity) {
        entity.setMetadata("Hologram", new FixedMetadataValue(controller.getPlugin(), true));
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

        if (viewPermission != null || !visibleByDefault) controller.getServer().getOnlinePlayers()
                .forEach(player -> updateVisibility(entity, player));
    }

    public void updateVisibility(Entity entity, Player player) {
        if (canSee(player)) player.showEntity(controller.getPlugin(), entity);
        else player.hideEntity(controller.getPlugin(), entity);
    }

    @Override
    public void despawn() {
        lines.forEach(line -> line.getEntity().ifPresent(Entity::remove));
    }

    @Override
    public boolean isSpawned() {
        return entity != null && entity.isValid();
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
