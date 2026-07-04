package net.thenextlvl.hologram.commands.dialog;

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
import net.thenextlvl.hologram.line.DisplayHologramLine;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@NullMarked
final class EditDisplayScaleDialog {
    private EditDisplayScaleDialog() {
    }

    static DialogLike create(
            final DisplayHologramLine line,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        final var transformation = line.getTransformation();
        final var scale = transformation.getScale();
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var x = DialogSupport.parseDouble("X", response.getText("x"), 0.1d, 100.0d);
                    final var y = DialogSupport.parseDouble("Y", response.getText("y"), 0.1d, 100.0d);
                    final var z = DialogSupport.parseDouble("Z", response.getText("z"), 0.1d, 100.0d);
                    if (x.error() != null || y.error() != null || z.error() != null) {
                        final var error = x.error() != null ? x.error() : y.error() != null ? y.error() : z.error();
                        DialogSupport.show(audience, ignored -> EditDisplayScaleDialog.create(line, Component.text(error, NamedTextColor.RED), reopen));
                        return;
                    }
                    final var updated = new Transformation(
                            transformation.getTranslation(),
                            transformation.getLeftRotation(),
                            new Vector3f(x.value().floatValue(), y.value().floatValue(), z.value().floatValue()),
                            transformation.getRightRotation()
                    );
                    line.setTransformation(updated);
                    DialogSupport.show(audience, reopen);
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, reopen))))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter X, Y, and Z scale values")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Scale"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("x", Component.text("X")).initial(Double.toString(scale.x())).build(),
                                DialogInput.text("y", Component.text("Y")).initial(Double.toString(scale.y())).build(),
                                DialogInput.text("z", Component.text("Z")).initial(Double.toString(scale.z())).build()
                        ))
                        .build())
                .type(DialogType.multiAction(List.of(save)).exitAction(back).build()));
    }
}
