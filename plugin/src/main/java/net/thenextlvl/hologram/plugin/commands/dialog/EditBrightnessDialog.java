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
import net.thenextlvl.hologram.line.DisplayHologramLine;
import org.bukkit.entity.Display;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Function;

@NullMarked
final class EditBrightnessDialog {
    private EditBrightnessDialog() {
    }

    static DialogLike create(
            final DisplayHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        final var current = line.getBrightness().orElse(null);
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var blockLight = DialogSupport.parseDouble("Block light", response.getText("block"), 0, 15);
                    final var skyLight = DialogSupport.parseDouble("Sky light", response.getText("sky"), 0, 15);
                    if (blockLight.error() != null || skyLight.error() != null) {
                        final var message = blockLight.error() != null ? blockLight.error() : skyLight.error();
                        DialogSupport.show(audience, ignored -> EditBrightnessDialog.create(line, reopen));
                        return;
                    }
                    line.setBrightness(new Display.Brightness(blockLight.value().intValue(), skyLight.value().intValue()));
                    DialogSupport.show(audience, reopen);
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var clear = ActionButton.builder(Component.text("Reset"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    line.setBrightness(null);
                    DialogSupport.show(audience, reopen);
                })))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, reopen))))
                .build();
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Brightness"))
                        .body(List.of(DialogBody.plainMessage(Component.text("Set block light and sky light, or reset to none"))))
                        .inputs(List.of(
                                DialogInput.text("block", Component.text("Block light")).initial(current != null ? Integer.toString(current.getBlockLight()) : "0").build(),
                                DialogInput.text("sky", Component.text("Sky light")).initial(current != null ? Integer.toString(current.getSkyLight()) : "0").build()
                        )).build())
                .type(DialogType.multiAction(List.of(save, clear)).exitAction(back).build()));
    }
}
