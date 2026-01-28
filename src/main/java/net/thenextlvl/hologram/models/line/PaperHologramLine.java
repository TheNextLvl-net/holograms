package net.thenextlvl.hologram.models.line;

import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@NullMarked
public abstract class PaperHologramLine implements HologramLine {
    protected volatile @Nullable ClickAction<?> clickAction = null;
    private final PaperHologram hologram;

    public PaperHologramLine(final PaperHologram hologram) {
        this.hologram = hologram;
    }

    @Override
    public Optional<ClickAction<?>> getClickAction() {
        return Optional.ofNullable(clickAction);
    }

    @Override
    public HologramLine setClickAction(@Nullable final ClickAction<?> clickAction) {
        if (Objects.equals(this.clickAction, clickAction)) return this;
        this.clickAction = clickAction;
        // todo: update click action entity if required?
        return this;
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

    public double getOffsetAfter(final Player player) {
        return 0;
    }

    public abstract CompletableFuture<@Nullable Entity> spawn(final Player player, final double offset);

    public abstract CompletableFuture<Void> despawn();

    public abstract CompletableFuture<@Nullable Void> despawn(final Player player);

    public abstract void invalidate(final Entity entity);

    public abstract CompletableFuture<Void> teleportRelative(final Location previous, final Location location);
}
