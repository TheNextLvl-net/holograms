package net.thenextlvl.hologram.action;

import net.thenextlvl.hologram.HologramLike;
import org.jspecify.annotations.Nullable;

/**
 * Represents a hologram-line page change.
 * <p>
 * If the hologram is {@code null}, the page change will be applied to the hologram that this action is invoked on.
 * <p>
 * If the line is {@code null}, the page change will be applied to the line that this action is invoked on.
 *
 * @param hologram hologram to change the page of
 * @param line     line index of the paged line
 * @param page     new page index
 * @since 0.12.0
 */
public record PageChange(
        @Nullable HologramLike hologram,
        @Nullable Integer line,
        int page
) {
    /**
     * Creates a new page change action that applies to the hologram and line this action is invoked on.
     *
     * @param page new page index
     * @since 1.0.0
     */
    public PageChange(final int page) {
        this(null, null, page);
    }
}
