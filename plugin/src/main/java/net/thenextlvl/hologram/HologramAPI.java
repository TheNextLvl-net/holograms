package net.thenextlvl.hologram;

import net.thenextlvl.hologram.api.HologramProvider;
import net.thenextlvl.hologram.listener.HologramListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class HologramAPI extends JavaPlugin {

    @Override
    public void onEnable() {
        var provider = getHologramProvider();
        Bukkit.getServicesManager().register(HologramProvider.class, provider, this, ServicePriority.Normal);
        Bukkit.getPluginManager().registerEvents(new HologramListener(provider), this);
    }

    private HologramProvider getHologramProvider() {
        var version = Bukkit.getBukkitVersion();
        if (version.contains("1.19.4"))
            return new net.thenextlvl.hologram.v1_19_R3.CraftHologramProvider();
        if (version.contains("1.20.1"))
            return new net.thenextlvl.hologram.v1_20_R1.CraftHologramProvider();
        throw new IllegalStateException("Your server version is not supported: " + version);
    }

    @Override
    public void onDisable() {
        Bukkit.getServicesManager().unregisterAll(this);
    }
}
