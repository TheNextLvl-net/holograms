package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NullMarked
final class EditIntValueDialog {
    private EditIntValueDialog() {
    }

    static Dialog<?> create(
            final String label,
            final int current,
            final int min,
            final int max,
            @Nullable final Component note,
            final BiConsumer<Audience, Integer> setter,
            final Function<Audience, Dialog<?>> reopen
    ) {
        final var save = Button.callback((response, audience) -> {
            final var parsed = DialogSupport.parseDouble(label, response.getText("value"), min, max);
            if (parsed.error() != null) {
                DialogSupport.show(audience, ignored -> EditIntValueDialog.create(label, current, min, max, Component.text(parsed.error(), NamedTextColor.RED), setter, reopen));
                return;
            }
            setter.accept(audience, parsed.value().intValue());
        }, Component.text("Save", NamedTextColor.GREEN)).uses(1);
        final var back = BackButton.create(reopen);
        final var dialog = Dialog.multiAction()
                .title(Component.text(label))
                .addBody(Body.text(Component.text("Enter the new " + label.toLowerCase(Locale.ROOT))));
        if (note != null) dialog.addBody(Body.text(note));
        return dialog
                .addInput(Input.text("value", Component.text(label)))
                .exitAction(back)
                .addButton(save);
    }
}
