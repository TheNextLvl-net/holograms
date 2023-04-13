package net.thenextlvl.hologram.api.line;

/**
 * An interface that represents a hologram line of the type {@link LineType#ITEM item}
 */
public interface ItemLine extends HologramLine {
    @Override
    default LineType getType() {
        return LineType.ITEM;
    }
}
