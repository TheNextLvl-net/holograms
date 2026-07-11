package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class SelectPageToSwapDialog {
    private SelectPageToSwapDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final Audience viewer
    ) {
        return SelectPageDialog.create(hologram, lineIndex, line, viewer, "Select first page",
                "Select the first page. It will trade places with the second page you choose.", -1,
                audience -> ChangePageOrderDialog.create(hologram, lineIndex, line, audience),
                pageIndex -> audience -> {
                    DialogSupport.show(audience, current -> SelectPageSwapTargetDialog.create(hologram, lineIndex, line, pageIndex, current));
                });
    }
}
