package net.thenextlvl.hologram.commands.edit;

import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.HologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
