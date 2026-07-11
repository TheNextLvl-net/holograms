package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;

@NullMarked
final class EditPagedLineDialog {
    private EditPagedLineDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final Audience viewer
    ) {
        final var pages = line.getPages().toList();
        final var actions = new ArrayList<Button<?>>();
        for (var index = 0; index < pages.size(); index++) {
            final var page = pages.get(index);
            final var pageIndex = index;
            actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
                        DialogSupport.show(audience, current -> EditPageDialog.create(hologram, lineIndex, line, pageIndex, current));
                    }), Component.text("Page " + (pageIndex + 1) + ": " + DialogSupport.lineDescription(page)))
                    .tooltip(DialogSupport.linePreview(page, viewer))
                    .width(300));
        }
        actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, ignored -> AddPageTypeDialog.create(hologram, lineIndex, line));
        }), Component.text("Add Page", NamedTextColor.GREEN)).width(300));
        if (pages.size() > 1)
            actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
                DialogSupport.show(audience, current -> ChangePageOrderDialog.create(hologram, lineIndex, line, current));
            }), Component.text("Change order", NamedTextColor.LIGHT_PURPLE)).width(300));
        actions.add(Button.clickEvent(ClickEvent.callback(audience -> {
            DialogSupport.show(audience, ignored -> EditPageSettingsDialog.create(hologram, lineIndex, line, null, null));
        }), Component.text("Page Settings", NamedTextColor.YELLOW)).width(300));
        actions.add(DialogSupport.clickActionsButton(hologram, line, DialogSupport.lineLabel(lineIndex, line), null,
                audience -> EditPagedLineDialog.create(hologram, lineIndex, line, viewer)));
        actions.add(DialogSupport.deleteLineButton(hologram, lineIndex).width(300));
        actions.add(DialogSupport.editHologramBackButton(hologram));

        final var body = new ArrayList<DialogBody>();
        if (pages.isEmpty()) body.add(Body.text(Component.text("No pages have been added yet")));
        final var dialog = Dialog.multiAction().title(DialogSupport.lineLabel(lineIndex, line));
        body.forEach(dialog::addBody);
        actions.forEach(dialog::addButton);
        dialog.columns(1);
        return dialog;
    }
}
