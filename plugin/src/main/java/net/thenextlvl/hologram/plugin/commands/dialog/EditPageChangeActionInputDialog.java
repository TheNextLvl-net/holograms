package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.action.PageChange;
import net.thenextlvl.hologram.line.HologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@NullMarked
final class EditPageChangeActionInputDialog {
    private EditPageChangeActionInputDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<PageChange> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, net.thenextlvl.dialogs.Dialog<?>> reopen
    ) {
        final var current = action.getInput();
        final var label = action.getActionType() == DialogSupport.ACTION_TYPES.setPage() ? "Page" : "Amount";
        final var initial = action.getActionType() == DialogSupport.ACTION_TYPES.setPage() ? Integer.toString(current.page() + 1) : Integer.toString(current.page());
        final var save = Button.callback((response, audience) -> {
            final var input = response.getText("value");
            if (input == null || input.isBlank()) {
                DialogSupport.show(audience, ignored -> EditPageChangeActionInputDialog.create(hologram, line, actionName, action, header, Component.text("Value cannot be empty", NamedTextColor.RED), reopen));
                return;
            }
            final int value;
            try {
                value = Integer.parseInt(input.trim());
            } catch (final NumberFormatException ignored) {
                DialogSupport.show(audience, ignored2 -> EditPageChangeActionInputDialog.create(hologram, line, actionName, action, header, Component.text("Value must be a number", NamedTextColor.RED), reopen));
                return;
            }
            if (action.getActionType() == DialogSupport.ACTION_TYPES.setPage()) {
                if (value < 1) {
                    DialogSupport.show(audience, ignored -> EditPageChangeActionInputDialog.create(hologram, line, actionName, action, header, Component.text("Page must be at least 1", NamedTextColor.RED), reopen));
                    return;
                }
                action.setInput(new PageChange(current.hologram(), current.line(), value - 1));
            } else {
                action.setInput(new PageChange(current.hologram(), current.line(), value));
            }
            DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
        }, Component.text("Save", NamedTextColor.GREEN)).uses(1);
        final var back = BackButton.create(ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
        final var body = new ArrayList<DialogBody>();
        body.add(Body.text(Component.text("Edit the page value")));
        if (note != null) body.add(Body.text(note));
        final var dialog = Dialog.multiAction().title(Component.text(DialogSupport.friendlyName(action.getActionType().name())));
        body.forEach(dialog::addBody);
        List.of(Input.text("value", Component.text(label)).initial(initial).build()).forEach(dialog::addInput);
        List.of(save).forEach(dialog::addButton);
        dialog.exitAction(back);
        return dialog;
    }
}
