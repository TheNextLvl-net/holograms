package net.thenextlvl.hologram.models.line;

import com.google.common.base.Preconditions;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public abstract class PaperHologramLine<E extends Entity> implements HologramLine<E> {
    private final PaperHologram hologram;
    private final Class<E> entityClass;
    private final EntityType entityType;
    private final Map<Player, E> entities = new ConcurrentHashMap<>();

    public PaperHologramLine(final PaperHologram hologram, final Class<E> entityClass) {
        this.hologram = hologram;
        this.entityType = Arrays.stream(EntityType.values())
                .filter(type -> type.getEntityClass() != null)
                .filter(type -> type.getEntityClass().isAssignableFrom(entityClass))
                .findAny().orElseThrow(() -> new IllegalArgumentException("Entity type not found for " + entityClass));
        this.entityClass = entityClass;
    }

    @Override
    public Class<E> getTypeClass() {
        return entityClass;
    }

    @Override
    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public PaperHologram getHologram() {
        return hologram;
    }

    @Override
    public Optional<E> getEntity(final Player player) {
        return Optional.ofNullable(getEntities().get(player));
    }

    @Override
    public Map<Player, E> getEntities() {
        return entities;
    }

    @Override
    public <T> Optional<T> getEntity(final Player player, final Class<T> type) {
        return getEntity(player).filter(type::isInstance).map(type::cast);
    }

    @Override
    public World getWorld() {
        return hologram.getWorld();
    }

    public void despawn() {
        entities.values().forEach(Entity::remove);
        entities.clear();
    }

    public void despawn(final Player player) {
        final var remove = entities.remove(player);
        if (remove != null) remove.remove();
    }

    public abstract double getHeight(Player player);

    public double getOffsetBefore(final Player player) {
        return 0;
    }

    public double getOffsetAfter() {
        return 0;
    }

    public E spawn(final Player player, final double offset) throws IllegalStateException {
        final var entity = entities.get(player);
        Preconditions.checkState(entity == null || !entity.isValid(), "Entity is already spawned");
        final var location = mutateSpawnLocation(hologram.getLocation().add(0, offset, 0));
        final var spawn = location.getWorld().spawn(location, getTypeClass(), false, e -> this.preSpawn(e, player));
        player.showEntity(hologram.getPlugin(), spawn);
        entities.put(player, spawn);
        return spawn;
    }

    protected Location mutateSpawnLocation(final Location location) {
        return location;
    }

    public void updateVisibility(final Player player) {
        final var entity = entities.get(player);
        if (entity == null) return;
        if (canSee(player)) player.showEntity(hologram.getPlugin(), entity);
        else player.hideEntity(hologram.getPlugin(), entity);
    }

    public boolean canSee(final Player player) {
        final var entity = entities.get(player);
        if (entity == null || !entity.isValid()) return false;
        if (!player.getWorld().equals(entity.getWorld())) return false;
        return hologram.canSee(player);
    }

    public CompletableFuture<Void> teleportRelative(final Location previous, final Location location) {
        return CompletableFuture.allOf(getEntities().values().stream()
                .filter(Entity::isValid)
                .map(entity -> entity.teleportAsync(new Location(
                        location.getWorld(),
                        location.getX() + entity.getX() - previous.getX(),
                        location.getY() + entity.getY() - previous.getY(),
                        location.getZ() + entity.getZ() - previous.getZ(),
                        location.getYaw(), location.getPitch()
                ))).toArray(CompletableFuture[]::new));
    }

    protected void preSpawn(final E entity, final Player player) {
        entity.setPersistent(false);
        entity.setVisibleByDefault(false);

        // if (hologram.getViewPermission() != null || !hologram.isVisibleByDefault())
        //     plugin.getServer().getOnlinePlayers().forEach(this::updateVisibility);
    }

    public void invalidate(final Entity entity) {
        entities.values().removeIf(e -> e.equals(entity));
    }
}
