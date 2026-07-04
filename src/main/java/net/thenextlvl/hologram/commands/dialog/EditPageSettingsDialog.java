package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
final class EditPageSettingsDialog {
    private EditPageSettingsDialog() {
    }

    static DialogLike create(final Hologram hologram, final int lineIndex, final PagedHologramLine line) {
        return EditPageSettingsDialog.create(hologram, lineIndex, line, null, null);
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            @Nullable final Component note,
            @Nullable final String intervalInput
    ) {
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
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
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();

        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditPagedLineDialog.create(hologram, lineIndex, line, current));
                }))).build();

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Current interval: " + DialogSupport.formatIntervalInput(line.getInterval()))));
        body.add(DialogBody.plainMessage(Component.text("Allowed units are millis (ms), seconds (s), minutes (m), and hours (h)")));
        body.add(DialogBody.plainMessage(Component.text("Numbers without a unit are seconds")));
        if (note != null) body.add(DialogBody.plainMessage(note));

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Page Settings"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("interval", Component.text("Time between cycles"))
                                        .initial(intervalInput != null ? intervalInput : DialogSupport.formatIntervalInput(line.getInterval()))
                                        .build(),
                                DialogInput.bool("random", Component.text("Random Order"))
                                        .initial(line.isRandomOrder())
                                        .build(),
                                DialogInput.bool("paused", Component.text("Paused"))
                                        .initial(line.isPaused())
                                        .build()
                        ))
                        .build())
                .type(DialogType.confirmation(save, back)));
    }
}
