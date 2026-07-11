package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.thenextlvl.dialogs.Dialog;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramDialog {
    private HologramDialog() {
    }

    public static void showLast(final Audience audience) {
        DialogSupport.showLast(audience);
    }

    public static Dialog<?> overview() {
        return OverviewDialog.create();
    }
}
