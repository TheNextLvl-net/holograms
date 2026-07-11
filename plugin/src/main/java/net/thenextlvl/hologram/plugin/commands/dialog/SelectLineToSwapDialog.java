package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class SelectLineToSwapDialog {
    private SelectLineToSwapDialog() {
    }

    static Dialog<?> create(final Hologram hologram, final Audience viewer) {
        return SelectLineDialog.create(hologram, viewer, "Select first line",
                "Select the first line. It will trade places with the second line you choose.", -1,
                audience -> ChangeLineOrderDialog.create(hologram, audience),
                lineIndex -> audience -> {
                    DialogSupport.show(audience, current -> SelectLineSwapTargetDialog.create(hologram, lineIndex, current));
                });
    }
}
