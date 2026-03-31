package net.thenextlvl.hologram.service;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.service.api.capability.CapabilityException;
import net.thenextlvl.service.api.hologram.line.BlockHologramLine;
import net.thenextlvl.service.api.hologram.line.EntityHologramLine;
import net.thenextlvl.service.api.hologram.line.HologramLine;
import net.thenextlvl.service.api.hologram.line.ItemHologramLine;
import net.thenextlvl.service.api.hologram.line.PagedHologramLine;
import net.thenextlvl.service.api.hologram.line.TextHologramLine;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@NullMarked
public record ServiceHologram(Hologram hologram) implements net.thenextlvl.service.api.hologram.Hologram {
    @Override
    public CompletableFuture<Boolean> teleportAsync(final Location location) {
        return hologram.teleportAsync(location);
    }

    @Override
    public Stream<HologramLine> getLines() {
        return hologram.getLines().map(this::wrapLine);
    }

    private HologramLine wrapLine(final net.thenextlvl.hologram.line.HologramLine line) {
        return switch (line) {
            case final net.thenextlvl.hologram.line.BlockHologramLine l -> new ServiceBlockHologramLine(this, l);
            case final net.thenextlvl.hologram.line.ItemHologramLine l -> new ServiceItemHologramLine(this, l);
            case final net.thenextlvl.hologram.line.TextHologramLine l -> new ServiceTextHologramLine(this, l);
            case final net.thenextlvl.hologram.line.EntityHologramLine l -> new ServiceEntityHologramLine(this, l);
            case final net.thenextlvl.hologram.line.PagedHologramLine l -> new ServicePagedHologramLine(this, l);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
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
    public boolean removeLine(final int index) throws CapabilityException {
        return hologram.removeLine(index);
    }

    @Override
    public boolean removeLines(final Collection<HologramLine> lines) {
        return hologram.removeLines(lines.stream()
                .filter(ServiceHologramLine.class::isInstance)
                .map(ServiceHologramLine.class::cast)
                .map(serviceLine -> serviceLine.line)
                .toList());
    }

    @Override
    public boolean clearLines() {
        return hologram.clearLines();
    }

    @Override
    public boolean hasLine(final HologramLine line) {
        return line instanceof final ServiceHologramLine<?> serviceLine && hologram.hasLine(serviceLine.line);
    }

    @Override
    public boolean moveLine(final int from, final int to) {
        return hologram.moveLine(from, to);
    }

    @Override
    public boolean swapLines(final int line1, final int line2) {
        return hologram.swapLines(line1, line2);
    }

    @Override
    public EntityHologramLine addEntityLine(final EntityType entityType) throws IllegalArgumentException, CapabilityException {
        return new ServiceEntityHologramLine(this, hologram.addEntityLine(entityType));
    }

    @Override
    public EntityHologramLine addEntityLine(final int index, final EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException {
        return new ServiceEntityHologramLine(this, hologram.addEntityLine(index, entityType));
    }

    @Override
    public BlockHologramLine addBlockLine() throws CapabilityException {
        return new ServiceBlockHologramLine(this, hologram.addBlockLine());
    }

    @Override
    public BlockHologramLine addBlockLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return new ServiceBlockHologramLine(this, hologram.addBlockLine(index));
    }

    @Override
    public ItemHologramLine addItemLine() throws CapabilityException {
        return new ServiceItemHologramLine(this, hologram.addItemLine());
    }

    @Override
    public ItemHologramLine addItemLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return new ServiceItemHologramLine(this, hologram.addItemLine(index));
    }

    @Override
    public TextHologramLine addTextLine() throws CapabilityException {
        return new ServiceTextHologramLine(this, hologram.addTextLine());
    }

    @Override
    public TextHologramLine addTextLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return new ServiceTextHologramLine(this, hologram.addTextLine(index));
    }

    @Override
    public PagedHologramLine addPagedLine() throws CapabilityException {
        return new ServicePagedHologramLine(this, hologram.addPagedLine());
    }

    @Override
    public PagedHologramLine addPagedLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return new ServicePagedHologramLine(this, hologram.addPagedLine(index));
    }

    @Override
    public PagedHologramLine setPagedLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return new ServicePagedHologramLine(this, hologram.setPagedLine(index));
    }

    @Override
    public EntityHologramLine setEntityLine(final int index, final EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException {
        return new ServiceEntityHologramLine(this, hologram.setEntityLine(index, entityType));
    }

    @Override
    public BlockHologramLine setBlockLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return new ServiceBlockHologramLine(this, hologram.setBlockLine(index));
    }

    @Override
    public ItemHologramLine setItemLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return new ServiceItemHologramLine(this, hologram.setItemLine(index));
    }

    @Override
    public TextHologramLine setTextLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return new ServiceTextHologramLine(this, hologram.setTextLine(index));
    }

    @Override
    public Optional<String> getViewPermission() {
        return hologram.getViewPermission();
    }

    @Override
    public boolean setViewPermission(@Nullable final String permission) {
        return hologram.setViewPermission(permission);
    }

    @Override
    public Stream<Player> getTrackedBy() {
        return hologram.getTrackedBy();
    }

    @Override
    public @Unmodifiable Set<UUID> getViewers() {
        return hologram.getViewers();
    }

    @Override
    public boolean addViewer(final UUID player) {
        return hologram.addViewer(player);
    }

    @Override
    public boolean addViewers(final Collection<UUID> players) {
        return hologram.addViewers(players);
    }

    @Override
    public boolean removeViewer(final UUID player) {
        return hologram.removeViewer(player);
    }

    @Override
    public boolean removeViewers(final Collection<UUID> players) {
        return hologram.removeViewers(players);
    }

    @Override
    public boolean isViewer(final UUID player) {
        return hologram.isViewer(player);
    }

    @Override
    public int getLineCount() {
        return hologram.getLineCount();
    }

    @Override
    public Optional<HologramLine> getLine(final int index) {
        return hologram.getLine(index).map(this::wrapLine);
    }

    @Override
    public <T extends HologramLine> Optional<T> getLine(final int index, final Class<T> type) {
        return getLine(index).filter(type::isInstance).map(type::cast);
    }

    @Override
    public int getLineIndex(final HologramLine line) {
        return line instanceof final ServiceHologramLine<?> serviceLine ? hologram.getLineIndex(serviceLine.line) : -1;
    }

    @Override
    public boolean removeLine(final HologramLine line) {
        return line instanceof final ServiceHologramLine<?> serviceLine && hologram.removeLine(serviceLine.line);
    }

    @Override
    public String getName() {
        return hologram.getName();
    }

    @Override
    public boolean setName(final String name) {
        return hologram.setName(name);
    }

    @Override
    public boolean isPersistent() {
        return hologram.isPersistent();
    }

    @Override
    public boolean setPersistent(final boolean persistent) {
        return hologram.setPersistent(persistent);
    }

    @Override
    public boolean persist() {
        return hologram.persist();
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
    public boolean setVisibleByDefault(final boolean visible) {
        return hologram.setVisibleByDefault(visible);
    }

    @Override
    public Iterator<HologramLine> iterator() {
        return getLines().iterator();
    }
}
