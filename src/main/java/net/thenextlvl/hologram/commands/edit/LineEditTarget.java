package net.thenextlvl.hologram.commands.edit;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.HologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents the target of a line/page edit operation.
 *
 * @param hologram   the hologram containing the line
 * @param lineIndex  the 0-based line index
 * @param pageIndex  the 0-based page index, or null if editing a line directly
 * @param line       the actual line instance to edit (a page is also a HologramLine)
 */
@NullMarked
public record LineEditTarget(
        Hologram hologram,
        int lineIndex,
        @Nullable Integer pageIndex,
        HologramLine line
) {
    public boolean isPage() {
        return pageIndex != null;
    }

    public int displayLineIndex() {
        return lineIndex + 1;
    }

    public int displayPageIndex() {
        return pageIndex != null ? pageIndex + 1 : 0;
    }
}
