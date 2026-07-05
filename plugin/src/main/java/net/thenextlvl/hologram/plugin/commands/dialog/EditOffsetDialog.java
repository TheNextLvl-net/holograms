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
import net.thenextlvl.hologram.line.StaticHologramLine;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Function;

@NullMarked
final class EditOffsetDialog {
    private EditOffsetDialog() {
    }

    static DialogLike create(
            final StaticHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        final var current = line.getOffset();
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var x = DialogSupport.parseDouble("X", response.getText("x"), -1000, 1000);
                    final var y = DialogSupport.parseDouble("Y", response.getText("y"), -1000, 1000);
                    final var z = DialogSupport.parseDouble("Z", response.getText("z"), -1000, 1000);
                    if (x.error() != null || y.error() != null || z.error() != null) {
                        DialogSupport.show(audience, ignored -> EditOffsetDialog.create(line, reopen));
                        return;
                    }
                    line.setOffset(new Vector3f(x.value().floatValue(), y.value().floatValue(), z.value().floatValue()));
                    DialogSupport.show(audience, reopen);
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, reopen))))
                .build();
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Offset"))
                        .body(List.of(DialogBody.plainMessage(Component.text("Set the entity offset"))))
                        .inputs(List.of(
                                DialogInput.text("x", Component.text("X")).initial(Float.toString(current.x())).build(),
                                DialogInput.text("y", Component.text("Y")).initial(Float.toString(current.y())).build(),
                                DialogInput.text("z", Component.text("Z")).initial(Float.toString(current.z())).build()
                        )).build())
                .type(DialogType.multiAction(List.of(save)).exitAction(back).build()));
    }
}
