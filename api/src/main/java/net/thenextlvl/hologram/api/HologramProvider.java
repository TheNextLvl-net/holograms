package net.thenextlvl.hologram.api;

public interface HologramProvider {

    HologramRegistry getHologramRegistry();

    HologramFactory getHologramFactory();

    HologramLoader getHologramLoader();
}
