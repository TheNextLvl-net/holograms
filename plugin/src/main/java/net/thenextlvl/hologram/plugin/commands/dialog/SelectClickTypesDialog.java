package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
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

    static Dialog<?> create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<?> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, Dialog<?>> reopen
    ) {
        final var actions = new ArrayList<Button<?>>();
        for (final var preset : ClickTypes.values()) {
            final var current = action.getClickTypes().equals(preset.getClickTypes());
            actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
                action.setClickTypes(EnumSet.copyOf(preset.getClickTypes()));
                DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
            }), Component.text(DialogSupport.friendlyName(preset.name()), current ? NamedTextColor.GREEN : NamedTextColor.WHITE)));
        }

        final var dialog = Dialog.multiAction()
                .title(Component.text("Click Types"))
                .addBody(Body.text(header))
                .addBody(Body.text(Component.text("Choose when this action should trigger")));
        if (note != null) dialog.addBody(Body.text(note));

        actions.forEach(dialog::addButton);
        return dialog.exitAction(BackButton.create(ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen)));
    }
}
