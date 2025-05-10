package net.thenextlvl.hologram;

import net.thenextlvl.hologram.implementation.PaperHologramController;
import net.thenextlvl.hologram.listener.HologramListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class HologramPlugin extends JavaPlugin {
    private final PaperHologramController controller = new PaperHologramController(this);
    private final Metrics metrics = new Metrics(this, 25817);

    public HologramPlugin() {
        getServer().getServicesManager().register(HologramController.class, controller, this, ServicePriority.Normal);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new HologramListener(this), this);
    }

    @Override
    public void onDisable() {
        metrics.shutdown();
    }

    public PaperHologramController hologramController() {
        return controller;
    }
}
