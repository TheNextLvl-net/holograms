package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NullMarked
final class EditFloatValueDialog {
    private EditFloatValueDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final String label,
            final double current,
            final double min,
            final double max,
            @Nullable final Component note,
            final BiConsumer<Audience, Double> setter,
            final Function<Audience, net.thenextlvl.dialogs.Dialog<?>> reopen
    ) {
        final var save = Button.callback((response, audience) -> {
            final var parsed = DialogSupport.parseDouble(label, response.getText("value"), min, max);
            if (parsed.error() != null) {
                DialogSupport.show(audience, ignored -> EditFloatValueDialog.create(label, current, min, max, Component.text(parsed.error(), NamedTextColor.RED), setter, reopen));
                return;
            }
            setter.accept(audience, parsed.value());
        }, Component.text("Save", NamedTextColor.GREEN)).uses(1);
        final var back = BackButton.create(reopen);
        final var body = new ArrayList<DialogBody>();
        body.add(Body.text(Component.text("Enter the new " + label.toLowerCase(Locale.ROOT))));
        if (note != null) body.add(Body.text(note));
        final var dialog = Dialog.multiAction().title(Component.text(label));
        body.forEach(dialog::addBody);
        List.of(Input.text("value", Component.text(label)).initial(Double.toString(current)).build()).forEach(dialog::addInput);
        List.of(save).forEach(dialog::addButton);
        dialog.exitAction(back);
        return dialog;
    }
}
