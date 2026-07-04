package net.thenextlvl.hologram.commands.dialog;

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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NullMarked
final class EditLineWidthDialog {
    private EditLineWidthDialog() {
    }

    static DialogLike create(
            final int current,
            final BiConsumer<Audience, Integer> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var parsed = DialogSupport.parseDouble("Line Width", response.getText("value"), 1, Integer.MAX_VALUE);
                    if (parsed.error() != null) {
                        DialogSupport.show(audience, ignored -> EditLineWidthDialog.create(current, setter, reopen));
                        return;
                    }
                    setter.accept(audience, parsed.value().intValue());
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var reset = ActionButton.builder(Component.text("Reset", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    setter.accept(audience, Integer.MAX_VALUE);
                    DialogSupport.show(audience, reopen);
                })))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, reopen))))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter the line width or use Reset to restore the default")));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Line Width"))
                        .body(body)
                        .inputs(List.of(DialogInput.text("value", Component.text("Line Width"))
                                .initial(current == Integer.MAX_VALUE ? "" : Integer.toString(current))
                                .build()))
                        .build())
                .type(DialogType.multiAction(List.of(save, reset)).exitAction(back).build()));
    }
}
