package net.thenextlvl.hologram.service;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramProvider;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.service.api.capability.CapabilityException;
import net.thenextlvl.service.api.hologram.HologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@NullMarked
public record ServiceHologram(Hologram hologram) implements net.thenextlvl.service.api.hologram.Hologram {
    @Override
    public CompletableFuture<Boolean> teleportAsync(final Location location) {
        return hologram.teleportAsync(location);
    }

    @Override
    public @Unmodifiable List<HologramLine<?>> getLines() {
        return hologram.getLines().map(line -> switch (line) {
            case final BlockHologramLine block -> new ServiceBlockHologramLine(block);
            case final ItemHologramLine item -> new ServiceItemHologramLine(item);
            case final TextHologramLine text -> new ServiceTextHologramLine(text);
            case final EntityHologramLine entity -> new ServiceEntityHologramLine(entity);
            default -> null;
        }).filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Location getLocation() {
        return hologram.getLocation();
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public World getWorld() {
        return hologram.getWorld();
    }

    @Override
    public double getX() {
        return hologram.getLocation().getX();
    }

    @Override
    public double getY() {
        return hologram.getLocation().getY();
    }

    @Override
    public double getZ() {
        return hologram.getLocation().getZ();
    }

    @Override
    public float getPitch() {
        return hologram.getLocation().getPitch();
    }

    @Override
    public float getYaw() {
        return hologram.getLocation().getYaw();
    }

    @Override
    public boolean addLine(final HologramLine<?> line) throws CapabilityException {
        return addLine(getLineCount(), line);
    }

    @Override
    public boolean addLine(final int index, final HologramLine<?> line) throws CapabilityException {
        if (!(line instanceof final ServiceHologramLine<?, ?> serviceLine)) return false;
        if (hologram.hasLine(serviceLine.line)) return false;
        // ((PaperHologram) hologram).addLine(index, serviceLine.line); // fixme: aint gonna work
        return true;
    }

    @Override
    public boolean addLines(final Collection<HologramLine<?>> lines) throws CapabilityException {
        return lines.stream().map(this::addLine).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean removeLine(final HologramLine<?> line) throws CapabilityException {
        final var index = getLines().indexOf(line);
        return index >= 0 && removeLine(index);
    }

    @Override
    public boolean removeLine(final int index) throws CapabilityException {
        return hologram.removeLine(index);
    }

    @Override
    public int getLineCount() {
        return hologram.getLineCount();
    }

    @Override
    public void remove() {
        HologramProvider.instance().deleteHologram(hologram);
    }

    @Override
    public Iterator<HologramLine<?>> iterator() {
        return getLines().iterator();
    }

    @Override
    public String getName() {
        return hologram.getName();
    }

    @Override
    public boolean isPersistent() {
        return hologram.isPersistent();
    }

    @Override
    public boolean persist() {
        return hologram.persist();
    }

    @Override
    public void setPersistent(final boolean persistent) {
        hologram.setPersistent(persistent);
    }

    @Override
    public @Unmodifiable Set<Player> getTrackedBy() {
        return hologram.getTrackedBy().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @Unmodifiable Set<Player> getViewers() {
        return hologram.getViewers().stream()
                .map(getServer()::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean addViewer(final Player player) {
        return hologram.addViewer(player.getUniqueId());
    }

    @Override
    public boolean addViewers(final Collection<Player> players) {
        return players.stream().map(this::addViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean isTrackedBy(final Player player) {
        return hologram.isTrackedBy(player);
    }

    @Override
    public boolean canSee(final Player player) {
        return hologram.canSee(player);
    }

    @Override
    public boolean isVisibleByDefault() {
        return hologram.isVisibleByDefault();
    }

    @Override
    public boolean removeViewer(final Player player) {
        return hologram.removeViewer(player.getUniqueId());
    }

    @Override
    public boolean removeViewers(final Collection<Player> players) {
        return players.stream().map(this::removeViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public double getDisplayRange() {
        return 0;
    }

    @Override
    public void setDisplayRange(final double range) {
    }

    @Override
    public void setVisibleByDefault(final boolean visible) {
        hologram.setVisibleByDefault(visible);
    }
}
