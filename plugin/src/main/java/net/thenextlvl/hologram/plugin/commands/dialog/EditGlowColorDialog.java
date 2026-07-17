package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.DialogBase;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NullMarked
final class EditGlowColorDialog {
    private EditGlowColorDialog() {
    }

    static Dialog<?> create(
            @Nullable final TextColor current,
            @Nullable final Component note,
            @Nullable final String currentInput,
            final BiConsumer<Audience, @Nullable TextColor> setter,
            final Function<Audience, Dialog<?>> reopen
    ) {
        final var actions = new ArrayList<Button<?>>();
        for (final var color : DialogSupport.namedTextColors()) {
            actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
                setter.accept(audience, color);
            }), Component.text(DialogSupport.describeTextColor(color), color)));
        }

        final var save = Button.callback((response, audience) -> {
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
        }, Component.text("Save", NamedTextColor.GREEN)).uses(1);
        final var reset = Button.clickEvent(ClickEvent.callback(audience -> {
            setter.accept(audience, null);
        }), Component.text("Reset", NamedTextColor.RED));
        actions.add(save);
        actions.add(reset);
        final var dialog = Dialog.multiAction()
                .closeAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)
                .title(Component.text("Glow Color"))
                .addBody(Body.text(Component.text("Pick a named color or enter a hex value")))
                .addBody(Body.text(Component.text("Use Reset to clear the glow color")))
                .addInput(Input.text("hex", Component.text("Hex value"))
                        .initial(currentInput != null ? currentInput : current != null ? "#" + Integer.toHexString(current.value()) : ""))
                .exitAction(BackButton.create(reopen));
        if (note != null) dialog.addBody(Body.text(note));
        actions.forEach(dialog::addButton);
        return dialog;
    }
}
