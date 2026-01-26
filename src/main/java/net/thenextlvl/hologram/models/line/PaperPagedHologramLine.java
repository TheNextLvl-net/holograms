package net.thenextlvl.hologram.models.line;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
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
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@NullMarked
public class PaperPagedHologramLine extends PaperHologramLine implements PagedHologramLine {
    private final List<PaperStaticHologramLine<?>> pages = new CopyOnWriteArrayList<>();
    private final Map<Player, Integer> currentPageIndex = new ConcurrentHashMap<>();
    private final Random random = new Random();

    private volatile @Nullable ScheduledTask cycleTask = null;
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
    public TextHologramLine addTextPage() {
        final var page = new PaperTextHologramLine(getHologram());
        pages.add(page);
        return page;
    }

    @Override
    public ItemHologramLine addItemPage() {
        final var page = new PaperItemHologramLine(getHologram());
        pages.add(page);
        return page;
    }

    @Override
    public BlockHologramLine addBlockPage() {
        final var page = new PaperBlockHologramLine(getHologram());
        pages.add(page);
        return page;
    }

    @Override
    public EntityHologramLine addEntityPage(final EntityType entityType) throws IllegalArgumentException {
        final var entityClass = entityType.getEntityClass();
        if (entityClass == null) throw new IllegalArgumentException("Entity type is not spawnable: " + entityType);
        final var page = new PaperEntityHologramLine<>(getHologram(), entityClass);
        pages.add(page);
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
    }

    @Override
    public Duration getInterval() {
        return interval;
    }

    @Override
    public PagedHologramLine setInterval(final Duration interval) {
        this.interval = interval;
        restartCycleTask();
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
        else restartCycleTask();
        return this;
    }

    public Optional<HologramLine> getCurrentPage(final Player player) {
        final var index = currentPageIndex.getOrDefault(player, 0);
        return getPage(index);
    }

    public int getCurrentPageIndex(final Player player) {
        return currentPageIndex.getOrDefault(player, 0);
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
    public double getOffsetAfter() {
        return pages.stream()
                .mapToDouble(PaperStaticHologramLine::getOffsetAfter)
                .max()
                .orElse(0d);
    }

    @Override
    public @Nullable Entity spawn(final Player player, final double offset) {
        if (pages.isEmpty()) return null;
        currentPageIndex.put(player, 0);
        final var page = pages.getFirst();
        startCycleTask();
        return page.spawn(player, offset);
    }

    @Override
    public void despawn() {
        stopCycleTask();
        pages.forEach(PaperStaticHologramLine::despawn);
        currentPageIndex.clear();
    }

    @Override
    public void despawn(final Player player) {
        currentPageIndex.remove(player);
        pages.forEach(page -> page.despawn(player));
    }

    public CompletableFuture<Void> teleportRelative(final Location previous, final Location location) {
        return CompletableFuture.allOf(pages.stream()
                .map(page -> page.teleportRelative(previous, location))
                .toArray(CompletableFuture[]::new));
    }

    public void invalidate(final Entity entity) {
        pages.forEach(page -> page.invalidate(entity));
    }

    private void cyclePage(final Player player, final double offset) {
        if (pages.isEmpty()) return;

        final int oldIndex = currentPageIndex.getOrDefault(player, 0);
        final var oldPage = pages.size() > oldIndex ? pages.get(oldIndex) : null;

        final int newIndex;
        if (randomOrder) {
            newIndex = random.nextInt(pages.size());
        } else {
            newIndex = (oldIndex + 1) % pages.size();
        }

        final var newPage = pages.get(newIndex);
        currentPageIndex.put(player, newIndex);

        if (oldPage == null) {
            newPage.spawn(player, offset);
            return;
        }

        if (oldPage.getEntityClass().equals(newPage.getEntityClass())) {
            final var entity = oldPage.removeEntity(player);
            if (entity != null && entity.isValid()) {
                newPage.adoptEntity(player, entity);
                return;
            }
        }

        oldPage.despawn(player);
        newPage.spawn(player, offset);
    }

    private void startCycleTask() {
        if (cycleTask != null || paused || pages.size() <= 1) return;

        final var ticks = interval.toMillis() / 50;
        cycleTask = getHologram().getPlugin().getServer().getGlobalRegionScheduler().runAtFixedRate(
                getHologram().getPlugin(),
                this::cycleAllPlayers,
                ticks,
                ticks
        );
    }

    private void stopCycleTask() {
        final var task = cycleTask;
        if (task != null) {
            task.cancel();
            cycleTask = null;
        }
    }

    private void restartCycleTask() {
        stopCycleTask();
        if (!paused && pages.size() > 1) {
            startCycleTask();
        }
    }

    private void cycleAllPlayers(final ScheduledTask task) {
        currentPageIndex.keySet().forEach(player -> {
            if (getHologram().isSpawned(player)) {
                cyclePage(player, calculateOffset(player));
            }
        });
    }

    private double calculateOffset(final Player player) {
        var offset = 0d;
        final var lines = getHologram().getLines().toList();
        for (var i = lines.size() - 1; i >= 0; i--) {
            final var line = lines.get(i);
            if (line == this) return offset;
            if (line instanceof final PaperStaticHologramLine<?> paperLine) {
                offset += 0.05 + paperLine.getHeight(player) + paperLine.getOffsetAfter();
            }
        }
        return offset;
    }

    public void addPage(final PaperStaticHologramLine<?> hologramLine) {
        this.pages.add(hologramLine);
    }
}
