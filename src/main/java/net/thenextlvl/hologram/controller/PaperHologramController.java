package net.thenextlvl.hologram.controller;

import com.google.common.base.Preconditions;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramController;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

@NullMarked
public class PaperHologramController implements HologramController {
    private final HologramPlugin plugin;
    public final Set<Hologram> holograms = new HashSet<>();

    public PaperHologramController(HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Path getDataFolder(World world) {
        return world.getWorldPath().resolve("holograms");
    }

    @Override
    public Optional<Hologram> getHologram(Entity entity) {
        return getHolograms(entity.getWorld())
                .filter(hologram -> hologram.getLines().stream().anyMatch(line ->
                        line.getEntity().filter(entity::equals).isPresent()))
                .findAny();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Entity> Optional<HologramLine<E>> getHologramLine(E entity) {
        return getHolograms(entity.getWorld())
                .filter(hologram -> hologram.getLines().stream().anyMatch(line ->
                        line.getEntity().filter(entity::equals).isPresent()))
                .map(character -> (HologramLine<E>) character)
                .findFirst();
    }

    @Override
    public Optional<BlockHologramLine> getHologramLine(BlockDisplay display) {
        return getHologramLine((Entity) display).map(BlockHologramLine.class::cast);
    }

    @Override
    public Optional<ItemHologramLine> getHologramLine(ItemDisplay display) {
        return getHologramLine((Entity) display).map(ItemHologramLine.class::cast);
    }

    @Override
    public Optional<TextHologramLine> getHologramLine(TextDisplay display) {
        return getHologramLine((Entity) display).map(TextHologramLine.class::cast);
    }

    @Override
    public Optional<Hologram> getHologram(String name) {
        return getHolograms().filter(hologram -> hologram.getName().equals(name))
                .findAny();
    }

    @Override
    public Optional<HologramLine<?>> getHologramLine(UUID uuid) {
        return getHolograms().flatMap(hologram -> hologram.getLines().stream().filter(line ->
                line.getEntity().map(Entity::getUniqueId).filter(uuid::equals).isPresent())).findAny();
    }

    @Override
    public Stream<Hologram> getHolograms() {
        return holograms.stream();
    }

    @Override
    public Stream<Hologram> getHolograms(org.bukkit.Chunk chunk) {
        return getHolograms(chunk.getWorld()).filter(hologram -> {
            var chunkX = hologram.getLocation().getBlockX() >> 4;
            var chunkZ = hologram.getLocation().getBlockZ() >> 4;
            return chunkX == chunk.getX() && chunkZ == chunk.getZ();
        });
    }

    @Override
    public Stream<Hologram> getHolograms(Player player) {
        return getHolograms().filter(hologram -> hologram.canSee(player));
    }

    @Override
    public Stream<Hologram> getHolograms(World world) {
        return getHolograms().filter(hologram -> world.equals(hologram.getWorld()));
    }

    @Override
    public @Unmodifiable Collection<? extends Hologram> getHologramNearby(Location location, double radius) {
        Preconditions.checkArgument(radius > 0, "Radius must be greater than 0");
        Preconditions.checkArgument(location.getWorld() != null, "World cannot be null");
        var radiusSquared = radius * radius;
        return getHolograms(location.getWorld())
                .filter(hologram -> hologram.getLocation().distanceSquared(location) <= radiusSquared)
                .toList();
    }

    @Override
    public Stream<String> getHologramNames() {
        return getHolograms().map(Hologram::getName);
    }

    @Override
    public boolean hologramExists(String name) {
        return getHolograms().anyMatch(hologram -> hologram.getName().equals(name));
    }

    @Override
    public boolean isHologramPart(Entity entity) {
        return getHolograms(entity.getWorld()).anyMatch(hologram -> hologram.getLines().stream().anyMatch(line ->
                line.getEntity().filter(entity::equals).isPresent()));
    }

    @Override
    public Hologram createHologram(String name, Location location) throws IllegalStateException {
        Preconditions.checkState(!hologramExists(name), "Hologram named %s already exists", name);
        var hologram = new PaperHologram(plugin, name, location);
        holograms.add(hologram);
        return hologram;
    }

    @Override
    public Hologram spawnHologram(String name, Location location, Consumer<Hologram> preSpawn) throws IllegalStateException {
        var hologram = createHologram(name, location);
        preSpawn.accept(hologram);
        hologram.spawn();
        return hologram;
    }

    @Override
    public boolean deleteHologram(Hologram hologram) {
        hologram.despawn();
        try {
            Files.deleteIfExists(hologram.getDataFile());
            Files.deleteIfExists(hologram.getBackupFile());
        } catch (IOException e) {
            plugin.getComponentLogger().warn("Failed to delete hologram data: {}", hologram.getName(), e);
        }
        return unregister(hologram);
    }

    @Override
    public void forEachHologram(Consumer<Hologram> action) {
        holograms.forEach(action);
    }

    public boolean unregister(Hologram hologram) {
        return holograms.remove(hologram);
    }

    public Server getServer() {
        return plugin.getServer();
    }

    public HologramPlugin getPlugin() {
        return plugin;
    }
}
