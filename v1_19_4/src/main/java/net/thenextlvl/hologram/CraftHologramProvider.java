package net.thenextlvl.hologram;

import lombok.Getter;

@Getter
public class CraftHologramProvider implements HologramProvider {
    private final CraftHologramRegistry hologramRegistry = new CraftHologramRegistry();
    private final CraftHologramFactory hologramFactory = new CraftHologramFactory();
    private final CraftHologramLoader hologramLoader = new CraftHologramLoader();
}
