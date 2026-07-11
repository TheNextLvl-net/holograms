package net.thenextlvl.hologram.plugin.commands.dialog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import net.thenextlvl.hologram.line.DisplayHologramLine;
import org.bukkit.entity.Display;
import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

@NullMarked
final class EditBrightnessDialog {
    private EditBrightnessDialog() {
    }

    static Dialog<?> create(
            final DisplayHologramLine line,
            final Function<Audience, Dialog<?>> reopen
    ) {
        final var current = line.getBrightness().orElse(null);
        final var save = Button.callback((response, audience) -> {
            final var blockLight = DialogSupport.parseDouble("Block light", response.getText("block"), 0, 15);
            final var skyLight = DialogSupport.parseDouble("Sky light", response.getText("sky"), 0, 15);
            if (blockLight.error() != null || skyLight.error() != null) {
                final var message = blockLight.error() != null ? blockLight.error() : skyLight.error();
                DialogSupport.show(audience, ignored -> EditBrightnessDialog.create(line, reopen));
                return;
            }
            line.setBrightness(new Display.Brightness(blockLight.value().intValue(), skyLight.value().intValue()));
            DialogSupport.show(audience, reopen);
        }, Component.text("Save", NamedTextColor.GREEN)).uses(1);
        final var clear = Button.clickEvent(ClickEvent.callback(audience -> {
            line.setBrightness(null);
            DialogSupport.show(audience, reopen);
        }), Component.text("Reset"));
        return Dialog.multiAction()
                .title(Component.text("Brightness"))
                .addBody(Body.text(Component.text("Set block light and sky light, or reset to none")))
                .addInput(Input.text("block", Component.text("Block light")).initial(current != null ? Integer.toString(current.getBlockLight()) : "0"))
                .addInput(Input.text("sky", Component.text("Sky light")).initial(current != null ? Integer.toString(current.getSkyLight()) : "0"))
                .addButton(save)
                .addButton(clear)
                .exitAction(BackButton.create(reopen));
    }
}
