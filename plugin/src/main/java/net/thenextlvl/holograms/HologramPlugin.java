package net.thenextlvl.holograms;

import core.bukkit.plugin.CorePlugin;
import net.thenextlvl.hologram.HologramProvider;
import net.thenextlvl.hologram.CraftHologramProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public class HologramPlugin extends CorePlugin {

    @Override
    public void onEnable() {
        Bukkit.getServicesManager().register(HologramProvider.class, getHologramProvider(), this, ServicePriority.Normal);
        Bukkit.getScheduler().runTask(this, () -> {

        });
    }

    private HologramProvider getHologramProvider() {
        return new CraftHologramProvider();
    }

    @Override
    public void onDisable() {
        Bukkit.getServicesManager().unregisterAll(this);
    }
}
