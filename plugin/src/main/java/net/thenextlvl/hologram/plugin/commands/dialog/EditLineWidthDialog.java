package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiConsumer;
import java.util.function.Function;

@NullMarked
final class EditLineWidthDialog {
    private EditLineWidthDialog() {
    }

    static Dialog<?> create(
            final int current,
            final BiConsumer<Audience, Integer> setter,
            final Function<Audience, Dialog<?>> reopen
    ) {
        final var save = Button.callback((response, audience) -> {
            final var parsed = DialogSupport.parseDouble("Line Width", response.getText("value"), 1, Integer.MAX_VALUE);
            if (parsed.error() != null) {
                DialogSupport.show(audience, ignored -> EditLineWidthDialog.create(current, setter, reopen));
                return;
            }
            setter.accept(audience, parsed.value().intValue());
        }, Component.text("Save", NamedTextColor.GREEN)).uses(1);
        final var reset = Button.clickEvent(ClickEvent.callback(audience -> {
            setter.accept(audience, Integer.MAX_VALUE);
            DialogSupport.show(audience, reopen);
        }), Component.text("Reset", NamedTextColor.RED));
        return Dialog.multiAction()
                .title(Component.text("Line Width"))
                .addBody(Body.text(Component.text("Enter the line width or use Reset to restore the default")))
                .addInput(Input.text("value", Component.text("Line Width")).initial(current == Integer.MAX_VALUE ? "" : Integer.toString(current)))
                .addButton(save)
                .addButton(reset)
                .exitAction(BackButton.create(reopen));
    }
}
