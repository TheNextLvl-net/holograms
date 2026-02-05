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
import java.util.UUID;
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
        return set(this.clickAction, clickAction, () -> {
            this.clickAction = clickAction;
            // todo: update click action entity if required?
        }, false);
    }

    @Override
    public PaperHologram getHologram() {
        return hologram;
    }

    @Override
    public World getWorld() {
        return hologram.getWorld();
    }

    @SuppressWarnings("unchecked")
    protected <T extends HologramLine, V> T set(
            final @Nullable V currentValue,
            final @Nullable V newValue,
            final Runnable setter,
            final boolean update
    ) {
        if (Objects.equals(currentValue, newValue)) return (T) this;
        setter.run();
        if (update) hologram.updateHologram();
        return (T) this;
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

    public abstract CompletableFuture<@Nullable Void> despawn(final UUID player);

    public abstract void invalidate(final Entity entity);

    public abstract CompletableFuture<Void> teleportRelative(final Location previous, final Location location);
}
