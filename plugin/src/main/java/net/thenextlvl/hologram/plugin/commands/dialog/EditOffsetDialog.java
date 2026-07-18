package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.DialogBase;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.button.CallbackButton;
import net.thenextlvl.dialogs.input.Input;
import net.thenextlvl.hologram.line.StaticHologramLine;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

@NullMarked
final class EditOffsetDialog {
    private EditOffsetDialog() {
    }

    static Dialog<?> create(final StaticHologramLine line, final Function<Audience, Dialog<?>> reopen) {
        final var current = line.getOffset();
        return Dialog.confirmation(saveButton(line, reopen), BackButton.create(reopen))
                .closeAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)
                .title(Component.text("Offset"))
                .addBody(Body.text(Component.text("Set the entity offset")))
                .addInput(Input.slider("x", Component.text("X"), -16, 16).initial(current.x()).step(.1F))
                .addInput(Input.slider("y", Component.text("Y"), -16, 16).initial(current.y()).step(.1F))
                .addInput(Input.slider("z", Component.text("Z"), -16, 16).initial(current.z()).step(.1F));
    }

    private static CallbackButton saveButton(final StaticHologramLine line, final Function<Audience, Dialog<?>> reopen) {
        return Button.callback((response, audience) -> {
            final var x = round(response.getFloat("x"));
            final var y = round(response.getFloat("y"));
            final var z = round(response.getFloat("z"));
            line.setOffset(new Vector3f(x, y, z));
            DialogSupport.show(audience, audience1 -> create(line, reopen));
        }, Component.text("Apply", NamedTextColor.GREEN)).uses(1);
    }

    private static float round(@Nullable final Float value) {
        if (value == null) return 0;
        final var scale = Math.pow(10, 3);
        return Math.round(value * scale) / (float) scale;
    }
}
