package net.thenextlvl.hologram;

public interface HologramProvider {

    HologramRegistry getHologramRegistry();

    HologramFactory getHologramFactory();

    HologramLoader getHologramLoader();
}
