package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.DialogBase;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.button.CallbackButton;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.StaticHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

@NullMarked
final class VisualOptionsDialog {
    private VisualOptionsDialog() {
    }

    static Dialog<?> create(
            final Component header,
            @Nullable final Component note,
            final List<Button<?>> actions,
            final Button<?> back
    ) {
        final var dialog = Dialog.multiAction()
                .closeAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)
                .title(Component.text("Visual Options"))
                .columns(1)
                .addBody(Body.text(header))
                .addBody(Body.text(Component.text("Edit how this line looks")));
        if (note != null) dialog.addBody(Body.text(note));
        actions.forEach(dialog::addButton);
        // todo: save button
        dialog.addButton(saveButton(line, reopen));
        dialog.addButton(back);
        // todo: add all default inputs here
        dialog.addInput(DialogSupport.visualGlowButton(hologram, page, audience -> EditTextPageVisualsDialog.create(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        return dialog;
    }

    private static CallbackButton saveButton(final HologramLine line, final Function<Audience, Dialog<?>> reopen) {
        return Button.callback((response, audience) -> {
            final var scale = response.getFloat("scale");
            DialogSupport.show(audience, reopen);
        }, Component.text("Apply", NamedTextColor.GREEN)).uses(1);
    }

    record StaticVisualOption(StaticHologramLine line, ) {
        
    }
}
