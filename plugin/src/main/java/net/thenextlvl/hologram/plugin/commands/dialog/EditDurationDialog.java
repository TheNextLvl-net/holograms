package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NullMarked
final class EditDurationDialog {
    private EditDurationDialog() {
    }

    static DialogLike create(
            final String label,
            final Duration current,
            @Nullable final Component note,
            final BiConsumer<Audience, Duration> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var parsed = DialogSupport.parseDuration(response.getText("value"), 0);
                    if (parsed.error() != null) {
                        DialogSupport.show(audience, ignored -> EditDurationDialog.create(label, current, Component.text(parsed.error(), NamedTextColor.RED), setter, reopen));
                        return;
                    }
                    setter.accept(audience, parsed.value());
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, reopen))))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter a duration using ms, s, m, or h")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(label))
                        .body(body)
                        .inputs(List.of(DialogInput.text("value", Component.text(label)).initial(DialogSupport.formatIntervalInput(current)).build()))
                        .build())
                .type(DialogType.multiAction(List.of(save)).exitAction(back).build()));
    }
}
