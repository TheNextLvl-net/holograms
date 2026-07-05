package net.thenextlvl.hologram.plugin.commands.dialog;

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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

@NullMarked
final class SelectLineDialog {
    private SelectLineDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final Audience viewer,
            final String title,
            final String description,
            final int excludedIndex,
            final Function<Audience, DialogLike> backDialog,
            final Function<Integer, Consumer<Audience>> selection
    ) {
        return SelectLineDialog.create(hologram, viewer, title, description, excludedIndex, backDialog, null, selection);
    }

    static DialogLike create(
            final Hologram hologram,
            final Audience viewer,
            final String title,
            final String description,
            final int excludedIndex,
            final Function<Audience, DialogLike> backDialog,
            @Nullable final Component note,
            final Function<Integer, Consumer<Audience>> selection
    ) {
        final var lines = hologram.getLines().toList();
        final var actions = new ArrayList<ActionButton>();
        for (var index = 0; index < lines.size(); index++) {
            final var line = lines.get(index);
            final var lineIndex = index;
            actions.add(ActionButton.builder(index == excludedIndex
                            ? DialogSupport.lineLabel(lineIndex, line).color(NamedTextColor.GOLD)
                            : DialogSupport.lineLabel(lineIndex, line))
                    .tooltip(DialogSupport.linePreview(line, viewer))
                    .action(DialogAction.staticAction(ClickEvent.callback(selection.apply(lineIndex)::accept)))
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
