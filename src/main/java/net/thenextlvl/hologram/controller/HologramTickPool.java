package net.thenextlvl.hologram.controller;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.models.line.PaperPagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@NullMarked
public final class HologramTickPool {
    private final Set<PaperPagedHologramLine> registeredLines = ConcurrentHashMap.newKeySet();
    private final HologramPlugin plugin;

    private volatile @Nullable ScheduledTask cycleTask = null;

    public HologramTickPool(final HologramPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean register(final PaperPagedHologramLine line) {
        if (!registeredLines.add(line)) return false;
        startTaskIfNeeded();
        return true;
    }

    public boolean unregister(final PaperPagedHologramLine line) {
        if (!registeredLines.remove(line)) return false;
        stopTaskIfEmpty();
        return true;
    }

    private void startTaskIfNeeded() {
        if (cycleTask != null || registeredLines.isEmpty()) return;
        cycleTask = plugin.getServer().getAsyncScheduler().runAtFixedRate(
                plugin, this::tick, 50, 50, TimeUnit.MILLISECONDS
        );
    }

    private void stopTaskIfEmpty() {
        if (!registeredLines.isEmpty()) return;
        final var task = cycleTask;
        if (task != null) {
            task.cancel();
            cycleTask = null;
        }
    }

    private void tick(final ScheduledTask task) {
        final long now = System.currentTimeMillis();
        registeredLines.forEach(line -> line.tickCycle(now));
    }

    public void shutdown() {
        final var task = cycleTask;
        if (task != null) {
            task.cancel();
            cycleTask = null;
        }
        registeredLines.clear();
    }
}
