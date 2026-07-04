package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@NullMarked
final class EditEnumDialog {
    private EditEnumDialog() {
    }

    static <E extends Enum<E>> DialogLike create(
            final String title,
            final E current,
            final Class<E> type,
            final Consumer<E> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var actions = new ArrayList<ActionButton>();
        for (final var value : type.getEnumConstants()) {
            actions.add(ActionButton.builder(Component.text(DialogSupport.friendlyName(value.name()),
                            value == current ? NamedTextColor.GREEN : NamedTextColor.WHITE))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        setter.accept(value);
                        DialogSupport.show(audience, reopen);
                    })))
                    .build());
        }
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, reopen))))
                .build();
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(title))
                        .body(List.of(DialogBody.plainMessage(Component.text("Choose a value"))))
                        .build())
                .type(DialogType.multiAction(actions).exitAction(back).build()));
    }
}
