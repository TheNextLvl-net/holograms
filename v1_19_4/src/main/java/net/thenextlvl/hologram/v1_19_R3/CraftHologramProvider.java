package net.thenextlvl.hologram.v1_19_R3;

import lombok.Getter;
import net.thenextlvl.hologram.api.HologramProvider;
import net.thenextlvl.hologram.v1_19_R3.event.CraftHologramListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class CraftHologramProvider implements HologramProvider {
    private final CraftHologramRegistry hologramRegistry = new CraftHologramRegistry();
    private final CraftHologramFactory hologramFactory = new CraftHologramFactory();
    private final CraftHologramLoader hologramLoader = new CraftHologramLoader();

    public CraftHologramProvider(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new CraftHologramListener(this), plugin);
    }
}
