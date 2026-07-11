package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

@NullMarked
final class SelectPageDialog {
    private SelectPageDialog() {
    }

    static Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final Audience viewer,
            final String title,
            final String description,
            final int excludedIndex,
            final Function<Audience, Dialog<?>> backDialog,
            final Function<Integer, Consumer<Audience>> selection
    ) {
        return SelectPageDialog.create(hologram, lineIndex, line, viewer, title, description, excludedIndex, backDialog, null, selection);
    }

    static Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final Audience viewer,
            final String title,
            final String description,
            final int excludedIndex,
            final Function<Audience, Dialog<?>> backDialog,
            @Nullable final Component note,
            final Function<Integer, Consumer<Audience>> selection
    ) {
        final var pages = line.getPages().toList();
        final var actions = new ArrayList<Button<?>>();
        for (var index = 0; index < pages.size(); index++) {
            final var page = pages.get(index);
            final var pageIndex = index;
            actions.add(Button.clickEvent(ClickEvent.callback(selection.apply(pageIndex)::accept), index == excludedIndex
                            ? Component.text("Page " + (pageIndex + 1) + ": " + DialogSupport.lineDescription(page), NamedTextColor.GOLD)
                            : Component.text("Page " + (pageIndex + 1) + ": " + DialogSupport.lineDescription(page)))
                    .tooltip(DialogSupport.linePreview(page, viewer))
                    .width(300));
        }
        final var back = BackButton.create(300, backDialog);

        final var dialog = Dialog.multiAction().title(Component.text(title)).columns(1).exitAction(back);
        DialogSupport.bodyLines(description, note).forEach(dialog::addBody);
        actions.forEach(dialog::addButton);
        return dialog;
    }
}
