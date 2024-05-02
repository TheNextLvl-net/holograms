package net.thenextlvl.hologram.implementation;

import lombok.Getter;
import net.thenextlvl.hologram.api.HologramProvider;

@Getter
public class CraftHologramProvider implements HologramProvider {
    private final CraftHologramRegistry hologramRegistry = new CraftHologramRegistry();
    private final CraftHologramFactory hologramFactory = new CraftHologramFactory();
    private final CraftHologramLoader hologramLoader = new CraftHologramLoader();
}
