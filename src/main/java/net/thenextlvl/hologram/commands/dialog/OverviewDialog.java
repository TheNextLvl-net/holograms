package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramProvider;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Comparator;

@NullMarked
final class OverviewDialog {
    private OverviewDialog() {
    }

    public static DialogLike create() {
        final var holograms = HologramProvider.instance().getHolograms()
                .sorted(Comparator.comparing(Hologram::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        final var builder = DialogBuilder.builder(Component.text("Manage your Holograms"));
        if (holograms.isEmpty()) builder.addBody(Component.text("No holograms have been created yet"));

        final var create = ActionButton.builder(Component.text("New Hologram", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> CreateHologramDialog.create("", null));
                }))).build();

        final var actions = new ArrayList<ActionButton>();
        holograms.stream().map(hologram -> ActionButton.builder(Component.text(hologram.getName()))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, viewer -> EditHologramDialog.create(hologram, viewer));
                }))).build()).forEach(actions::add);
        actions.add(create);

        final var body = new ArrayList<DialogBody>();
        if (holograms.isEmpty()) {
            body.add(DialogBody.plainMessage(Component.text("No holograms have been created yet")));
        }

        return Dialog.create(builder1 -> builder1.empty()
                .base(DialogBase.builder(Component.text("Manage your Holograms"))
                        .body(body)
                        .build())
                .type(DialogType.multiAction(actions).build()));
    }
}
