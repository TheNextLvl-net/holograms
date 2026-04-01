package net.thenextlvl.hologram.service;

import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.service.capability.CapabilityException;
import net.thenextlvl.service.hologram.line.BlockHologramLine;
import net.thenextlvl.service.hologram.line.EntityHologramLine;
import net.thenextlvl.service.hologram.line.HologramLine;
import net.thenextlvl.service.hologram.line.ItemHologramLine;
import net.thenextlvl.service.hologram.line.StaticHologramLine;
import net.thenextlvl.service.hologram.line.TextHologramLine;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

@NullMarked
public final class ServicePagedHologramLine extends ServiceHologramLine<PagedHologramLine> implements net.thenextlvl.service.hologram.line.PagedHologramLine {
    public ServicePagedHologramLine(final ServiceHologram hologram, final PagedHologramLine line) {
        super(hologram, line);
    }

    @Override
    public Stream<StaticHologramLine> getPages() {
        return line.getPages().map(this::wrapPage);
    }

    private StaticHologramLine wrapPage(final net.thenextlvl.hologram.line.StaticHologramLine line) {
        return switch (line) {
            case final net.thenextlvl.hologram.line.ItemHologramLine l -> new ServiceItemHologramLine(hologram, l);
            case final net.thenextlvl.hologram.line.BlockHologramLine l -> new ServiceBlockHologramLine(hologram, l);
            case final net.thenextlvl.hologram.line.EntityHologramLine l -> new ServiceEntityHologramLine(hologram, l);
            case final net.thenextlvl.hologram.line.TextHologramLine l -> new ServiceTextHologramLine(hologram, l);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    @Override
    public Optional<StaticHologramLine> getPage(final int index) {
        return line.getPage(index).map(this::wrapPage);
    }

    @Override
    public <T extends StaticHologramLine> Optional<T> getPage(final int index, final Class<T> type) {
        return getPage(index).map(type::isInstance).map(type::cast);
    }

    @Override
    public int getPageCount() {
        return line.getPageCount();
    }

    @Override
    public int getPageIndex(final HologramLine line) {
        return line instanceof final ServiceHologramLine<?> service ? this.line.getPageIndex(service.line) : -1;
    }

    @Override
    public TextHologramLine addTextPage() throws CapabilityException {
        return new ServiceTextHologramLine(hologram, line.addTextPage());
    }

    @Override
    public ItemHologramLine addItemPage() throws CapabilityException {
        return new ServiceItemHologramLine(hologram, line.addItemPage());
    }

    @Override
    public BlockHologramLine addBlockPage() throws CapabilityException {
        return new ServiceBlockHologramLine(hologram, line.addBlockPage());
    }

    @Override
    public EntityHologramLine addEntityPage(final EntityType entityType) throws IllegalArgumentException, CapabilityException {
        return new ServiceEntityHologramLine(hologram, line.addEntityPage(entityType));
    }

    @Override
    public boolean removePage(final int index) {
        return line.removePage(index);
    }

    @Override
    public boolean removePage(final HologramLine page) {
        return page instanceof final ServiceHologramLine<?> service && line.removePage(service.line);
    }

    @Override
    public boolean clearPages() {
        return line.clearPages();
    }

    @Override
    public boolean swapPages(final int first, final int second) {
        return line.swapPages(first, second);
    }

    @Override
    public boolean movePage(final int from, final int to) {
        return line.movePage(from, to);
    }

    @Override
    public TextHologramLine setTextPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return new ServiceTextHologramLine(hologram, line.setTextPage(index));
    }

    @Override
    public ItemHologramLine setItemPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return new ServiceItemHologramLine(hologram, line.setItemPage(index));
    }

    @Override
    public BlockHologramLine setBlockPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return new ServiceBlockHologramLine(hologram, line.setBlockPage(index));
    }

    @Override
    public EntityHologramLine setEntityPage(final int index, final EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException {
        return new ServiceEntityHologramLine(hologram, line.setEntityPage(index, entityType));
    }

    @Override
    public TextHologramLine insertTextPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return new ServiceTextHologramLine(hologram, line.insertTextPage(index));
    }

    @Override
    public ItemHologramLine insertItemPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return new ServiceItemHologramLine(hologram, line.insertItemPage(index));
    }

    @Override
    public BlockHologramLine insertBlockPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return new ServiceBlockHologramLine(hologram, line.insertBlockPage(index));
    }

    @Override
    public EntityHologramLine insertEntityPage(final int index, final EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException {
        return new ServiceEntityHologramLine(hologram, line.insertEntityPage(index, entityType));
    }

    @Override
    public Duration getInterval() {
        return line.getInterval();
    }

    @Override
    public boolean setInterval(final Duration interval) throws IllegalArgumentException {
        if (getInterval().equals(interval)) return false;
        line.setInterval(interval);
        return true;
    }

    @Override
    public boolean isRandomOrder() {
        return line.isRandomOrder();
    }

    @Override
    public boolean setRandomOrder(final boolean random) {
        if (isRandomOrder() == random) return false;
        line.setRandomOrder(random);
        return true;
    }

    @Override
    public boolean isPaused() {
        return line.isPaused();
    }

    @Override
    public boolean setPaused(final boolean paused) {
        if (isPaused() == paused) return false;
        line.setPaused(paused);
        return true;
    }

    @Override
    public CompletableFuture<Boolean> cyclePage(final Player player) {
        return line.cyclePage(player);
    }

    @Override
    public CompletableFuture<Boolean> cyclePage(final Player player, final int amount) {
        return line.cyclePage(player, amount);
    }

    @Override
    public CompletableFuture<Boolean> setPage(final Player player, final int page) throws IndexOutOfBoundsException {
        return line.setPage(player, page);
    }

    @Override
    public OptionalInt getCurrentPageIndex(final Player player) {
        return line.getCurrentPageIndex(player);
    }

    @Override
    public Optional<StaticHologramLine> getCurrentPage(final Player player) {
        return line.getCurrentPage(player).map(this::wrapPage);
    }

    @Override
    public void forEachPage(final Consumer<StaticHologramLine> action) {
        line.getPages().map(this::wrapPage).forEach(action);
    }
}
