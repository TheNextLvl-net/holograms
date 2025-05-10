package net.thenextlvl.hologram;

import net.thenextlvl.hologram.api.HologramProvider;
import net.thenextlvl.hologram.implementation.CraftHologramProvider;
import net.thenextlvl.hologram.listener.HologramListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class HologramPlugin extends JavaPlugin {
    private final HologramProvider provider = new CraftHologramProvider();
    private final Metrics metrics = new Metrics(this, 20033);

    public HologramPlugin() {
        getServer().getServicesManager().register(HologramProvider.class, provider, this, ServicePriority.Normal);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new HologramListener(provider), this);
    }

    @Override
    public void onDisable() {
        metrics.shutdown();
    }
}
