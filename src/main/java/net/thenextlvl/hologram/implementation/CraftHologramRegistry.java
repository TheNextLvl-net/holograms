package net.thenextlvl.hologram.implementation;

import com.google.common.base.Preconditions;
import net.thenextlvl.hologram.api.HologramRegistry;
import net.thenextlvl.hologram.api.hologram.Hologram;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;

@NullMarked
public class CraftHologramRegistry implements HologramRegistry {
    private final Collection<Hologram> holograms = new ArrayList<>();

    @Override
    public Collection<Hologram> getHolograms() {
        return holograms;
    }

    @Override
    public void register(Hologram hologram) throws IllegalArgumentException {
        Preconditions.checkArgument(!isRegistered(hologram), "Hologram already registered");
        holograms.add(hologram);
    }

    @Override
    public void unregister(Hologram hologram) throws IllegalArgumentException {
        Preconditions.checkArgument(isRegistered(hologram), "Hologram not registered");
        holograms.remove(hologram);
    }

    @Override
    public boolean isRegistered(Hologram hologram) {
        return holograms.contains(hologram);
    }
}
