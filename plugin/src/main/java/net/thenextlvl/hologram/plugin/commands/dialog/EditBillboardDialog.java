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
import net.thenextlvl.hologram.line.StaticHologramLine;
import org.bukkit.entity.Display;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@NullMarked
final class EditBillboardDialog {
    private EditBillboardDialog() {
    }

    static DialogLike create(
            final StaticHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        final var actions = new ArrayList<ActionButton>();
        for (final var billboard : Display.Billboard.values()) {
            actions.add(ActionButton.builder(Component.text(DialogSupport.friendlyName(billboard.name())))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        line.setBillboard(billboard);
                        DialogSupport.show(audience, reopen);
                    }))).build());
        }
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, reopen))))
                .build();
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Billboard"))
                        .body(List.of(DialogBody.plainMessage(Component.text("Choose the billboard mode"))))
                        .build())
                .type(DialogType.multiAction(actions).exitAction(back).build()));
    }
}
