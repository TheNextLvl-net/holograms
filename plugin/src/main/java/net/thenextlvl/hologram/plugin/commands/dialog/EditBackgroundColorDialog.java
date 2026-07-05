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
import org.bukkit.Color;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NullMarked
final class EditBackgroundColorDialog {
    private EditBackgroundColorDialog() {
    }

    static DialogLike create(
            final String label,
            @Nullable final Color current,
            @Nullable final Component note,
            @Nullable final String currentInput,
            final BiConsumer<Audience, @Nullable Color> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var actions = new ArrayList<ActionButton>();
        for (final var color : DialogSupport.namedTextColors()) {
            actions.add(ActionButton.builder(Component.text(DialogSupport.describeTextColor(color), color))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        setter.accept(audience, Color.fromRGB(color.value()));
                    })))
                    .build());
        }

        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("value");
                    final var parsed = DialogSupport.parseBackgroundColor(input);
                    if (parsed.error() != null) {
                        DialogSupport.show(audience, ignored -> EditBackgroundColorDialog.create(label, current, Component.text(parsed.error(), NamedTextColor.RED), input, setter, reopen));
                        return;
                    }
                    setter.accept(audience, parsed.value());
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, reopen))))
                .build();
        final var reset = ActionButton.builder(Component.text("Reset", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    setter.accept(audience, null);
                    DialogSupport.show(audience, reopen);
                })))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Pick a named color or enter a hex value")));
        body.add(DialogBody.plainMessage(Component.text("Use Reset to clear the background color")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        actions.add(save);
        actions.add(reset);
        actions.add(back);
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(label))
                        .body(body)
                        .inputs(List.of(DialogInput.text("value", Component.text(label))
                                .initial(currentInput != null ? currentInput : current != null ? "#" + Integer.toHexString(current.asARGB()) : "")
                                .build()))
                        .build())
                .type(DialogType.multiAction(actions).build()));
    }
}
