package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.BlockHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
final class EditBlockLineDialog {
    private EditBlockLineDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final BlockHologramLine line,
            @Nullable final Component note
    ) {
        final var held = DialogSupport.useHeldBlockButton((audience, block) -> {
            line.setBlock(block);
            DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
        });
        final var remove = DialogSupport.deleteLineButton(hologram, lineIndex, false);
        final var visual = Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, current -> EditBlockLineVisualsDialog.create(hologram, lineIndex, line, note, current));
        }), Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE));
        final var actionsButton = DialogSupport.clickActionsButton(hologram, line, DialogSupport.lineLabel(lineIndex, line), note,
                audience -> EditBlockLineDialog.create(hologram, lineIndex, line, note));
        final var back = DialogSupport.editHologramBackButton(hologram);
        return BlockSearchDialog.create(DialogSupport.lineLabel(lineIndex, line), line.getBlock().getMaterial().key().asString(), note,
                List.of(held, visual, actionsButton, remove), back, (audience, block) -> {
                    line.setBlock(block);
                    DialogSupport.show(audience, current -> EditHologramDialog.create(hologram, current));
                });
    }
}
