package net.thenextlvl.hologram.plugin.commands.dialog;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import net.thenextlvl.dialogs.Dialog;
import net.thenextlvl.dialogs.body.Body;
import net.thenextlvl.dialogs.button.Button;
import net.thenextlvl.dialogs.input.Input;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.action.UnparsedTitle;
import net.thenextlvl.hologram.line.HologramLine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@NullMarked
final class EditTitleActionInputDialog {
    private EditTitleActionInputDialog() {
    }

    static net.thenextlvl.dialogs.Dialog<?> create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<UnparsedTitle> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, net.thenextlvl.dialogs.Dialog<?>> reopen
    ) {
        final var current = action.getInput();
        final var times = current.times();
        final var save = Button.callback((response, audience) -> {
            final var title = response.getText("title");
            final var subtitle = response.getText("subtitle");
            final var fadeIn = response.getText("fade_in");
            final var stay = response.getText("stay");
            final var fadeOut = response.getText("fade_out");
            final var hasTimes = !DialogSupport.isBlank(fadeIn) || !DialogSupport.isBlank(stay) || !DialogSupport.isBlank(fadeOut);
            final var allTimes = !DialogSupport.isBlank(fadeIn) && !DialogSupport.isBlank(stay) && !DialogSupport.isBlank(fadeOut);
            if (hasTimes && !allTimes) {
                DialogSupport.show(audience, ignored -> EditTitleActionInputDialog.create(hologram, line, actionName, action, header, Component.text("Either set all title timings or leave them blank", NamedTextColor.RED), reopen));
                return;
            }

            final Title.Times parsedTimes;
            try {
                parsedTimes = hasTimes
                        ? Title.Times.times(
                        Ticks.duration(Integer.parseInt(fadeIn.trim())),
                        Ticks.duration(Integer.parseInt(stay.trim())),
                        Ticks.duration(Integer.parseInt(fadeOut.trim())))
                        : null;
            } catch (final NumberFormatException ignored) {
                DialogSupport.show(audience, ignored1 -> EditTitleActionInputDialog.create(hologram, line, actionName, action, header, Component.text("Title timings must be numbers", NamedTextColor.RED), reopen));
                return;
            }

            action.setInput(new UnparsedTitle(title != null ? title : "", subtitle != null ? subtitle : "", parsedTimes));
            DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
        }, Component.text("Save", NamedTextColor.GREEN)).uses(1);
        final var back = BackButton.create(ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen));
        final var body = new ArrayList<DialogBody>();
        body.add(Body.text(Component.text("Edit the title, subtitle, and optional timings in ticks")));
        if (note != null) body.add(Body.text(note));
        final var dialog = Dialog.multiAction().title(Component.text("Send Title"));
        body.forEach(dialog::addBody);
        List.of(
                Input.text("title", Component.text("Title")).initial(current.title()).build(),
                Input.text("subtitle", Component.text("Subtitle")).initial(current.subtitle()).build(),
                Input.text("fade_in", Component.text("Fade In (ticks)")).initial(times != null ? Long.toString(times.fadeIn().toMillis() / 50) : "").build(),
                Input.text("stay", Component.text("Stay (ticks)")).initial(times != null ? Long.toString(times.stay().toMillis() / 50) : "").build(),
                Input.text("fade_out", Component.text("Fade Out (ticks)")).initial(times != null ? Long.toString(times.fadeOut().toMillis() / 50) : "").build()
        ).forEach(dialog::addInput);
        List.of(save).forEach(dialog::addButton);
        dialog.exitAction(back);
        return dialog;
    }
}
