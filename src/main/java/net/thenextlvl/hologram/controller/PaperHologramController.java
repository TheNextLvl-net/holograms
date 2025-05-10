package net.thenextlvl.hologram.controller;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.thenextlvl.hologram.BlockHologram;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramController;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.ItemHologram;
import net.thenextlvl.hologram.TextHologram;
import net.thenextlvl.hologram.model.PaperBlockHologram;
import net.thenextlvl.hologram.model.PaperItemHologram;
import net.thenextlvl.hologram.model.PaperTextHologram;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

@NullMarked
public class PaperHologramController implements HologramController {
    private final HologramPlugin plugin;
    private final Map<String, Hologram<?>> holograms = new HashMap<>();

    public PaperHologramController(HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Display> Optional<Hologram<E>> getHologram(E display) {
        return holograms.values().stream()
                .filter(hologram -> hologram.getEntity()
                        .filter(display::equals)
                        .isPresent()
                ).map(character -> (Hologram<E>) character)
                .findFirst();
    }

    @Override
    public Optional<BlockHologram> getHologram(BlockDisplay display) {
        return getHologram((Display) display).map(BlockHologram.class::cast);
    }

    @Override
    public Optional<ItemHologram> getHologram(ItemDisplay display) {
        return getHologram((Display) display).map(ItemHologram.class::cast);
    }

    @Override
    public Optional<TextHologram> getHologram(TextDisplay display) {
        return getHologram((Display) display).map(TextHologram.class::cast);
    }

    @Override
    public Optional<Hologram<?>> getHologram(String name) {
        return Optional.ofNullable(holograms.get(name));
    }

    @Override
    public Optional<Hologram<?>> getHologram(UUID uuid) {
        return holograms.values().stream()
                .filter(hologram -> hologram.getEntity()
                        .map(Entity::getUniqueId)
                        .filter(uuid::equals)
                        .isPresent()
                ).findFirst();
    }

    @Override
    public @Unmodifiable Collection<? extends Hologram<?>> getHolograms() {
        return List.copyOf(holograms.values());
    }

    @Override
    public @Unmodifiable Collection<? extends Hologram<?>> getHolograms(Player player) {
        return holograms.values().stream()
                .filter(hologram -> hologram.canSee(player))
                .toList();
    }

    @Override
    public @Unmodifiable Collection<? extends Hologram<?>> getHolograms(World world) {
        return holograms.values().stream()
                .filter(hologram -> world.equals(hologram.getWorld()))
                .toList();
    }

    @Override
    public @Unmodifiable Collection<? extends Hologram<?>> getHologramNearby(Location location, double radius) {
        Preconditions.checkArgument(radius > 0, "Radius must be greater than 0");
        Preconditions.checkArgument(location.getWorld() != null, "World cannot be null");
        var radiusSquared = radius * radius;
        return getHolograms(location.getWorld()).stream()
                .filter(hologram -> hologram.getLocation() != null)
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
    public boolean isHologram(Entity entity) {
        return holograms.values().stream().anyMatch(hologram ->
                hologram.getEntity().map(entity::equals).orElse(false));
    }

    @Override
    public TextHologram createTextHologram(String name) throws IllegalStateException {
        return createHologram(PaperTextHologram::new, name);
    }

    @Override
    public BlockHologram createBlockHologram(String name) throws IllegalStateException {
        return createHologram(PaperBlockHologram::new, name);
    }

    @Override
    public ItemHologram createItemHologram(String name) throws IllegalStateException {
        return createHologram(PaperItemHologram::new, name);
    }

    @Override
    public TextHologram createHologram(String name, Component text) {
        var hologram = createTextHologram(name);
        hologram.setText(text);
        return hologram;
    }

    @Override
    public BlockHologram createHologram(String name, BlockData block) {
        var hologram = createBlockHologram(name);
        hologram.setBlock(block);
        return hologram;
    }

    @Override
    public ItemHologram createHologram(String name, ItemStack itemStack) {
        var hologram = createItemHologram(name);
        hologram.setItemStack(itemStack);
        return hologram;
    }

    @Override
    public TextHologram spawnHologram(String name, Location location, Component text) {
        return spawn(createHologram(name, text), location);
    }

    @Override
    public BlockHologram spawnHologram(String name, Location location, BlockData block) {
        return spawn(createHologram(name, block), location);
    }

    @Override
    public ItemHologram spawnHologram(String name, Location location, ItemStack itemStack) {
        return spawn(createHologram(name, itemStack), location);
    }

    private <T extends Hologram<?>> T createHologram(BiFunction<HologramPlugin, String, T> constructor, String name) {
        Preconditions.checkState(!hologramExists(name), "Hologram named %s already exists", name);
        var hologram = constructor.apply(plugin, name);
        holograms.put(name, hologram);
        return hologram;
    }

    private <T extends Hologram<?>> T spawn(T hologram, Location location) {
        hologram.spawn(location);
        return hologram;
    }

    public void unregister(String name) {
        holograms.remove(name);
    }
}
