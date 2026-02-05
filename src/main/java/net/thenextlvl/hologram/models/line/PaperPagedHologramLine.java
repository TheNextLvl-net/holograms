package net.thenextlvl.hologram.models.line;

import com.google.common.base.Preconditions;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@NullMarked
public final class PaperPagedHologramLine extends PaperHologramLine implements PagedHologramLine {
    private final List<PaperStaticHologramLine<?>> pages = new CopyOnWriteArrayList<>();
    private final Map<UUID, Integer> currentPageIndex = new ConcurrentHashMap<>();
    private final Random random = new Random();

    private final AtomicBoolean cycling = new AtomicBoolean(false);
    private final AtomicLong nextCycleTime = new AtomicLong(0);

    private volatile Duration interval = Duration.ofSeconds(2);
    private volatile boolean paused = false;
    private volatile boolean randomOrder = false;

    public PaperPagedHologramLine(final PaperHologram hologram) {
        super(hologram);
    }

    @Override
    public Optional<Entity> getEntity(final Player player) {
        return getCurrentPage(player).flatMap(page -> page.getEntity(player));
    }

    @Override
    public <T> Optional<T> getEntity(final Player player, final Class<T> type) {
        return getEntity(player).filter(type::isInstance).map(type::cast);
    }

    @Override
    public Class<? extends Entity> getEntityClass() {
        return Entity.class;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.MARKER;
    }

    @Override
    public LineType getType() {
        return LineType.PAGED;
    }

    @Override
    public boolean isPart(final Entity entity) {
        return pages.stream().anyMatch(page -> page.isPart(entity));
    }

    @Override
    public List<HologramLine> getPages() {
        return List.copyOf(pages);
    }

    @Override
    public Optional<HologramLine> getPage(final int index) {
        if (index < 0 || index >= pages.size()) return Optional.empty();
        return Optional.of(pages.get(index));
    }

    @Override
    public <T extends HologramLine> Optional<T> getPage(final int index, final Class<T> type) {
        return getPage(index).filter(type::isInstance).map(type::cast);
    }

    @Override
    public int getPageCount() {
        return pages.size();
    }

    @Override
    public int getPageIndex(final HologramLine line) {
        return line instanceof final PaperStaticHologramLine<?> page ? pages.indexOf(page) : -1;
    }

    @Override
    public TextHologramLine addTextPage() {
        final var page = new PaperTextHologramLine(getHologram(), this);
        pages.add(page);
        getHologram().updateHologram();
        return page;
    }

    @Override
    public ItemHologramLine addItemPage() {
        final var page = new PaperItemHologramLine(getHologram(), this);
        pages.add(page);
        getHologram().updateHologram();
        return page;
    }

    @Override
    public BlockHologramLine addBlockPage() {
        final var page = new PaperBlockHologramLine(getHologram(), this);
        pages.add(page);
        getHologram().updateHologram();
        return page;
    }

    @Override
    public EntityHologramLine addEntityPage(final EntityType entityType) throws IllegalArgumentException {
        final var page = new PaperEntityHologramLine(getHologram(), this, entityType);
        pages.add(page);
        getHologram().updateHologram();
        return page;
    }

    @Override
    public boolean removePage(final int index) {
        if (index < 0 || index >= pages.size()) return false;
        final var removed = pages.remove(index);
        removed.despawn();
        return true;
    }

    @Override
    public boolean removePage(final HologramLine page) {
        if (!(page instanceof final PaperStaticHologramLine<?> paperPage)) return false;
        final var removed = pages.remove(paperPage);
        if (removed) paperPage.despawn();
        return removed;
    }

    @Override
    public void clearPages() {
        pages.forEach(PaperStaticHologramLine::despawn);
        pages.clear();
        currentPageIndex.clear();
        stopCycleTask();
    }

    public boolean swapPages(final int first, final int second) {
        if (first < 0 || first >= pages.size() || second < 0 || second >= pages.size()) return false;
        java.util.Collections.swap(pages, first, second);
        getHologram().updateHologram();
        return true;
    }

    @Override
    public boolean movePage(final int from, final int to) {
        if (from < 0 || from >= pages.size() || to < 0 || to >= pages.size()) return false;
        final var page = pages.remove(from);
        pages.add(to, page);
        getHologram().updateHologram();
        return true;
    }

    @Override
    public TextHologramLine setTextPage(final int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= pages.size())
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + pages.size());

        final var page = new PaperTextHologramLine(getHologram(), this);
        pages.set(index, page);
        getHologram().updateHologram();
        return page;
    }

    @Override
    public ItemHologramLine setItemPage(final int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= pages.size())
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + pages.size());

        final var page = new PaperItemHologramLine(getHologram(), this);
        pages.set(index, page);
        getHologram().updateHologram();
        return page;
    }

    @Override
    public BlockHologramLine setBlockPage(final int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= pages.size())
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + pages.size());

        final var page = new PaperBlockHologramLine(getHologram(), this);
        pages.set(index, page);
        getHologram().updateHologram();
        return page;
    }

    @Override
    public EntityHologramLine setEntityPage(final int index, final EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (index < 0 || index >= pages.size())
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + pages.size());

        final var page = new PaperEntityHologramLine(getHologram(), this, entityType);
        pages.set(index, page);
        getHologram().updateHologram();
        return page;
    }

    @Override
    public TextHologramLine insertTextPage(final int index) {
        if (index < 0 || index > pages.size())
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + pages.size());
        final var page = new PaperTextHologramLine(getHologram(), this);
        pages.add(index, page);
        getHologram().updateHologram();
        return page;
    }

    @Override
    public ItemHologramLine insertItemPage(final int index) {
        if (index < 0 || index > pages.size())
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + pages.size());
        final var page = new PaperItemHologramLine(getHologram(), this);
        pages.add(index, page);
        getHologram().updateHologram();
        return page;
    }

    @Override
    public BlockHologramLine insertBlockPage(final int index) {
        if (index < 0 || index > pages.size())
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + pages.size());
        final var page = new PaperBlockHologramLine(getHologram(), this);
        pages.add(index, page);
        getHologram().updateHologram();
        return page;
    }

    @Override
    public EntityHologramLine insertEntityPage(final int index, final EntityType entityType) throws IllegalArgumentException {
        if (index < 0 || index > pages.size())
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + pages.size());
        final var page = new PaperEntityHologramLine(getHologram(), this, entityType);
        pages.add(index, page);
        getHologram().updateHologram();
        return page;
    }

    @Override
    public Duration getInterval() {
        return interval;
    }

    @Override
    public PagedHologramLine setInterval(final Duration interval) {
        Preconditions.checkArgument(interval.isPositive(), "Interval must be bigger than zero");
        this.interval = interval;
        nextCycleTime.set(System.currentTimeMillis() + interval.toMillis());
        return this;
    }

    @Override
    public boolean isRandomOrder() {
        return randomOrder;
    }

    @Override
    public PagedHologramLine setRandomOrder(final boolean random) {
        this.randomOrder = random;
        return this;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public PagedHologramLine setPaused(final boolean paused) {
        this.paused = paused;
        if (paused) stopCycleTask();
        else startCycleTask();
        return this;
    }

    public Optional<PaperStaticHologramLine<?>> getCurrentPage(final Player player) {
        final var index = currentPageIndex.getOrDefault(player.getUniqueId(), 0);
        return getPage(index).map(hologramLine -> (PaperStaticHologramLine<?>) hologramLine);
    }

    public int getCurrentPageIndex(final Player player) {
        return currentPageIndex.getOrDefault(player.getUniqueId(), 0);
    }

    @Override
    public double getHeight(final Player player) {
        return getCurrentPage(player)
                .map(page -> page.getHeight(player))
                .orElse(0d);
    }

    @Override
    public double getOffsetBefore(final Player player) {
        return getCurrentPage(player)
                .map(page -> page.getOffsetBefore(player))
                .orElse(0d);
    }

    @Override
    public double getOffsetAfter(final Player player) {
        return getCurrentPage(player)
                .map(page -> page.getOffsetAfter(player))
                .orElse(0d);
    }

    @Override
    public CompletableFuture<@Nullable Entity> spawn(final Player player, final double offset) {
        if (pages.isEmpty() || !player.isConnected()) return CompletableFuture.completedFuture(null);
        currentPageIndex.compute(player.getUniqueId(), (ignored, index) ->
                index == null || index >= pages.size() ? 0 : index);
        final var page = getCurrentPage(player).orElseGet(pages::getFirst);
        startCycleTask();
        return page.spawn(player, offset);
    }

    @Override
    public CompletableFuture<Void> despawn() {
        stopCycleTask();
        currentPageIndex.clear();
        final var futures = pages.stream()
                .map(PaperStaticHologramLine::despawn)
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(futures);
    }

    @Override
    public CompletableFuture<Void> despawn(final UUID player) {
        currentPageIndex.remove(player);
        if (currentPageIndex.isEmpty()) stopCycleTask();
        final var futures = pages.stream()
                .map(page -> page.despawn(player))
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(futures);
    }

    @Override
    public CompletableFuture<Void> teleportRelative(final Location previous, final Location location) {
        return CompletableFuture.allOf(pages.stream()
                .map(page -> page.teleportRelative(previous, location))
                .toArray(CompletableFuture[]::new));
    }

    @Override
    public void invalidate(final Entity entity) {
        pages.forEach(page -> page.invalidate(entity));
    }

    private CompletableFuture<Void> cyclePage(final Player player, final double offset) {
        if (pages.isEmpty() || !player.isConnected()) return CompletableFuture.completedFuture(null);

        final int oldIndex = currentPageIndex.getOrDefault(player.getUniqueId(), 0);
        final var oldPage = pages.size() > oldIndex ? pages.get(oldIndex) : null;

        final int newIndex;
        if (randomOrder) {
            newIndex = random.nextInt(pages.size());
        } else {
            newIndex = (oldIndex + 1) % pages.size();
        }

        final var newPage = pages.get(newIndex);
        currentPageIndex.put(player.getUniqueId(), newIndex);

        if (oldPage != null && newPage.adoptEntity(oldPage, player, offset))
            return CompletableFuture.completedFuture(null);

        final var despawn = oldPage != null ? oldPage.despawn(player.getUniqueId())
                : CompletableFuture.<Void>completedFuture(null);
        return despawn.thenCompose(v -> newPage.spawn(player, offset).thenAccept(e -> {
        }));
    }

    private void startCycleTask() {
        if (paused || pages.size() <= 1) return;
        if (!getHologram().getPlugin().hologramTickPool().register(this)) return;
        nextCycleTime.set(System.currentTimeMillis() + interval.toMillis());
    }

    private void stopCycleTask() {
        if (getHologram().getPlugin().hologramTickPool().unregister(this)) nextCycleTime.set(0);
    }

    public void tickCycle(final long now) {
        if (now < nextCycleTime.get()) return;
        if (!cycling.compareAndSet(false, true)) return;

        cycleAllPlayers().whenComplete((v, t) -> {
            final long elapsed = System.currentTimeMillis() - now;
            nextCycleTime.set(System.currentTimeMillis() + Math.max(0, interval.toMillis() - elapsed));
            cycling.set(false);
        });
    }

    private CompletableFuture<Void> cycleAllPlayers() {
        final var futures = currentPageIndex.keySet().stream()
                .map(getHologram().getPlugin().getServer()::getPlayer)
                .filter(Objects::nonNull)
                .map(player -> cyclePage(player, calculateOffset(player)))
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(futures); // todo: add proper realigning
    }

    private double calculateOffset(final Player player) {
        final var lines = getHologram().getLines().toList();
        var offset = 0d;
        for (var i = lines.size() - 1; i >= 0; i--) {
            final var line = (PaperHologramLine) lines.get(i);
            if (line == this) return offset;
            offset += 0.05 + line.getHeight(player) + line.getOffsetAfter(player);
        }
        return offset;
    }

    public void addPage(final PaperStaticHologramLine<?> hologramLine) {
        hologramLine.parentLine = this;
        this.pages.add(hologramLine);
    }
}
