package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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

    static DialogLike create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<PageChange> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        final var current = action.getInput();
        final var label = action.getActionType() == DialogSupport.ACTION_TYPES.setPage() ? "Page" : "Amount";
        final var initial = action.getActionType() == DialogSupport.ACTION_TYPES.setPage() ? Integer.toString(current.page() + 1) : Integer.toString(current.page());
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
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
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen)))))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Edit the page value")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(DialogSupport.friendlyName(action.getActionType().name())))
                        .body(body)
                        .inputs(List.of(DialogInput.text("value", Component.text(label)).initial(initial).build()))
                        .build())
                .type(DialogType.multiAction(List.of(save)).exitAction(back).build()));
    }
}
