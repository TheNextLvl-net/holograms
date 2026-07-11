package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import net.thenextlvl.hologram.line.StaticHologramLine;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Function;

@NullMarked
final class EditOffsetDialog {
    private EditOffsetDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final StaticHologramLine line,
            final Function<Audience, net.thenextlvl.dialogs.Dialog<?>> reopen
    ) {
        final var current = line.getOffset();
        final var save = Button.callback((response, audience) -> {
            final var x = DialogSupport.parseDouble("X", response.getText("x"), -1000, 1000);
            final var y = DialogSupport.parseDouble("Y", response.getText("y"), -1000, 1000);
            final var z = DialogSupport.parseDouble("Z", response.getText("z"), -1000, 1000);
            if (x.error() != null || y.error() != null || z.error() != null) {
                DialogSupport.show(audience, ignored -> EditOffsetDialog.create(line, reopen));
                return;
            }
            line.setOffset(new Vector3f(x.value().floatValue(), y.value().floatValue(), z.value().floatValue()));
            DialogSupport.show(audience, reopen);
        }, Component.text("Save", NamedTextColor.GREEN)).uses(1);
        final var back = BackButton.create(reopen);
        final var dialog = Dialog.multiAction().title(Component.text("Offset"));
        List.of(Body.text(Component.text("Set the entity offset"))).forEach(dialog::addBody);
        List.of(
                Input.text("x", Component.text("X")).initial(Float.toString(current.x())).build(),
                Input.text("y", Component.text("Y")).initial(Float.toString(current.y())).build(),
                Input.text("z", Component.text("Z")).initial(Float.toString(current.z())).build()
        ).forEach(dialog::addInput);
        List.of(save).forEach(dialog::addButton);
        dialog.exitAction(back);
        return dialog;
    }
}
