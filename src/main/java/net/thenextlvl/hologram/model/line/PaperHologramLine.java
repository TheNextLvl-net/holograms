package net.thenextlvl.hologram.model.line;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.model.PaperHologram;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

@NullMarked
public abstract class PaperHologramLine<E extends Entity> implements HologramLine<E> {
    private final HologramPlugin plugin;

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
        this.plugin = hologram.getController().getPlugin();
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
    public Hologram getHologram() {
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

    @Override
    public void despawn() {
        if (entity == null) return;
        entity.remove();
        entity = null;
    }

    @Override
    public void spawn() {
        if (entity != null) return;
        var location = getLocation();
        this.entity = location.getWorld().spawn(location, getTypeClass(), this::preSpawn);
    }

    protected void preSpawn(E entity) {
        entity.setPersistent(false);
        entity.setVisibleByDefault(hologram.isVisibleByDefault());

        if (hologram.getViewPermission() == null && hologram.isVisibleByDefault()) return;
        plugin.getServer().getOnlinePlayers().forEach(player -> hologram.updateVisibility(entity, player));
    }
}
