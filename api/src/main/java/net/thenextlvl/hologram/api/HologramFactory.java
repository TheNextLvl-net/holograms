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
import java.util.function.Function;

public interface HologramFactory {

    Hologram createHologram(Location location, Collection<? extends HologramLine> lines);

    Hologram createHologram(Location location, HologramLine... lines);

    BlockLine createBlockLine(Function<BlockDisplay, Number> function);

    BlockLine createBlockLine(Consumer<BlockDisplay> consumer);

    ItemLine createItemLine(Function<ItemDisplay, Number> function);

    ItemLine createItemLine(Consumer<ItemDisplay> consumer);

    TextLine createTextLine(Function<TextDisplay, Number> function);

    TextLine createTextLine(Consumer<TextDisplay> consumer);
}
