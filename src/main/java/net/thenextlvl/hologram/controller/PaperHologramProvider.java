package net.thenextlvl.hologram.controller;

import com.google.common.base.Preconditions;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.HologramProvider;
import net.thenextlvl.hologram.event.HologramCreateEvent;
import net.thenextlvl.hologram.event.HologramDeleteEvent;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static net.thenextlvl.hologram.HologramPlugin.ISSUES;

@NullMarked
public class PaperHologramProvider implements HologramProvider {
    private final HologramPlugin plugin;
    public final Set<Hologram> holograms = new HashSet<>();

    public PaperHologramProvider(final HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Path getDataFolder(final World world) {
        return world.getWorldPath().resolve("holograms");
    }

    @Override
    public Optional<Hologram> getHologram(final Entity entity) {
        return getHolograms(entity.getWorld())
                .filter(hologram -> hologram.getLines().anyMatch(line ->
                        line.getEntity().filter(entity::equals).isPresent()))
                .findAny();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Entity> Optional<HologramLine<E>> getHologramLine(final E entity) {
        return getHolograms(entity.getWorld())
                .filter(hologram -> hologram.getLines().anyMatch(line ->
                        line.getEntity().filter(entity::equals).isPresent()))
                .map(hologram -> (HologramLine<E>) hologram)
                .findFirst();
    }

    @Override
    public Optional<BlockHologramLine> getHologramLine(final BlockDisplay display) {
        return getHologramLine((Entity) display).map(BlockHologramLine.class::cast);
    }

    @Override
    public Optional<ItemHologramLine> getHologramLine(final ItemDisplay display) {
        return getHologramLine((Entity) display).map(ItemHologramLine.class::cast);
    }

    @Override
    public Optional<TextHologramLine> getHologramLine(final TextDisplay display) {
        return getHologramLine((Entity) display).map(TextHologramLine.class::cast);
    }

    @Override
    public Optional<Hologram> getHologram(final String name) {
        return getHolograms().filter(hologram -> hologram.getName().equals(name))
                .findAny();
    }

    @Override
    public Optional<HologramLine<?>> getHologramLine(final UUID uuid) {
        return getHolograms().flatMap(hologram -> hologram.getLines().filter(line ->
                line.getEntity().map(Entity::getUniqueId).filter(uuid::equals).isPresent())).findAny();
    }

    @Override
    public Stream<Hologram> getHolograms() {
        return holograms.stream();
    }

    @Override
    public Stream<Hologram> getHolograms(final Chunk chunk) {
        return getHolograms(chunk.getWorld()).filter(hologram -> {
            return ((PaperHologram) hologram).isInChunk(chunk);
        });
    }

    @Override
    public Stream<Hologram> getHolograms(final Player player) {
        return getHolograms().filter(hologram -> hologram.canSee(player));
    }

    @Override
    public Stream<Hologram> getHolograms(final World world) {
        return getHolograms().filter(hologram -> world.equals(hologram.getWorld()));
    }

    @Override
    public Stream<Hologram> getHologramNearby(final Location location, final double radius) {
        Preconditions.checkArgument(radius > 0, "Radius must be greater than 0");
        Preconditions.checkArgument(location.getWorld() != null, "World cannot be null");
        final var radiusSquared = radius * radius;
        return getHolograms(location.getWorld()).filter(hologram -> {
            return hologram.getLocation().distanceSquared(location) <= radiusSquared;
        });
    }

    @Override
    public Stream<String> getHologramNames() {
        return getHolograms().map(Hologram::getName);
    }

    @Override
    public boolean hasHologram(final String name) {
        return getHolograms().anyMatch(hologram -> hologram.getName().equals(name));
    }

    @Override
    public boolean hasHologram(final Hologram hologram) {
        return holograms.contains(hologram);
    }

    @Override
    public boolean isHologramPart(final Entity entity) {
        return getHolograms(entity.getWorld()).anyMatch(hologram -> hologram.getLines().anyMatch(line ->
                line.getEntity().filter(entity::equals).isPresent()));
    }

    @Override
    public Hologram createHologram(final String name, final Location location) throws IllegalStateException {
        Preconditions.checkState(!hasHologram(name), "Hologram named %s already exists", name);
        final var hologram = new PaperHologram(plugin, name, location);
        holograms.add(hologram);
        new HologramCreateEvent(hologram).callEvent();
        return hologram;
    }

    @Override
    public Hologram spawnHologram(final String name, final Location location, final Consumer<Hologram> preSpawn) throws IllegalStateException {
        final var hologram = createHologram(name, location);
        preSpawn.accept(hologram);
        hologram.spawn();
        return hologram;
    }

    @Override
    public boolean deleteHologram(final Hologram hologram) {
        if (!new HologramDeleteEvent(hologram).callEvent()) return false;

        hologram.despawn();
        try {
            Files.deleteIfExists(hologram.getDataFile());
            Files.deleteIfExists(hologram.getBackupFile());
        } catch (final IOException e) {
            plugin.getComponentLogger().warn("Failed to delete hologram data: {}", hologram.getName(), e);
            plugin.getComponentLogger().warn("Please look for similar issues or report this on GitHub: {}", ISSUES);
            HologramPlugin.ERROR_TRACKER.trackError(e);
        }
        return unregister(hologram);
    }

    @Override
    public void forEachHologram(final Consumer<Hologram> action) {
        holograms.forEach(action);
    }

    public boolean unregister(final Hologram hologram) {
        return holograms.remove(hologram);
    }

    public Server getServer() {
        return plugin.getServer();
    }

    public HologramPlugin getPlugin() {
        return plugin;
    }
}
