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
import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NullMarked
final class EditGlowColorDialog {
    private EditGlowColorDialog() {
    }

    static DialogLike create(
            @Nullable final TextColor current,
            @Nullable final Component note,
            @Nullable final String currentInput,
            final BiConsumer<Audience, @Nullable TextColor> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var actions = new ArrayList<ActionButton>();
        for (final var color : DialogSupport.namedTextColors()) {
            actions.add(ActionButton.builder(Component.text(DialogSupport.describeTextColor(color), color))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        setter.accept(audience, color);
                    })))
                    .build());
        }

        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("hex");
                    if (input == null || input.isBlank()) {
                        DialogSupport.show(audience, ignored -> EditGlowColorDialog.create(current, Component.text("Hex value cannot be empty", NamedTextColor.RED), input, setter, reopen));
                        return;
                    }
                    final var parsed = DialogSupport.parseTextColor(input);
                    if (parsed.error() != null || parsed.value() == null) {
                        DialogSupport.show(audience, ignored -> EditGlowColorDialog.create(current, Component.text("Invalid color", NamedTextColor.RED), input, setter, reopen));
                        return;
                    }
                    setter.accept(audience, parsed.value());
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var reset = ActionButton.builder(Component.text("Reset", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    setter.accept(audience, null);
                })))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, reopen))))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Pick a named color or enter a hex value")));
        body.add(DialogBody.plainMessage(Component.text("Use Reset to clear the glow color")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        actions.add(save);
        actions.add(reset);
        actions.add(back);
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Glow Color"))
                        .body(body)
                        .inputs(List.of(DialogInput.text("hex", Component.text("Hex value"))
                                .initial(currentInput != null ? currentInput : current != null ? "#" + Integer.toHexString(current.value()) : "")
                                .build()))
                        .build())
                .type(DialogType.multiAction(actions).build()));
    }
}
