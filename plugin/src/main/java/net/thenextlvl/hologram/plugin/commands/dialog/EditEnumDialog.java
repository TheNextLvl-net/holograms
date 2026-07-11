package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

@NullMarked
final class EditEnumDialog {
    private EditEnumDialog() {
    }

    static <E extends Enum<E>> Dialog<?> create(
            final String title,
            final E current,
            final Class<E> type,
            final Consumer<E> setter,
            final Function<Audience, Dialog<?>> reopen
    ) {
        final var actions = new ArrayList<Button<?>>();
        for (final var value : type.getEnumConstants()) {
            actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
                setter.accept(value);
                DialogSupport.show(audience, reopen);
            }), Component.text(DialogSupport.friendlyName(value.name()),
                    value == current ? NamedTextColor.GREEN : NamedTextColor.WHITE)));
        }
        final var dialog = Dialog.multiAction()
                .title(Component.text(title))
                .addBody(Body.text(Component.text("Choose a value")))
                .exitAction(BackButton.create(reopen));
        actions.forEach(dialog::addButton);
        return dialog;
    }
}
