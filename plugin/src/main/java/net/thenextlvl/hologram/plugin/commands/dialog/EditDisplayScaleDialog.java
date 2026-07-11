package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
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

    static net.thenextlvl.dialogs.Dialog<?> create(
            final DisplayHologramLine line,
            @Nullable final Component note,
            final Function<Audience, net.thenextlvl.dialogs.Dialog<?>> reopen
    ) {
        final var transformation = line.getTransformation();
        final var scale = transformation.getScale();
        final var save = Button.callback((response, audience) -> {
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
        }, Component.text("Save", NamedTextColor.GREEN)).uses(1);
        final var back = BackButton.create(reopen);
        final var body = new ArrayList<DialogBody>();
        body.add(Body.text(Component.text("Enter X, Y, and Z scale values")));
        if (note != null) body.add(Body.text(note));
        final var dialog = Dialog.multiAction().title(Component.text("Scale"));
        body.forEach(dialog::addBody);
        List.of(
                Input.text("x", Component.text("X")).initial(Double.toString(scale.x())).build(),
                Input.text("y", Component.text("Y")).initial(Double.toString(scale.y())).build(),
                Input.text("z", Component.text("Z")).initial(Double.toString(scale.z())).build()
        ).forEach(dialog::addInput);
        List.of(save).forEach(dialog::addButton);
        dialog.exitAction(back);
        return dialog;
    }
}
