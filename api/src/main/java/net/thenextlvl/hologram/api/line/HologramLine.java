package net.thenextlvl.hologram.api.line;

/**
 * An interface that represents a single hologram line
 */
public interface HologramLine {
    /**
     * Get the type of the hologram line
     * @return the {@link LineType} enum
     */
    LineType getType();
}
