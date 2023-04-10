package net.thenextlvl.hologram;

import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.CraftBlockLine;
import net.thenextlvl.hologram.line.CraftItemLine;
import net.thenextlvl.hologram.line.CraftTextLine;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;

import java.util.Collection;
import java.util.function.Consumer;

public class CraftHologramFactory implements HologramFactory {

    @Override
    public Hologram createHologram(Location location, Collection<HologramLine> lines) {
        return new CraftHologram(location, lines);
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
