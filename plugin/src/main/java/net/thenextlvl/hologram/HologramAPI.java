package net.thenextlvl.hologram;

import net.thenextlvl.hologram.api.HologramProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class HologramAPI extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getServicesManager().register(HologramProvider.class, getHologramProvider(), this, ServicePriority.Normal);
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
