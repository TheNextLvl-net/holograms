package net.thenextlvl.hologram.v1_20_R3;

import lombok.Getter;
import net.thenextlvl.hologram.api.HologramProvider;

@Getter
public class CraftHologramProvider implements HologramProvider {
    private final CraftHologramRegistry hologramRegistry = new CraftHologramRegistry();
    private final CraftHologramFactory hologramFactory = new CraftHologramFactory();
    private final CraftHologramLoader hologramLoader = new CraftHologramLoader();
}
