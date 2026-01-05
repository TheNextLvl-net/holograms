package net.thenextlvl.hologram.adapters.deserializers;

import net.thenextlvl.hologram.line.DisplayHologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
abstract class DisplayHologramLineDeserializer<T extends DisplayHologramLine<T, ?>> extends HologramLineDeserializer<T> {
}
