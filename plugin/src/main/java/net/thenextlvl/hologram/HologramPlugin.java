package net.thenextlvl.hologram;

import core.bukkit.plugin.CorePlugin;
import net.thenextlvl.hologram.api.HologramProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public class HologramPlugin extends CorePlugin {

    @Override
    public void onEnable() {
        Bukkit.getServicesManager().register(HologramProvider.class, getHologramProvider(), this, ServicePriority.Normal);
        Bukkit.getScheduler().runTask(this, () -> {
            // TODO: 10.04.23 load holograms from file
        });
    }

    private HologramProvider getHologramProvider() {
        var version = Bukkit.getBukkitVersion();
        if (version.contains("1.19.4"))
            return new net.thenextlvl.hologram.v1_19_R3.CraftHologramProvider();
        throw new IllegalStateException("Your server version is not supported: " + version);
    }

    @Override
    public void onDisable() {
        Bukkit.getServicesManager().unregisterAll(this);
    }
}
