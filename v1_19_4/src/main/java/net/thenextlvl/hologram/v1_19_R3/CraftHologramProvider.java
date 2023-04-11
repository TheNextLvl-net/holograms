package net.thenextlvl.hologram.v1_19_R3;

import core.bukkit.plugin.CorePlugin;
import lombok.Getter;
import net.thenextlvl.hologram.api.HologramProvider;
import net.thenextlvl.hologram.v1_19_R3.event.CraftHologramListener;

@Getter
public class CraftHologramProvider implements HologramProvider {
    private final CraftHologramRegistry hologramRegistry = new CraftHologramRegistry();
    private final CraftHologramFactory hologramFactory = new CraftHologramFactory();
    private final CraftHologramLoader hologramLoader = new CraftHologramLoader();

    public CraftHologramProvider(CorePlugin plugin) {
        plugin.registerListener(new CraftHologramListener(this));
    }
}
