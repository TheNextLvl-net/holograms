package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;

@NullMarked
final class SelectPageDialog {
    private SelectPageDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final Audience viewer,
            final String title,
            final String description,
            final int excludedIndex,
            final Function<Audience, DialogLike> backDialog,
            final Function<Integer, java.util.function.Consumer<Audience>> selection
    ) {
        return SelectPageDialog.create(hologram, lineIndex, line, viewer, title, description, excludedIndex, backDialog, null, selection);
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final Audience viewer,
            final String title,
            final String description,
            final int excludedIndex,
            final Function<Audience, DialogLike> backDialog,
            @Nullable final Component note,
            final Function<Integer, java.util.function.Consumer<Audience>> selection
    ) {
        final var pages = line.getPages().toList();
        final var actions = new ArrayList<ActionButton>();
        for (var index = 0; index < pages.size(); index++) {
            final var page = pages.get(index);
            final var pageIndex = index;
            actions.add(ActionButton.builder(index == excludedIndex
                            ? Component.text("Page " + (pageIndex + 1) + ": " + DialogSupport.lineDescription(page), NamedTextColor.GOLD)
                            : Component.text("Page " + (pageIndex + 1) + ": " + DialogSupport.lineDescription(page)))
                    .tooltip(DialogSupport.linePreview(page, viewer))
                    .action(DialogAction.staticAction(ClickEvent.callback(selection.apply(pageIndex)::accept)))
                    .width(300)
                    .build());
        }
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, backDialog);
                }))).width(300).build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(title))
                        .body(DialogSupport.bodyLines(description, note))
                        .build())
                .type(DialogType.multiAction(actions).columns(1).exitAction(back).build()));
    }
}
