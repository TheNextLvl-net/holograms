package net.thenextlvl.hologram.v1_20_R1;

import net.thenextlvl.hologram.api.Hologram;
import net.thenextlvl.hologram.api.HologramFactory;
import net.thenextlvl.hologram.api.line.HologramLine;
import net.thenextlvl.hologram.v1_20_R1.line.CraftBlockLine;
import net.thenextlvl.hologram.v1_20_R1.line.CraftHologramLine;
import net.thenextlvl.hologram.v1_20_R1.line.CraftItemLine;
import net.thenextlvl.hologram.v1_20_R1.line.CraftTextLine;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class CraftHologramFactory implements HologramFactory {

    @Override
    @SuppressWarnings("unchecked")
    public Hologram createHologram(Location location, Collection<? extends HologramLine> lines) {
        return new CraftHologram(location.clone(), (Collection<CraftHologramLine>) lines);
    }

    @Override
    public Hologram createHologram(Location location, HologramLine... lines) {
        return createHologram(location, List.of(lines));
    }

    @Override
    public CraftBlockLine createBlockLine(Function<BlockDisplay, Number> function) {
        return new CraftBlockLine(function);
    }

    @Override
    public CraftBlockLine createBlockLine(Consumer<BlockDisplay> consumer) {
        return createBlockLine(display -> {
            consumer.accept(display);
            return 1;
        });
    }

    @Override
    public CraftItemLine createItemLine(Function<ItemDisplay, Number> function) {
        return new CraftItemLine(function);
    }

    @Override
    public CraftItemLine createItemLine(Consumer<ItemDisplay> consumer) {
        return createItemLine(display -> {
            consumer.accept(display);
            return 0.6;
        });
    }

    @Override
    public CraftTextLine createTextLine(Function<TextDisplay, Number> function) {
        return new CraftTextLine(function);
    }

    @Override
    public CraftTextLine createTextLine(Consumer<TextDisplay> consumer) {
        return createTextLine(display -> {
            consumer.accept(display);
            return 0.27;
        });
    }
}
