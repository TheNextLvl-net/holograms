package net.thenextlvl.hologram.api.line;

/**
 * An interface that represents a hologram line of the type {@link LineType#TEXT text}
 */
public interface TextLine extends HologramLine {
    @Override
    default LineType getType() {
        return LineType.TEXT;
    }
}
