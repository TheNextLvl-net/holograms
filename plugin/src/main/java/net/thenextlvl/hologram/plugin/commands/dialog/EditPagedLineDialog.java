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
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;

@NullMarked
final class EditPagedLineDialog {
    private EditPagedLineDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final Audience viewer
    ) {
        final var pages = line.getPages().toList();
        final var actions = new ArrayList<ActionButton>();
        for (var index = 0; index < pages.size(); index++) {
            final var page = pages.get(index);
            final var pageIndex = index;
            actions.add(ActionButton.builder(Component.text("Page " + (pageIndex + 1) + ": " + DialogSupport.lineDescription(page)))
                    .tooltip(DialogSupport.linePreview(page, viewer))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        DialogSupport.show(audience, current -> EditPageDialog.create(hologram, lineIndex, line, pageIndex, current));
                    }))).width(300).build());
        }
        actions.add(ActionButton.builder(Component.text("Add Page", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> AddPageTypeDialog.create(hologram, lineIndex, line));
                }))).width(300).build());
        if (pages.size() > 1)
            actions.add(ActionButton.builder(Component.text("Change order", NamedTextColor.LIGHT_PURPLE))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        DialogSupport.show(audience, current -> ChangePageOrderDialog.create(hologram, lineIndex, line, current));
                    }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Page Settings", NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> EditPageSettingsDialog.create(hologram, lineIndex, line, null, null));
                }))).width(300).build());
        actions.add(DialogSupport.clickActionsButton(hologram, line, DialogSupport.lineLabel(lineIndex, line), null,
                audience -> EditPagedLineDialog.create(hologram, lineIndex, line, viewer)));
        actions.add(DialogSupport.deleteLineButton(hologram, lineIndex));
        actions.add(DialogSupport.editHologramBackButton(hologram));

        final var body = new ArrayList<DialogBody>();
        if (pages.isEmpty()) body.add(DialogBody.plainMessage(Component.text("No pages have been added yet")));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(DialogSupport.lineLabel(lineIndex, line)).body(body).build())
                .type(DialogType.multiAction(actions).columns(1).build()));
    }
}
