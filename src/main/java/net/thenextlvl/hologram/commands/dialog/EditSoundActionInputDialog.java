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
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.line.HologramLine;
import org.bukkit.Registry;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@NullMarked
final class EditSoundActionInputDialog {
    private EditSoundActionInputDialog() {
    }

    static DialogLike create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<Sound> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        final var current = action.getInput();
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var soundInput = response.getText("sound");
                    final var sourceInput = response.getText("source");
                    final var volumeInput = response.getText("volume");
                    final var pitchInput = response.getText("pitch");
                    if (soundInput == null || soundInput.isBlank()) {
                        DialogSupport.show(audience, ignored -> EditSoundActionInputDialog.create(hologram, line, actionName, action, header, Component.text("Sound cannot be empty", NamedTextColor.RED), reopen));
                        return;
                    }
                    final Key soundKey;
                    try {
                        soundKey = Key.key(soundInput.trim());
                    } catch (final IllegalArgumentException ignored) {
                        DialogSupport.show(audience, ignored2 -> EditSoundActionInputDialog.create(hologram, line, actionName, action, header, Component.text("Invalid sound key", NamedTextColor.RED), reopen));
                        return;
                    }
                    if (Registry.SOUND_EVENT.get(soundKey) == null) {
                        DialogSupport.show(audience, ignored -> EditSoundActionInputDialog.create(hologram, line, actionName, action, header, Component.text("Invalid sound", NamedTextColor.RED), reopen));
                        return;
                    }
                    final var source = DialogSupport.parseSoundSource(sourceInput);
                    if (source == null) {
                        DialogSupport.show(audience, ignored -> EditSoundActionInputDialog.create(hologram, line, actionName, action, header, Component.text("Invalid sound source", NamedTextColor.RED), reopen));
                        return;
                    }
                    final var volume = DialogSupport.parseDouble("Volume", volumeInput, 0, Double.MAX_VALUE);
                    if (volume.error() != null) {
                        DialogSupport.show(audience, ignored -> EditSoundActionInputDialog.create(hologram, line, actionName, action, header, Component.text(volume.error(), NamedTextColor.RED), reopen));
                        return;
                    }
                    final var pitch = DialogSupport.parseDouble("Pitch", pitchInput, 0, 2);
                    if (pitch.error() != null) {
                        DialogSupport.show(audience, ignored -> EditSoundActionInputDialog.create(hologram, line, actionName, action, header, Component.text(pitch.error(), NamedTextColor.RED), reopen));
                        return;
                    }
                    action.setInput(Sound.sound(soundKey, source, volume.value().floatValue(), pitch.value().floatValue()));
                    DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen)))))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Edit the sound key, source, volume, and pitch")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Play Sound"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("sound", Component.text("Sound")).initial(current.name().asString()).build(),
                                DialogInput.text("source", Component.text("Source")).initial(current.source().name()).build(),
                                DialogInput.text("volume", Component.text("Volume")).initial(Double.toString(current.volume())).build(),
                                DialogInput.text("pitch", Component.text("Pitch")).initial(Double.toString(current.pitch())).build()
                        ))
                        .build())
                .type(DialogType.multiAction(List.of(save)).exitAction(back).build()));
    }
}
