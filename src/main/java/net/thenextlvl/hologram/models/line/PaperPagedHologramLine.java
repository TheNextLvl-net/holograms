package net.thenextlvl.hologram.models.line;

import com.google.common.base.Preconditions;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.thenextlvl.hologram.event.HologramPageAddEvent;
import net.thenextlvl.hologram.event.HologramPageChangeEvent;
import net.thenextlvl.hologram.event.HologramPageRemoveEvent;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.StaticHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

@NullMarked
public final class PaperPagedHologramLine extends PaperHologramLine implements PagedHologramLine {
    private final List<PaperStaticHologramLine<?>> pages = new CopyOnWriteArrayList<>();
    private final Map<UUID, Integer> currentPageIndex = new ConcurrentHashMap<>();
    private final Set<UUID> trackedPlayers = ConcurrentHashMap.newKeySet();
    private final Random random = new Random();

    private final AtomicBoolean cycling = new AtomicBoolean(false);

    private volatile Duration interval = Duration.ofSeconds(2);
    private volatile boolean paused = false;
    private volatile boolean randomOrder = false;

    private volatile @Nullable ScheduledTask task;

    @Override
    public HologramLine copyFrom(final HologramLine other) {
        if (other instanceof final PagedHologramLine paged) {
            despawn();
            pages.clear();
            paged.forEachPage(page -> {
                final var copy = switch (page) {
                    case final TextHologramLine text -> addTextPage();
                    case final ItemHologramLine item -> addItemPage();
                    case final BlockHologramLine block -> addBlockPage();
                    case final EntityHologramLine entity -> addEntityPage(page.getEntityType());
                    default -> null;
                };
                if (copy != null) copy.copyFrom(page);
            });
            interval = paged.getInterval();
            paused = paged.isPaused();
            randomOrder = paged.isRandomOrder();
        }
        return super.copyFrom(other);
    }

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
    public LineType getType() {
        return LineType.PAGED;
    }

    @Override
    public boolean isPart(final Entity entity) {
        return pages.stream().anyMatch(page -> page.isPart(entity));
    }

    @Override
    public Stream<StaticHologramLine> getPages() {
        return pages.stream().map(page -> page);
    }

    @Override
    public Optional<StaticHologramLine> getPage(final int index) {
        if (index < 0 || index >= pages.size()) return Optional.empty();
        return Optional.of(pages.get(index));
    }

    @Override
    public <T extends StaticHologramLine> Optional<T> getPage(final int index, final Class<T> type) {
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
        return addPage(new PaperTextHologramLine(getHologram(), this));
    }

    @Override
    public ItemHologramLine addItemPage() {
        return addPage(new PaperItemHologramLine(getHologram(), this));
    }

    @Override
    public BlockHologramLine addBlockPage() {
        return addPage(new PaperBlockHologramLine(getHologram(), this));
    }

    @Override
    public EntityHologramLine addEntityPage(final EntityType entityType) throws IllegalArgumentException {
        return addPage(new PaperEntityHologramLine(getHologram(), this, entityType));
    }

    private <T extends PaperStaticHologramLine<?>> T addPage(final T page) {
        pages.add(page);
        new HologramPageAddEvent(getHologram(), this, page).callEvent();
        getHologram().updateHologram();
        return page;
    }

    @Override
    public boolean removePage(final int index) {
        if (index < 0 || index >= pages.size()) return false;
        final var removed = pages.remove(index);
        new HologramPageRemoveEvent(getHologram(), this, removed).callEvent();
        removed.despawn();
        return true;
    }

    @Override
    public boolean removePage(final HologramLine page) {
        if (!(page instanceof final PaperStaticHologramLine<?> paperPage)) return false;
        final var removed = pages.remove(paperPage);
        if (removed) {
            new HologramPageRemoveEvent(getHologram(), this, paperPage).callEvent();
            paperPage.despawn();
        }
        return removed;
    }

    @Override
    public boolean clearPages() {
        if (pages.isEmpty()) return false;
        pages.forEach(page -> new HologramPageRemoveEvent(getHologram(), this, page).callEvent());
        despawn().thenRun(pages::clear);
        return true;
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
        return setPage(index, () -> new PaperTextHologramLine(getHologram(), this));
    }

    @Override
    public ItemHologramLine setItemPage(final int index) throws IndexOutOfBoundsException {
        return setPage(index, () -> new PaperItemHologramLine(getHologram(), this));
    }

    @Override
    public BlockHologramLine setBlockPage(final int index) throws IndexOutOfBoundsException {
        return setPage(index, () -> new PaperBlockHologramLine(getHologram(), this));
    }

    @Override
    public EntityHologramLine setEntityPage(final int index, final EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException {
        return setPage(index, () -> new PaperEntityHologramLine(getHologram(), this, entityType));
    }

    private <T extends PaperStaticHologramLine<?>> T setPage(final int index, final Supplier<T> supplier) {
        if (index < 0 || index >= pages.size())
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + pages.size());
        final var page = supplier.get();
        pages.set(index, page);
        getHologram().updateHologram();
        return page;
    }

    @Override
    public TextHologramLine insertTextPage(final int index) {
        return insertPage(index, () -> new PaperTextHologramLine(getHologram(), this));
    }

    @Override
    public ItemHologramLine insertItemPage(final int index) {
        return insertPage(index, () -> new PaperItemHologramLine(getHologram(), this));
    }

    @Override
    public BlockHologramLine insertBlockPage(final int index) {
        return insertPage(index, () -> new PaperBlockHologramLine(getHologram(), this));
    }

    @Override
    public EntityHologramLine insertEntityPage(final int index, final EntityType entityType) throws IllegalArgumentException {
        return insertPage(index, () -> new PaperEntityHologramLine(getHologram(), this, entityType));
    }

    private <T extends PaperStaticHologramLine<?>> T insertPage(final int index, final Supplier<T> supplier) {
        if (index < 0 || index > pages.size())
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + pages.size());
        final var page = supplier.get();
        pages.add(index, page);
        new HologramPageAddEvent(getHologram(), this, page).callEvent();
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

    @Override
    public CompletableFuture<Boolean> cyclePage(final Player player) {
        return cyclePage(player, 1);
    }

    @Override
    public CompletableFuture<Boolean> cyclePage(final Player player, final int amount) {
        return cyclePage(player, calculateOffset(player), amount);
    }

    @Override
    public CompletableFuture<Boolean> setPage(final Player player, final int page) throws IndexOutOfBoundsException {
        if (page < 0 || page >= pages.size())
            throw new IndexOutOfBoundsException("Index: " + page + ", Size: " + pages.size());
        return setPage(player, calculateOffset(player), currentPageIndex.getOrDefault(player.getUniqueId(), 0), page);
    }

    @Override
    public OptionalInt getCurrentPageIndex(final Player player) {
        final var value = currentPageIndex.get(player.getUniqueId());
        return value != null ? OptionalInt.of(value) : OptionalInt.empty();
    }

    @Override
    public Optional<StaticHologramLine> getCurrentPage(final Player player) {
        return getPage(getCurrentPageIndex(player).orElse(0));
    }

    @Override
    public void forEachPage(final Consumer<StaticHologramLine> action) {
        pages.forEach(action);
    }

    @Override
    public double getHeight(final Player player) {
        return getCurrentPage(player)
                .map(PaperStaticHologramLine.class::cast)
                .map(page -> page.getHeight(player))
                .orElse(0d);
    }

    @Override
    public double getOffsetBefore(final Player player) {
        return getCurrentPage(player)
                .map(PaperStaticHologramLine.class::cast)
                .map(page -> page.getOffsetBefore(player))
                .orElse(0d);
    }

    @Override
    public double getOffsetAfter(final Player player) {
        return getCurrentPage(player)
                .map(PaperStaticHologramLine.class::cast)
                .map(page -> page.getOffsetAfter(player))
                .orElse(0d);
    }

    @Override
    public CompletableFuture<@Nullable Entity> spawn(final Player player, final double offset) {
        if (pages.isEmpty() || !player.isConnected()) return CompletableFuture.completedFuture(null);
        return ((PaperStaticHologramLine<?>) getCurrentPage(player)
                .orElseGet(pages::getFirst))
                .spawn(player, offset);
    }

    @Override
    public CompletableFuture<Void> despawn() {
        stopCycleTask();
        currentPageIndex.clear();
        trackedPlayers.clear();
        final var futures = pages.stream()
                .map(PaperStaticHologramLine::despawn)
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(futures);
    }

    @Override
    public CompletableFuture<Void> despawn(final UUID player) {
        untrack(player);
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

    public void untrack(final UUID player) {
        if (trackedPlayers.remove(player) && trackedPlayers.isEmpty()) stopCycleTask();
    }

    public void track(final UUID player) {
        if (trackedPlayers.add(player)) startCycleTask();
    }

    @Override
    public void invalidate(final Entity entity) {
        pages.forEach(page -> page.invalidate(entity));
    }

    private CompletableFuture<Boolean> cyclePage(final Player player, final double offset, @Nullable final Integer amount) {
        if (pages.isEmpty() || !player.isConnected()) return CompletableFuture.completedFuture(false);

        final int oldIndex = currentPageIndex.getOrDefault(player.getUniqueId(), 0);
        final int newIndex = findVisiblePage(player, oldIndex, amount);
        if (newIndex == -1) return despawn(player.getUniqueId()).thenApply(v -> false);

        return setPage(player, offset, oldIndex, newIndex);
    }

    private CompletableFuture<Boolean> setPage(final Player player, final double offset, final int oldIndex, final int newIndex) {
        if (pages.isEmpty() || !player.isConnected()) return CompletableFuture.completedFuture(false);

        final var oldPage = pages.size() > oldIndex ? pages.get(oldIndex) : null;
        final var newPage = pages.get(newIndex);

        if (oldPage != null) {
            final var event = new HologramPageChangeEvent(getHologram(), this, player, oldPage, newPage);
            if (!event.callEvent()) return CompletableFuture.completedFuture(false);
        }

        currentPageIndex.put(player.getUniqueId(), newIndex);
        if (!trackedPlayers.contains(player.getUniqueId())) return CompletableFuture.completedFuture(true);

        final var adopt = oldPage != null ? newPage.adoptEntities(oldPage, player) : CompletableFuture.<Void>completedFuture(null);
        return adopt.thenCompose(v -> getHologram().updateHologram(player));
    }

    private void startCycleTask() {
        if (task != null || paused || pages.isEmpty() || trackedPlayers.isEmpty()) return;
        final long delayTicks = Math.max(1, interval.toMillis() / 50);
        task = getHologram().getPlugin().getServer().getRegionScheduler().runDelayed(
                getHologram().getPlugin(),
                getHologram().getLocation(),
                task -> tickCycle(),
                delayTicks
        );
    }

    private void stopCycleTask() {
        final var scheduledTask = task;
        if (scheduledTask != null) {
            scheduledTask.cancel();
            task = null;
        }
        cycling.set(false);
    }

    private void tickCycle() {
        if (!cycling.compareAndSet(false, true)) return;

        cycleAllPlayers().whenComplete((v, t) -> {
            cycling.set(false);
            task = null;
            startCycleTask();
        });
    }

    private CompletableFuture<Void> cycleAllPlayers() {
        final var futures = trackedPlayers.stream()
                .map(getHologram().getPlugin().getServer()::getPlayer)
                .filter(Objects::nonNull)
                .map(player -> cyclePage(player, calculateOffset(player), null))
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(futures);
    }

    private int findVisiblePage(final Player player, final int currentIndex, @Nullable final Integer amount) {
        final var size = pages.size();
        if (randomOrder && amount == null) {
            final var visible = new ArrayList<Integer>(size);
            for (var i = 0; i < size; i++) {
                if (i != currentIndex && pages.get(i).canSee(player)) visible.add(i);
            }
            if (visible.isEmpty()) return -1;
            return visible.get(random.nextInt(visible.size()));
        }
        final var step = amount != null ? amount : 1;
        final var direction = step < 0 ? -1 : 1;
        var index = Math.floorMod(currentIndex + step, size);
        for (var i = 0; i < size; i++) {
            if (pages.get(index).canSee(player)) return index;
            index = Math.floorMod(index + direction, size);
        }
        return -1;
    }

    private double calculateOffset(final Player player) {
        var offset = 0d;
        for (final var l : getHologram()) {
            if (l == this) return offset;
            final var line = (PaperHologramLine) l;
            offset += 0.05 + line.getHeight(player) + line.getOffsetAfter(player);
        }
        return offset;
    }

    public void addPageInternal(final PaperStaticHologramLine<?> hologramLine) {
        hologramLine.parentLine = this;
        this.pages.add(hologramLine);
    }
}
