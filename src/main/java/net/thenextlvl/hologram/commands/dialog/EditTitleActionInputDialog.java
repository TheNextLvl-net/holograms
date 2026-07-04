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
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
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

    static DialogLike create(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<UnparsedTitle> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        final var current = action.getInput();
        final var times = current.times();
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
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
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, actionName, action, header, note, reopen)))))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Edit the title, subtitle, and optional timings in ticks")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Send Title"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("title", Component.text("Title")).initial(current.title()).build(),
                                DialogInput.text("subtitle", Component.text("Subtitle")).initial(current.subtitle()).build(),
                                DialogInput.text("fade_in", Component.text("Fade In (ticks)")).initial(times != null ? Long.toString(times.fadeIn().toMillis() / 50) : "").build(),
                                DialogInput.text("stay", Component.text("Stay (ticks)")).initial(times != null ? Long.toString(times.stay().toMillis() / 50) : "").build(),
                                DialogInput.text("fade_out", Component.text("Fade Out (ticks)")).initial(times != null ? Long.toString(times.fadeOut().toMillis() / 50) : "").build()
                        ))
                        .build())
                .type(DialogType.multiAction(List.of(save)).exitAction(back).build()));
    }
}
