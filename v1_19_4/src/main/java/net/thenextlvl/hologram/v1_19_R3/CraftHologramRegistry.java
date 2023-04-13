package net.thenextlvl.hologram.v1_19_R3;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.hologram.api.Hologram;
import net.thenextlvl.hologram.api.HologramRegistry;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class CraftHologramRegistry implements HologramRegistry {
    private Collection<Hologram> holograms = new ArrayList<>();

    @Override
    public void register(Hologram hologram) {
        holograms.add(hologram);
    }

    @Override
    public void unregister(Hologram hologram) {
        holograms.remove(hologram);
    }

    @Override
    public boolean isRegistered(Hologram hologram) {
        return holograms.contains(hologram);
    }
}