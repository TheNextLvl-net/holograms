package net.thenextlvl.hologram.action;

import net.thenextlvl.hologram.HologramLike;

/**
 * Represents a hologram-line page change.
 *
 * @param hologram hologram to change the page of
 * @param line     line index of the paged line
 * @param page     new page index
 * @since 0.12.0
 */
public record PageChange(
        HologramLike hologram,
        int line,
        int page
) {
}
