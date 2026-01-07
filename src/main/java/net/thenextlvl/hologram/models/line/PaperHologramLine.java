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
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

@NullMarked
public abstract class PaperHologramLine<E extends Entity> implements HologramLine<E> {
    private final PaperHologram hologram;
    private final Class<E> entityClass;
    private final EntityType entityType;
    private @Nullable E entity;

    public PaperHologramLine(PaperHologram hologram, Class<E> entityClass) {
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
    public Optional<E> getEntity() {
        return Optional.ofNullable(entity);
    }

    @Override
    public <T> Optional<T> getEntity(Class<T> type) {
        return getEntity().filter(type::isInstance).map(type::cast);
    }

    @Override
    public Location getLocation() {
        return hologram.getLocation();
    }

    @Override
    public World getWorld() {
        return hologram.getWorld();
    }

    public void despawn() {
        if (entity == null) return;
        entity.remove();
        entity = null;
    }

    // todo: do some more crazy math to calculate the perfect offset
    public abstract double getHeight();

    public double getOffsetBefore() {
        return 0;
    }

    public double getOffsetAfter() {
        return 0;
    }

    public E spawn(double offset) throws IllegalStateException {
        Preconditions.checkState(entity == null || !entity.isValid(), "Entity is already spawned");
        var location = getLocation().clone().add(0, offset, 0);
        return this.entity = location.getWorld().spawn(location, getTypeClass(), false, this::preSpawn);
    }

    public void updateVisibility(Player player) {
        if (entity == null) return;
        if (canSee(player)) player.showEntity(hologram.getPlugin(), entity);
        else player.hideEntity(hologram.getPlugin(), entity);
    }

    public boolean canSee(Player player) {
        if (entity == null || !entity.isValid()) return false;
        if (!player.getWorld().equals(entity.getWorld())) return false;
        return hologram.canSee(player);
    }

    protected void preSpawn(E entity) {
        entity.setPersistent(false);
        entity.setVisibleByDefault(hologram.isVisibleByDefault());

        // if (hologram.getViewPermission() != null || !hologram.isVisibleByDefault())
        //     plugin.getServer().getOnlinePlayers().forEach(this::updateVisibility);
    }

    public void invalidate(Entity entity) {
        if (this.entity == entity) this.entity = null;
    }
}
