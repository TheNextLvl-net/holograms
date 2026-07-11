package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import org.bukkit.Color;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NullMarked
final class EditBackgroundColorDialog {
    private EditBackgroundColorDialog() {
    }

    static Dialog<?> create(
            final String label,
            @Nullable final Color current,
            @Nullable final Component note,
            @Nullable final String currentInput,
            final BiConsumer<Audience, @Nullable Color> setter,
            final Function<Audience, Dialog<?>> reopen
    ) {
        final var actions = new ArrayList<Button<?>>();
        for (final var color : DialogSupport.namedTextColors()) {
            actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
                setter.accept(audience, Color.fromRGB(color.value()));
            }), Component.text(DialogSupport.describeTextColor(color), color)));
        }

        final var save = Button.callback((response, audience) -> {
            final var input = response.getText("value");
            final var parsed = DialogSupport.parseBackgroundColor(input);
            if (parsed.error() != null) {
                DialogSupport.show(audience, ignored -> EditBackgroundColorDialog.create(label, current, Component.text(parsed.error(), NamedTextColor.RED), input, setter, reopen));
                return;
            }
            setter.accept(audience, parsed.value());
        }, Component.text("Save", NamedTextColor.GREEN)).uses(1);
        final var reset = Button.clickEvent(ClickEvent.callback(audience -> {
            setter.accept(audience, null);
            DialogSupport.show(audience, reopen);
        }), Component.text("Reset", NamedTextColor.RED));
        actions.add(save);
        actions.add(reset);
        final var dialog = Dialog.multiAction()
                .title(Component.text(label))
                .addBody(Body.text(Component.text("Pick a named color or enter a hex value")))
                .addBody(Body.text(Component.text("Use Reset to clear the background color")))
                .addInput(Input.text("value", Component.text(label))
                        .initial(currentInput != null ? currentInput : current != null ? "#" + Integer.toHexString(current.asARGB()) : ""))
                .exitAction(BackButton.create(reopen));
        if (note != null) dialog.addBody(Body.text(note));
        actions.forEach(dialog::addButton);
        return dialog;
    }
}
