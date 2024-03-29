package net.thenextlvl.hologram.v1_20_R2;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.thenextlvl.hologram.api.hologram.Hologram;
import net.thenextlvl.hologram.api.HologramRegistry;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class CraftHologramRegistry implements HologramRegistry {
    private final Collection<Hologram> holograms = new ArrayList<>();

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
