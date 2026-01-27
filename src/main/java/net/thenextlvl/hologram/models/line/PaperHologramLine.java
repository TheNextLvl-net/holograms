package net.thenextlvl.hologram.models.line;

import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@NullMarked
public abstract class PaperHologramLine implements HologramLine {
    private final PaperHologram hologram;

    public PaperHologramLine(final PaperHologram hologram) {
        this.hologram = hologram;
    }

    @Override
    public PaperHologram getHologram() {
        return hologram;
    }

    @Override
    public World getWorld() {
        return hologram.getWorld();
    }

    public abstract double getHeight(Player player);

    public double getOffsetBefore(final Player player) {
        return 0;
    }

    public double getOffsetAfter() {
        return 0;
    }

    public abstract @Nullable Entity spawn(final Player player, final double offset);

    public abstract CompletableFuture<Void> despawn();

    public abstract CompletableFuture<Void> despawn(final Player player);

    public abstract void invalidate(final Entity entity);
    
    public abstract CompletableFuture<Void> teleportRelative(final Location previous, final Location location);
}
