package net.thenextlvl.hologram.api;

import java.util.Collection;

public interface HologramRegistry {
    Collection<? extends Hologram> getHolograms();

    void register(Hologram hologram);

    void unregister(Hologram hologram);

    boolean isRegistered(Hologram hologram);
}