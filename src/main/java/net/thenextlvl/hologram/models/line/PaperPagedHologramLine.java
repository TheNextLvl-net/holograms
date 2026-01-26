package net.thenextlvl.hologram.models.line;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Location;
import org.bukkit.World;
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
public class PaperPagedHologramLine implements PagedHologramLine {
    private final PaperHologram hologram;
    private final List<PaperHologramLine<?>> pages = new CopyOnWriteArrayList<>();
    private final Map<Player, Integer> currentPageIndex = new ConcurrentHashMap<>();
    private final Random random = new Random();

    private volatile Duration interval = Duration.ofSeconds(2);
    private volatile boolean randomOrder = false;
    private volatile boolean paused = false;
    private volatile boolean glowing = false;
    private volatile @Nullable TextColor glowColor = null;
    private volatile @Nullable ScheduledTask cycleTask = null;

    public PaperPagedHologramLine(final PaperHologram hologram) {
        this.hologram = hologram;
    }

    @Override
    public PaperHologram getHologram() {
        return hologram;
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
    public World getWorld() {
        return hologram.getWorld();
    }

    @Override
    public boolean isGlowing() {
        return glowing;
    }

    @Override
    public PagedHologramLine setGlowing(final boolean glowing) {
        this.glowing = glowing;
        pages.forEach(page -> page.setGlowing(glowing));
        return this;
    }

    @Override
    public Optional<TextColor> getGlowColor() {
        return Optional.ofNullable(glowColor);
    }

    @Override
    public PagedHologramLine setGlowColor(final @Nullable TextColor color) {
        this.glowColor = color;
        pages.forEach(page -> page.setGlowColor(color));
        return this;
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
        final var page = new PaperTextHologramLine(hologram);
        page.setGlowing(glowing);
        page.setGlowColor(glowColor);
        pages.add(page);
        return page;
    }

    @Override
    public ItemHologramLine addItemPage() {
        final var page = new PaperItemHologramLine(hologram);
        page.setGlowing(glowing);
        page.setGlowColor(glowColor);
        pages.add(page);
        return page;
    }

    @Override
    public BlockHologramLine addBlockPage() {
        final var page = new PaperBlockHologramLine(hologram);
        page.setGlowing(glowing);
        page.setGlowColor(glowColor);
        pages.add(page);
        return page;
    }

    @Override
    public EntityHologramLine addEntityPage(final EntityType entityType) throws IllegalArgumentException {
        final var entityClass = entityType.getEntityClass();
        if (entityClass == null) throw new IllegalArgumentException("Entity type is not spawnable: " + entityType);
        final var page = new PaperEntityHologramLine<>(hologram, entityClass);
        page.setGlowing(glowing);
        page.setGlowColor(glowColor);
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
        if (!(page instanceof final PaperHologramLine<?> paperPage)) return false;
        final var removed = pages.remove(paperPage);
        if (removed) paperPage.despawn();
        return removed;
    }

    @Override
    public void clearPages() {
        pages.forEach(PaperHologramLine::despawn);
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

    public double getHeight(final Player player) {
        return getCurrentPage(player)
                .filter(PaperHologramLine.class::isInstance)
                .map(PaperHologramLine.class::cast)
                .map(page -> page.getHeight(player))
                .orElse(0d);
    }

    public double getOffsetBefore(final Player player) {
        return getCurrentPage(player)
                .filter(PaperHologramLine.class::isInstance)
                .map(PaperHologramLine.class::cast)
                .map(page -> page.getOffsetBefore(player))
                .orElse(0d);
    }

    public double getOffsetAfter() {
        return pages.stream()
                .mapToDouble(PaperHologramLine::getOffsetAfter)
                .max()
                .orElse(0d);
    }

    public Entity spawn(final Player player, final double offset) throws IllegalStateException {
        if (pages.isEmpty()) throw new IllegalStateException("No pages to spawn");
        currentPageIndex.put(player, 0);
        final var page = pages.getFirst();
        startCycleTask();
        return page.spawn(player, offset);
    }

    public void despawn() {
        stopCycleTask();
        pages.forEach(PaperHologramLine::despawn);
        currentPageIndex.clear();
    }

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
        final var oldPage = pages.get(oldIndex);

        final int newIndex;
        if (randomOrder) {
            newIndex = random.nextInt(pages.size());
        } else {
            newIndex = (oldIndex + 1) % pages.size();
        }

        final var newPage = pages.get(newIndex);

        oldPage.despawn(player);
        newPage.spawn(player, offset);
        currentPageIndex.put(player, newIndex);
    }

    private void startCycleTask() {
        if (cycleTask != null || paused || pages.size() <= 1) return;

        final var ticks = interval.toMillis() / 50;
        cycleTask = hologram.getPlugin().getServer().getGlobalRegionScheduler().runAtFixedRate(
                hologram.getPlugin(),
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
            if (player.isOnline() && hologram.isSpawned(player)) {
                cyclePage(player, calculateOffset(player));
            }
        });
    }

    private double calculateOffset(final Player player) {
        var offset = 0d;
        final var lines = hologram.getLines().toList();
        for (var i = lines.size() - 1; i >= 0; i--) {
            final var line = lines.get(i);
            if (line == this) return offset;
            if (line instanceof final PaperHologramLine<?> paperLine) {
                offset += 0.05 + paperLine.getHeight(player) + paperLine.getOffsetAfter();
            }
        }
        return offset;
    }
}
