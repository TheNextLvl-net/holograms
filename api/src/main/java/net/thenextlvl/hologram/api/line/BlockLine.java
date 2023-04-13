package net.thenextlvl.hologram.api.line;

/**
 * An interface that represents a hologram line of the type {@link LineType#BLOCK block}
 */
public interface BlockLine extends HologramLine {
    @Override
    default LineType getType() {
        return LineType.BLOCK;
    }
}
