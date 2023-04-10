package net.thenextlvl.hologram.api;

import net.thenextlvl.hologram.api.line.BlockLine;
import net.thenextlvl.hologram.api.line.HologramLine;
import net.thenextlvl.hologram.api.line.ItemLine;
import net.thenextlvl.hologram.api.line.TextLine;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;

import java.util.Collection;
import java.util.function.Consumer;

public interface HologramFactory {

    Hologram createHologram(Location location, Collection<HologramLine> lines);

    BlockLine createBlockLine(Consumer<BlockDisplay> consumer);

    ItemLine createItemLine(Consumer<ItemDisplay> consumer);

    TextLine createTextLine(Consumer<TextDisplay> consumer);
}
