package net.thenextlvl.hologram.service;

import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.HologramProvider;
import net.thenextlvl.service.hologram.Hologram;
import net.thenextlvl.service.hologram.HologramCapability;
import net.thenextlvl.service.hologram.HologramController;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
public final class ServiceHologramController implements HologramController {
    private final HologramProvider provider = HologramProvider.instance();
    private final Set<HologramCapability> capabilities = EnumSet.allOf(HologramCapability.class);
    private final HologramPlugin plugin;

    public ServiceHologramController(final HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Hologram createHologram(final String name, final Location location) {
        return new ServiceHologram(provider.createHologram(name, location));
    }

    @Override
    public boolean deleteHologram(final Hologram hologram) {
        return hologram instanceof ServiceHologram(
                final net.thenextlvl.hologram.Hologram service
        ) && provider.deleteHologram(service);
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
    public @Unmodifiable Set<HologramCapability> getCapabilities() {
        return Set.copyOf(capabilities);
    }

    @Override
    public boolean hasCapabilities(final Collection<HologramCapability> capabilities) {
        return this.capabilities.containsAll(capabilities);
    }

    @Override
    public boolean hasCapability(final HologramCapability capability) {
        return capabilities.contains(capability);
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public String getName() {
        return "Holograms";
    }
}
