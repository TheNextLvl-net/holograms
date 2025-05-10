package net.thenextlvl.hologram;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.thenextlvl.hologram.command.HologramCommand;
import net.thenextlvl.hologram.controller.PaperHologramController;
import net.thenextlvl.hologram.listener.HologramListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

@NullMarked
public class HologramPlugin extends JavaPlugin {
    private final PaperHologramController controller = new PaperHologramController(this);
    private final Metrics metrics = new Metrics(this, 25817);

    public HologramPlugin() {
        getServer().getServicesManager().register(HologramController.class, controller, this, ServicePriority.Normal);
    }

    @Override
    public void onEnable() {
        registerCommands();
        registerListeners();
    }

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(HologramCommand.create(this), Set.of("holo")));
    }

    private void registerListeners() {
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
