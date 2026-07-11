package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
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

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<Sound> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, net.thenextlvl.dialogs.Dialog<?>> reopen
    ) {
        final var current = action.getInput();
        final var save = Button.callback((response, audience) -> {
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
        }, Component.text("Save", NamedTextColor.GREEN)).uses(1);
        final var back = BackButton.create(ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
        final var body = new ArrayList<DialogBody>();
        body.add(Body.text(Component.text("Edit the sound key, source, volume, and pitch")));
        if (note != null) body.add(Body.text(note));
        final var dialog = Dialog.multiAction().title(Component.text("Play Sound"));
        body.forEach(dialog::addBody);
        List.of(
                Input.text("sound", Component.text("Sound")).initial(current.name().asString()).build(),
                Input.text("source", Component.text("Source")).initial(current.source().name()).build(),
                Input.text("volume", Component.text("Volume")).initial(Double.toString(current.volume())).build(),
                Input.text("pitch", Component.text("Pitch")).initial(Double.toString(current.pitch())).build()
        ).forEach(dialog::addInput);
        List.of(save).forEach(dialog::addButton);
        dialog.exitAction(back);
        return dialog;
    }
}
