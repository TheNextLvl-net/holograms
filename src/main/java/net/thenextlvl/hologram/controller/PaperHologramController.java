package net.thenextlvl.hologram.controller;

import com.google.common.base.Preconditions;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramController;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.model.PaperHologram;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@NullMarked
public class PaperHologramController implements HologramController {
    private final HologramPlugin plugin;
    private final Map<String, Hologram> holograms = new HashMap<>();

    public PaperHologramController(HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Entity> Optional<HologramLine<E>> getHologramLine(E entity) {
        return holograms.values().stream()
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
        return Optional.ofNullable(holograms.get(name));
    }

    @Override
    public Optional<HologramLine<?>> getHologramLine(UUID uuid) {
        return holograms.values().stream().flatMap(hologram -> hologram.getLines().stream().filter(line ->
                line.getEntity().map(Entity::getUniqueId).filter(uuid::equals).isPresent())).findAny();
    }

    @Override
    public @Unmodifiable Collection<? extends Hologram> getHolograms() {
        return List.copyOf(holograms.values());
    }

    @Override
    public @Unmodifiable Collection<? extends Hologram> getHolograms(Player player) {
        return holograms.values().stream()
                .filter(hologram -> hologram.canSee(player))
                .toList();
    }

    @Override
    public @Unmodifiable Collection<? extends Hologram> getHolograms(World world) {
        return holograms.values().stream()
                .filter(hologram -> world.equals(hologram.getWorld()))
                .toList();
    }

    @Override
    public @Unmodifiable Collection<? extends Hologram> getHologramNearby(Location location, double radius) {
        Preconditions.checkArgument(radius > 0, "Radius must be greater than 0");
        Preconditions.checkArgument(location.getWorld() != null, "World cannot be null");
        var radiusSquared = radius * radius;
        return getHolograms(location.getWorld()).stream()
                .filter(hologram -> hologram.getLocation().distanceSquared(location) <= radiusSquared)
                .toList();
    }

    @Override
    public @Unmodifiable Set<String> getHologramNames() {
        return Set.copyOf(holograms.keySet());
    }

    @Override
    public boolean hologramExists(String name) {
        return holograms.containsKey(name);
    }

    @Override
    public boolean isHologramPart(Entity entity) {
        return holograms.values().stream().anyMatch(hologram -> hologram.getLines().stream().anyMatch(line ->
                line.getEntity().filter(entity::equals).isPresent()));
    }

    @Override
    public Hologram createHologram(String name, Location location) throws IllegalStateException {
        Preconditions.checkState(!hologramExists(name), "Hologram named %s already exists", name);
        var hologram = new PaperHologram(this, name, location);
        holograms.put(name, hologram);
        return hologram;
    }

    @Override
    public Hologram spawnHologram(String name, Location location, Consumer<Hologram> preSpawn) throws IllegalStateException {
        var hologram = createHologram(name, location);
        preSpawn.accept(hologram);
        hologram.spawn();
        return hologram;
    }

    public void unregister(String name) {
        holograms.remove(name);
    }

    public Server getServer() {
        return plugin.getServer();
    }

    public HologramPlugin getPlugin() {
        return plugin;
    }
}
