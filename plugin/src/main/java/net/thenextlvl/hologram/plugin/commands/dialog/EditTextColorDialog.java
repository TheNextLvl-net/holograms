package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NullMarked
final class EditTextColorDialog {
    private EditTextColorDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final String label,
            @Nullable final TextColor current,
            @Nullable final Component note,
            final BiConsumer<Audience, @Nullable TextColor> setter,
            final Function<Audience, net.thenextlvl.dialogs.Dialog<?>> reopen
    ) {
        final var save = Button.callback((response, audience) -> {
            final var input = response.getText("value");
            final var parsed = DialogSupport.parseTextColor(input);
            if (parsed.error() != null) {
                DialogSupport.show(audience, ignored -> EditTextColorDialog.create(label, current, Component.text(parsed.error(), NamedTextColor.RED), setter, reopen));
                return;
            }
            setter.accept(audience, parsed.value());
        }, Component.text("Save", NamedTextColor.GREEN)).uses(1);
        final var back = BackButton.create(reopen);
        final var body = new ArrayList<DialogBody>();
        body.add(Body.text(Component.text("Enter a named color or hex value; blank resets it")));
        if (note != null) body.add(Body.text(note));
        final var dialog = Dialog.multiAction().title(Component.text(label));
        body.forEach(dialog::addBody);
        List.of(Input.text("value", Component.text(label)).initial(current != null ? "#" + Integer.toHexString(current.value()) : "").build()).forEach(dialog::addInput);
        List.of(save).forEach(dialog::addButton);
        dialog.exitAction(back);
        return dialog;
    }
}
