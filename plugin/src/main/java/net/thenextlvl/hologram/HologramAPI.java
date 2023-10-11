package net.thenextlvl.hologram;

import core.annotation.FieldsAreNotNullByDefault;
import core.annotation.MethodsReturnNotNullByDefault;
import net.thenextlvl.hologram.api.HologramProvider;
import net.thenextlvl.hologram.listener.HologramListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

@FieldsAreNotNullByDefault
@MethodsReturnNotNullByDefault
public class HologramAPI extends JavaPlugin {
    private final Metrics metrics = new Metrics(this, 20033);

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
        if (version.contains("1.20.2"))
            return new net.thenextlvl.hologram.v1_20_R2.CraftHologramProvider();
        throw new IllegalStateException("Your server version is not supported: " + version);
    }

    @Override
    public void onDisable() {
        Bukkit.getServicesManager().unregisterAll(this);
        metrics.shutdown();
    }
}
