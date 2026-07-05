package net.thenextlvl.hologram.plugin.commands.dialog;

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
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.plugin.models.ClickTypes;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.function.Function;

@NullMarked
final class SelectClickTypesDialog {
    private SelectClickTypesDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<?> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        final var actions = new ArrayList<ActionButton>();
        for (final var preset : ClickTypes.values()) {
            final var current = action.getClickTypes().equals(preset.getClickTypes());
            actions.add(ActionButton.builder(Component.text(DialogSupport.friendlyName(preset.name()), current ? NamedTextColor.GREEN : NamedTextColor.WHITE))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        action.setClickTypes(EnumSet.copyOf(preset.getClickTypes()));
                        DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
                    })))
                    .build());
        }

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(header));
        body.add(DialogBody.plainMessage(Component.text("Choose when this action should trigger")));
        if (note != null) body.add(DialogBody.plainMessage(note));

        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen)))))
                .build();
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Click Types"))
                        .body(body)
                        .build())
                .type(DialogType.multiAction(DialogSupport.addBack(actions, back)).build()));
    }
}
