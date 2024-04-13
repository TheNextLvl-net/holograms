package net.thenextlvl.hologram.v1_20_R3;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.thenextlvl.hologram.api.hologram.Hologram;
import net.thenextlvl.hologram.api.HologramRegistry;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
