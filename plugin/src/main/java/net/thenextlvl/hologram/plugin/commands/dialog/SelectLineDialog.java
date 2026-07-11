package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

@NullMarked
final class SelectLineDialog {
    private SelectLineDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final Audience viewer,
            final String title,
            final String description,
            final int excludedIndex,
            final Function<Audience, Dialog<?>> backDialog,
            final Function<Integer, Consumer<Audience>> selection
    ) {
        return SelectLineDialog.create(hologram, viewer, title, description, excludedIndex, backDialog, null, selection);
    }

    static Dialog<?> create(
            final Hologram hologram,
            final Audience viewer,
            final String title,
            final String description,
            final int excludedIndex,
            final Function<Audience, Dialog<?>> backDialog,
            @Nullable final Component note,
            final Function<Integer, Consumer<Audience>> selection
    ) {
        final var lines = hologram.getLines().toList();
        final var actions = new ArrayList<Button<?>>();
        for (var index = 0; index < lines.size(); index++) {
            final var line = lines.get(index);
            final var lineIndex = index;
            actions.add(Button.clickEvent(ClickEvent.callback(selection.apply(lineIndex)::accept), index == excludedIndex
                            ? DialogSupport.lineLabel(lineIndex, line).color(NamedTextColor.GOLD)
                            : DialogSupport.lineLabel(lineIndex, line))
                    .tooltip(DialogSupport.linePreview(line, viewer))
                    .width(300));
        }
        final var back = BackButton.create(backDialog).width(300);

        final var dialog = Dialog.multiAction().title(Component.text(title)).columns(1).exitAction(back);
        DialogSupport.bodyLines(description, note).forEach(dialog::addBody);
        actions.forEach(dialog::addButton);
        return dialog;
    }
}
