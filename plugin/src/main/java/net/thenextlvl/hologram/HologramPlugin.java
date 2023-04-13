package net.thenextlvl.hologram;

import core.bukkit.plugin.CorePlugin;
import lombok.Getter;
import net.thenextlvl.hologram.api.Hologram;
import net.thenextlvl.hologram.api.HologramProvider;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.Nullable;

@Getter
public class HologramPlugin extends CorePlugin {
    private @Nullable RegisteredServiceProvider<HologramProvider> registration;

    @Override
    public void onEnable() {
        Bukkit.getServicesManager().register(HologramProvider.class, getHologramProvider(), this, ServicePriority.Normal);
        this.registration = Bukkit.getServicesManager().getRegistration(HologramProvider.class);
        assert registration != null;
        HologramProvider provider = registration.getProvider();
        provider.getHologramRegistry().register(test(provider));
    }

    private Hologram test(HologramProvider provider) {
        var factory = provider.getHologramFactory();
        var location = Bukkit.getWorlds().get(0).getSpawnLocation();
        return factory.createHologram(location, factory.createBlockLine(block -> {
            block.setBlock(Material.LAVA_CAULDRON.createBlockData());
            block.setGlowColorOverride(Color.BLACK);
            block.setGlowing(true);
        }));
    }

    private HologramProvider getHologramProvider() {
        var version = Bukkit.getBukkitVersion();
        if (version.contains("1.19.4"))
            return new net.thenextlvl.hologram.v1_19_R3.CraftHologramProvider(this);
        throw new IllegalStateException("Your server version is not supported: " + version);
    }

    @Override
    public void onDisable() {
        Bukkit.getServicesManager().unregisterAll(this);
    }
}
