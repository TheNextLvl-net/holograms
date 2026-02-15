package net.thenextlvl.hologram.service;

import net.kyori.adventure.text.Component;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.HologramProvider;
import net.thenextlvl.service.api.capability.CapabilityException;
import net.thenextlvl.service.api.hologram.Hologram;
import net.thenextlvl.service.api.hologram.HologramCapability;
import net.thenextlvl.service.api.hologram.HologramController;
import net.thenextlvl.service.api.hologram.HologramLine;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NullMarked
public final class ServiceHologramController implements HologramController {
    private final HologramProvider provider = HologramProvider.instance();

    @Override
    public Hologram createHologram(final String name, final Location location, final Collection<HologramLine<?>> lines) throws CapabilityException {
        final var hologram = new ServiceHologram(provider.createHologram(name, location));
        hologram.addLines(lines); // fixme: aint gonna work
        return hologram;
    }

    @Override
    public HologramLine<BlockData> createLine(final BlockData block) throws CapabilityException {
        return null; // todo
    }

    @Override
    public HologramLine<Component> createLine(final Component text) throws CapabilityException {
        return null; // todo
    }

    @Override
    public HologramLine<EntityType> createLine(final EntityType entity) throws CapabilityException {
        return null; // todo    
    }

    @Override
    public HologramLine<ItemStack> createLine(final ItemStack itemStack) throws CapabilityException {
        return null; // todo
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms() {
        return provider.getHolograms()
                .map(ServiceHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms(final Player player) {
        return provider.getHolograms(player)
                .map(ServiceHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms(final World world) {
        return provider.getHolograms(world)
                .map(ServiceHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Optional<Hologram> getHologram(final String name) {
        return provider.getHologram(name).map(ServiceHologram::new);
    }

    @Override
    public @Unmodifiable EnumSet<HologramCapability> getCapabilities() {
        return EnumSet.allOf(HologramCapability.class);
    }

    @Override
    public boolean hasCapabilities(final Collection<HologramCapability> capabilities) {
        return true;
    }

    @Override
    public boolean hasCapability(final HologramCapability capability) {
        return true;
    }

    @Override
    public Plugin getPlugin() {
        return JavaPlugin.getPlugin(HologramPlugin.class);
    }

    @Override
    public String getName() {
        return "Holograms";
    }
}
