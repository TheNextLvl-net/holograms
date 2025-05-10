package net.thenextlvl.hologram.implementation;

import net.thenextlvl.hologram.api.HologramFactory;
import net.thenextlvl.hologram.api.HologramProvider;
import net.thenextlvl.hologram.api.HologramRegistry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CraftHologramProvider implements HologramProvider {
    private final CraftHologramRegistry hologramRegistry = new CraftHologramRegistry();
    private final CraftHologramFactory hologramFactory = new CraftHologramFactory();

    @Override
    public HologramRegistry getHologramRegistry() {
        return hologramRegistry;
    }

    @Override
    public HologramFactory getHologramFactory() {
        return hologramFactory;
    }
}
