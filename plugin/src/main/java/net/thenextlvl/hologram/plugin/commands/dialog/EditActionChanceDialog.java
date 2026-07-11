package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.line.HologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

@NullMarked
final class EditActionChanceDialog {
    private EditActionChanceDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<?> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, Dialog<?>> reopen
    ) {
        return EditIntValueDialog.create("Chance", action.getChance(), 0, 100, note, (audience, value) -> {
            action.setChance(value);
            DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
        }, audience -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
    }
}
