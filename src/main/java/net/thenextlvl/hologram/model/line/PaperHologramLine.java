package net.thenextlvl.hologram.model.line;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.HologramLine;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public abstract class PaperHologramLine<E extends Entity> implements HologramLine<E> {
    private final Hologram hologram;
    private @Nullable E entity;

    public PaperHologramLine(Hologram hologram) {
        this.hologram = hologram;
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
}
