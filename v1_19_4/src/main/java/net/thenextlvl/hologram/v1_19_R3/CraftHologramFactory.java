package net.thenextlvl.hologram.v1_19_R3;

import net.thenextlvl.hologram.api.Hologram;
import net.thenextlvl.hologram.api.HologramFactory;
import net.thenextlvl.hologram.api.line.HologramLine;
import net.thenextlvl.hologram.v1_19_R3.line.CraftBlockLine;
import net.thenextlvl.hologram.v1_19_R3.line.CraftHologramLine;
import net.thenextlvl.hologram.v1_19_R3.line.CraftItemLine;
import net.thenextlvl.hologram.v1_19_R3.line.CraftTextLine;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;

import java.util.Collection;
import java.util.function.Consumer;

public class CraftHologramFactory implements HologramFactory {

    @Override
    @SuppressWarnings("unchecked")
    public Hologram createHologram(Location location, Collection<? extends HologramLine> lines) {
        return new CraftHologram(location, (Collection<CraftHologramLine>) lines);
    }

    @Override
    public CraftBlockLine createBlockLine(Consumer<BlockDisplay> consumer) {
        return new CraftBlockLine(consumer);
    }

    @Override
    public CraftItemLine createItemLine(Consumer<ItemDisplay> consumer) {
        return new CraftItemLine(consumer);
    }

    @Override
    public CraftTextLine createTextLine(Consumer<TextDisplay> consumer) {
        return new CraftTextLine(consumer);
    }
}
