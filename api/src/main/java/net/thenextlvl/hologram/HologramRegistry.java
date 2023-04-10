package net.thenextlvl.hologram;

import java.util.Collection;

public interface HologramRegistry {
    Collection<Hologram> getHolograms();

    void register(Hologram hologram);

    void unregister(Hologram hologram);

    boolean isRegistered(Hologram hologram);
}
