package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

@NullMarked
final class EditPageSettingsDialog {
    private EditPageSettingsDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(final Hologram hologram, final int lineIndex, final PagedHologramLine line) {
        return EditPageSettingsDialog.create(hologram, lineIndex, line, null, null);
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            @Nullable final Component note,
            @Nullable final String intervalInput
    ) {
        final var save = Button.callback((response, audience) -> {
            final var input = response.getText("interval");
            final var interval = DialogSupport.parseDuration(input);
            if (interval.error() != null) {
                DialogSupport.show(audience, ignored -> EditPageSettingsDialog.create(hologram, lineIndex, line,
                        Component.text(interval.error(), NamedTextColor.RED), input));
                return;
            }

            line.setInterval(interval.value());
            final var random = response.getBoolean("random");
            if (random != null) line.setRandomOrder(random);
            final var paused = response.getBoolean("paused");
            if (paused != null) line.setPaused(paused);
            DialogSupport.show(audience, ignored -> EditPageSettingsDialog.create(hologram, lineIndex, line, null, null));
        }, Component.text("Save", NamedTextColor.GREEN)).uses(1);

        final var back = BackButton.create(current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));

        final var body = new ArrayList<DialogBody>();
        body.add(Body.text(Component.text("Current interval: " + DialogSupport.formatIntervalInput(line.getInterval()))));
        body.add(Body.text(Component.text("Allowed units are millis (ms), seconds (s), minutes (m), and hours (h)")));
        body.add(Body.text(Component.text("Numbers without a unit are seconds")));
        if (note != null) body.add(Body.text(note));

        final var dialog = Dialog.confirmation(back, save).title(Component.text("Page Settings"));
        body.forEach(dialog::addBody);
        return dialog;
    }
}
