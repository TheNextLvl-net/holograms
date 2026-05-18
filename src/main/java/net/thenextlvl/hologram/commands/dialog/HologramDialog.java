package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramProvider;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@NullMarked
public final class HologramDialog {
    public static DialogLike overview() {
        final var close = ActionButton.builder(Component.text("Close")).build();

        final var create = ActionButton.builder(Component.text("New Hologram"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(createNewHologram("", null));
                }))).width(302).build();

        final var holograms = HologramProvider.instance().getHolograms()
                .sorted(Comparator.comparing(Hologram::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        final var actions = new ArrayList<ActionButton>();
        holograms.stream().map(hologram -> ActionButton.builder(Component.text(hologram.getName()))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(editHologram(hologram));
                }))).build()).forEach(actions::add);
        actions.add(create);

        final var body = new ArrayList<DialogBody>();
        if (holograms.isEmpty()) {
            body.add(DialogBody.plainMessage(Component.text("No holograms have been created yet")));
        }

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Manage your Holograms"))
                        .body(body)
                        .build())
                .type(DialogType.multiAction(actions).exitAction(close).build()));
    }

    private static DialogLike createNewHologram(final String initial, @Nullable final Component note) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(overview());
                })))
                .build();

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter the name of the new hologram")));
        if (note != null) body.add(DialogBody.plainMessage(note));

        final var create = ActionButton.builder(Component.text("Create"))
                .action(DialogAction.customClick((response, audience) -> {
                    if (!(audience instanceof final Player player)) {
                        audience.closeDialog();
                        return;
                    }

                    final var input = response.getText("name");
                    final var name = input != null ? input.trim() : null;
                    if (name == null || name.isBlank()) {
                        final var text = Component.text("Name cannot be empty", NamedTextColor.RED);
                        audience.showDialog(createNewHologram("", text));
                        return;
                    }

                    try {
                        final var hologram = HologramProvider.instance().spawnHologram(name, player.getLocation(), ignored -> {
                        });
                        audience.showDialog(editHologram(hologram));
                    } catch (final IllegalStateException ignored) {
                        final var text = Component.text("A hologram with this name already exists", NamedTextColor.RED);
                        audience.showDialog(createNewHologram(name, text));
                    }
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Create a new hologram"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("name", Component.text("Hologram name")).initial(initial).build()
                        )).build())
                .type(DialogType.multiAction(List.of(create)).exitAction(back).build()));
    }

    private static DialogLike editHologram(final Hologram hologram) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(overview());
                })))
                .build();

        final var lines = hologram.getLines().toList();
        final var actions = new ArrayList<ActionButton>();
        for (var index = 0; index < lines.size(); index++) {
            final var line = lines.get(index);
            final var lineIndex = index;
            actions.add(ActionButton.builder(lineLabel(lineIndex, line))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        audience.showDialog(editLine(hologram, lineIndex));
                    }))).width(300).build());
        }

        actions.add(ActionButton.builder(Component.text("Add Line"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(addLine(hologram, "", null));
                }))).width(300).build());

        final var body = new ArrayList<DialogBody>();
        if (lines.isEmpty()) {
            body.add(DialogBody.plainMessage(Component.text("No lines have been added yet")));
        }

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(hologram.getName()))
                        .body(body)
                        .build())
                .type(DialogType.multiAction(actions).columns(1).exitAction(back).build()));
    }

    private static DialogLike addLine(final Hologram hologram, final String initial, @Nullable final Component note) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(editHologram(hologram));
                })))
                .build();

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter the text for the new line")));
        if (note != null) body.add(DialogBody.plainMessage(note));

        final var add = ActionButton.builder(Component.text("Add"))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("text");
                    final var text = input != null ? input.trim() : null;
                    if (text == null || text.isBlank()) {
                        audience.showDialog(addLine(hologram, "", Component.text("Text cannot be empty", NamedTextColor.RED)));
                        return;
                    }

                    hologram.addTextLine().setUnparsedText(text);
                    audience.showDialog(editHologram(hologram));
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Line"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("text", Component.text("Line text")).initial(initial).build()
                        )).build())
                .type(DialogType.multiAction(List.of(add)).exitAction(back).build()));
    }

    private static DialogLike editLine(final Hologram hologram, final int lineIndex) {
        final var line = hologram.getLine(lineIndex).orElse(null);
        if (line == null) return editHologram(hologram);

        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(editHologram(hologram));
                })))
                .build();

        final var remove = ActionButton.builder(Component.text("Remove"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    hologram.removeLine(lineIndex);
                    audience.showDialog(editHologram(hologram));
                }))).build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(lineLabel(lineIndex, line))
                        .body(List.of(DialogBody.plainMessage(Component.text("Line edit dialog is not implemented yet"))))
                        .build())
                .type(DialogType.multiAction(List.of(remove)).exitAction(back).build()));
    }

    private static Component lineLabel(final int lineIndex, final HologramLine line) {
        return Component.text(lineIndex + 1 + ". " + lineDescription(line));
    }

    private static String lineDescription(final HologramLine line) {
        if (line instanceof final TextHologramLine textLine) {
            return textLine.getUnparsedText().filter(text -> !text.isBlank())
                    .orElse(line.getType().name());
        }
        return line.getType().name();
    }
}
