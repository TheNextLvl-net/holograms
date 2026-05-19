package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramProvider;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@NullMarked
public final class HologramDialog {
    private static final Map<UUID, Function<Audience, DialogLike>> LAST_DIALOGS = new ConcurrentHashMap<>();

    public static void showLast(final Audience audience) {
        if (audience instanceof final Player player) {
            show(audience, LAST_DIALOGS.getOrDefault(player.getUniqueId(), ignored -> overview()));
            return;
        }
        audience.showDialog(overview());
    }

    private static void show(final Audience audience, final Function<Audience, DialogLike> dialog) {
        if (audience instanceof final Player player) LAST_DIALOGS.put(player.getUniqueId(), dialog);
        audience.showDialog(dialog.apply(audience));
    }

    public static DialogLike overview() {
        final var create = ActionButton.builder(Component.text("New Hologram", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> createNewHologram("", null));
                }))).build();

        final var holograms = HologramProvider.instance().getHolograms()
                .sorted(Comparator.comparing(Hologram::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        final var actions = new ArrayList<ActionButton>();
        holograms.stream().map(hologram -> ActionButton.builder(Component.text(hologram.getName()))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, viewer -> editHologram(hologram, viewer));
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
                .type(DialogType.multiAction(actions).build()));
    }

    private static DialogLike createNewHologram(final String initial, @Nullable final Component note) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> overview());
                })))
                .width(150)
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
                        show(audience, ignored -> createNewHologram("", text));
                        return;
                    }

                    try {
                        final var hologram = HologramProvider.instance().spawnHologram(name, player.getLocation(), ignored -> {
                        });
                        show(audience, viewer -> editHologram(hologram, viewer));
                    } catch (final IllegalStateException ignored) {
                        final var text = Component.text("A hologram with this name already exists", NamedTextColor.RED);
                        show(audience, current -> createNewHologram(name, text));
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

    private static DialogLike editHologram(final Hologram hologram, final Audience viewer) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> overview());
                })))
                .width(300)
                .build();

        final var lines = hologram.getLines().toList();
        final var actions = new ArrayList<ActionButton>();
        for (var index = 0; index < lines.size(); index++) {
            final var line = lines.get(index);
            final var lineIndex = index;
            actions.add(ActionButton.builder(lineLabel(lineIndex, line))
                    .tooltip(linePreview(line, viewer))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        show(audience, current -> editLine(hologram, lineIndex, current));
                    }))).width(300).build());
        }

        actions.add(ActionButton.builder(Component.text("Add Line", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addLineType(hologram));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Teleport Hologram", NamedTextColor.AQUA))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> teleportHologram(hologram));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Rename Hologram", NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> renameHologram(hologram, hologram.getName(), null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Delete Hologram", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(deleteHologram(hologram, audience));
                }))).width(300).build());

        final var body = new ArrayList<DialogBody>();
        if (lines.isEmpty()) {
            body.add(DialogBody.plainMessage(Component.text("No lines have been added yet")));
        }
        actions.add(back);

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(hologram.getName()))
                        .body(body)
                        .build())
                .type(DialogType.multiAction(actions).columns(1).build()));
    }

    private static DialogLike editHologram(final Hologram hologram) {
        return editHologram(hologram, Audience.empty());
    }

    private static DialogLike moveHologram(final Hologram hologram, final Audience viewer) {
        final var confirm = ActionButton.builder(Component.text("Yes"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    if (!(audience instanceof final Player player)) {
                        show(audience, current -> editHologram(hologram, current));
                        return;
                    }

                    hologram.teleportAsync(player.getLocation());
                    show(audience, current -> editHologram(hologram, current));
                }))).build();

        final var cancel = ActionButton.builder(Component.text("No"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editHologram(hologram, current));
                }))).build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Move " + hologram.getName() + " here?"))
                        .build())
                .type(DialogType.confirmation(confirm, cancel)));
    }

    private static DialogLike teleportHologram(final Hologram hologram) {
        return teleportHologram(hologram, hologram.getLocation(), null);
    }

    private static DialogLike teleportHologram(final Hologram hologram, final Location location) {
        return teleportHologram(hologram, location, null);
    }

    private static DialogLike teleportHologram(final Hologram hologram, final Location location, @Nullable final Component note) {
        return teleportHologram(hologram, location, locationInputs(location), note);
    }

    private static DialogLike teleportHologram(
            final Hologram hologram,
            final Location location,
            final LocationInputs inputs,
            @Nullable final Component note
    ) {
        final var teleportPlayer = ActionButton.builder(Component.text("Teleport to Hologram", NamedTextColor.AQUA))
                .action(DialogAction.customClick((response, audience) -> {
                    if (!(audience instanceof final Player player)) {
                        show(audience, current -> editHologram(hologram, current));
                        return;
                    }

                    player.teleportAsync(hologram.getLocation()).thenRun(() -> {
                        final var parsed = parseLocation(location,
                                response.getText("world"), response.getText("x"), response.getText("y"), response.getText("z"),
                                response.getText("yaw"), response.getText("pitch"));
                        final var currentInputs = locationInputs(response);
                        show(audience, ignored -> teleportHologram(hologram, parsed.value() != null ? parsed.value() : location, currentInputs,
                                parsed.error() != null ? Component.text(parsed.error(), NamedTextColor.RED) : null));
                    });
                }, ClickCallback.Options.builder().uses(1).build())).width(300).build();
        final var moveHere = ActionButton.builder(Component.text("Move Here", NamedTextColor.AQUA))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    if (audience instanceof final Player player) {
                        final var snapshot = player.getLocation().clone();
                        show(audience, ignored -> teleportHologram(hologram, snapshot));
                        return;
                    }
                    show(audience, ignored -> teleportHologram(hologram));
                }))).width(300).build();
        final var move = ActionButton.builder(Component.text("Commence Teleport", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var target = parseLocation(hologram.getLocation(), response.getText("world"), response.getText("x"), response.getText("y"),
                            response.getText("z"), response.getText("yaw"), response.getText("pitch"));
                    if (target.error() != null) {
                        final var currentInputs = locationInputs(response);
                        show(audience, ignored -> teleportHologram(hologram, location, currentInputs, Component.text(target.error(), NamedTextColor.RED)));
                        return;
                    }

                    hologram.teleportAsync(target.value());
                    show(audience, ignored -> teleportHologram(hologram, target.value()));
                }, ClickCallback.Options.builder().uses(1).build()))
                .width(300).build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editHologram(hologram, current));
                }))).width(300).build();

        final var body = new ArrayList<DialogBody>();
        if (note != null) body.add(DialogBody.plainMessage(note));

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Teleport Hologram"))
                        .body(body)
                        .inputs(List.of(
                                locationInput("world", "World", inputs.world()),
                                locationInput("x", "X", inputs.x()),
                                locationInput("y", "Y", inputs.y()),
                                locationInput("z", "Z", inputs.z()),
                                locationInput("yaw", "Yaw", inputs.yaw()),
                                locationInput("pitch", "Pitch", inputs.pitch())
                        )).build())
                .type(DialogType.multiAction(List.of(teleportPlayer, moveHere, move, back)).columns(1).build()));
    }

    private static DialogLike deleteHologram(final Hologram hologram, final Audience viewer) {
        final var confirm = ActionButton.builder(Component.text("Delete Hologram", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    HologramProvider.instance().deleteHologram(hologram);
                    show(audience, ignored -> overview());
                }))).build();

        final var cancel = ActionButton.builder(Component.text("Cancel"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editHologram(hologram, current));
                }))).build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Delete " + hologram.getName() + "?"))
                        .body(List.of(DialogBody.plainMessage(Component.text("This cannot be undone"))))
                        .build())
                .type(DialogType.confirmation(confirm, cancel)));
    }

    private static DialogLike renameHologram(final Hologram hologram, final String initial, @Nullable final Component note) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editHologram(hologram, current));
                })))
                .width(150)
                .build();

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter the new hologram name")));
        if (note != null) body.add(DialogBody.plainMessage(note));

        final var rename = ActionButton.builder(Component.text("Rename"))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("name");
                    final var name = input != null ? input.trim() : null;
                    if (name == null || name.isBlank()) {
                        final var text = Component.text("Name cannot be empty", NamedTextColor.RED);
                        show(audience, ignored -> renameHologram(hologram, "", text));
                        return;
                    }

                    if (hologram.getName().equals(name)) {
                        show(audience, current -> editHologram(hologram, current));
                        return;
                    }

                    if (!hologram.setName(name)) {
                        final var text = Component.text("A hologram with this name already exists", NamedTextColor.RED);
                        show(audience, ignored -> renameHologram(hologram, name, text));
                        return;
                    }

                    show(audience, current -> editHologram(hologram, current));
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Rename Hologram"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("name", Component.text("Hologram name")).initial(initial).build()
                        )).build())
                .type(DialogType.multiAction(List.of(rename)).exitAction(back).build()));
    }

    private static DialogLike addLine(final Hologram hologram, final String initial, @Nullable final Component note) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addLineType(hologram));
                })))
                .width(150)
                .build();

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter the text for the new line")));
        if (note != null) body.add(DialogBody.plainMessage(note));

        final var add = ActionButton.builder(Component.text("Add"))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("text");
                    final var text = input != null ? input.trim() : null;
                    if (text == null || text.isBlank()) {
                        show(audience, ignored -> addLine(hologram, "", Component.text("Text cannot be empty", NamedTextColor.RED)));
                        return;
                    }

                    hologram.addTextLine().setUnparsedText(saveLineBreaks(text));
                    show(audience, current -> editHologram(hologram, current));
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Line"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("text", Component.text("Line text"))
                                        .initial(loadLineBreaks(initial))
                                        .maxLength(8192)
                                        .multiline(TextDialogInput.MultilineOptions.create(null, 120))
                                        .build()
                        )).build())
                .type(DialogType.multiAction(List.of(add)).exitAction(back).build()));
    }

    private static DialogLike addLineType(final Hologram hologram) {
        final var actions = new ArrayList<ActionButton>();
        actions.add(ActionButton.builder(Component.text("Text", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addLine(hologram, "", null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Item", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addItemLine(hologram, "minecraft:stone", null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Block", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addBlockLine(hologram, "minecraft:stone", null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Entity", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addEntityLine(hologram, "pig", null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Paged", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    hologram.addPagedLine();
                    show(audience, current -> editHologram(hologram, current));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editHologram(hologram, current));
                }))).width(300).build());

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Line")).build())
                .type(DialogType.multiAction(actions).columns(1).build()));
    }

    private static DialogLike addBlockLine(final Hologram hologram, final String initial, @Nullable final Component note) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addLineType(hologram));
                }))).width(150).build();
        final var add = ActionButton.builder(Component.text("Add"))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("block");
                    try {
                        hologram.addBlockLine().setBlock(Bukkit.createBlockData(input != null ? input.trim() : ""));
                        show(audience, current -> editHologram(hologram, current));
                    } catch (final IllegalArgumentException e) {
                        show(audience, ignored -> addBlockLine(hologram, input != null ? input : initial,
                                Component.text("Invalid block data", NamedTextColor.RED)));
                    }
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter block data, for example minecraft:stone")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Block Line"))
                        .body(body)
                        .inputs(List.of(DialogInput.text("block", Component.text("Block data")).initial(initial).build()))
                        .build())
                .type(DialogType.multiAction(List.of(add)).exitAction(back).build()));
    }

    private static DialogLike addItemLine(final Hologram hologram, final String initial, @Nullable final Component note) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addLineType(hologram));
                }))).width(150).build();
        final var add = ActionButton.builder(Component.text("Add"))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("item");
                    final var item = parseItemStack(input);
                    if (item.error() != null) {
                        show(audience, ignored -> addItemLine(hologram, input != null ? input : initial,
                                Component.text(item.error(), NamedTextColor.RED)));
                        return;
                    }

                    hologram.addItemLine().setItemStack(item.value());
                    show(audience, current -> editHologram(hologram, current));
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var held = ActionButton.builder(Component.text("Use Held Item", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    if (audience instanceof final Player player) {
                        hologram.addItemLine().setItemStack(player.getInventory().getItemInMainHand());
                    }
                    show(audience, current -> editHologram(hologram, current));
                }))).build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter an item material, for example minecraft:diamond")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Item Line"))
                        .body(body)
                        .inputs(List.of(DialogInput.text("item", Component.text("Item material")).initial(initial).build()))
                        .build())
                .type(DialogType.multiAction(List.of(add, held)).exitAction(back).build()));
    }

    private static DialogLike addEntityLine(final Hologram hologram, final String initial, @Nullable final Component note) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addLineType(hologram));
                }))).width(150).build();
        final var add = ActionButton.builder(Component.text("Add"))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("entity");
                    final var entityType = parseEntityType(input);
                    if (entityType.error() != null) {
                        show(audience, ignored -> addEntityLine(hologram, input != null ? input : initial,
                                Component.text(entityType.error(), NamedTextColor.RED)));
                        return;
                    }

                    try {
                        hologram.addEntityLine(entityType.value());
                        show(audience, current -> editHologram(hologram, current));
                    } catch (final IllegalArgumentException e) {
                        show(audience, ignored -> addEntityLine(hologram, input != null ? input : initial,
                                Component.text("Entity type is not spawnable", NamedTextColor.RED)));
                    }
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter an entity type, for example pig")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Entity Line"))
                        .body(body)
                        .inputs(List.of(DialogInput.text("entity", Component.text("Entity type")).initial(initial).build()))
                        .build())
                .type(DialogType.multiAction(List.of(add)).exitAction(back).build()));
    }

    private static DialogLike editLine(final Hologram hologram, final int lineIndex, final Audience viewer) {
        final var line = hologram.getLine(lineIndex).orElse(null);
        if (line == null) return editHologram(hologram, viewer);

        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editHologram(hologram, current));
                })))
                .width(300)
                .build();

        final var remove = ActionButton.builder(Component.text("Delete this line", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(deleteLine(hologram, lineIndex, audience));
                }))).width(300).build();

        return switch (line) {
            case final TextHologramLine textLine -> editTextLine(hologram, lineIndex, textLine, null);
            case final BlockHologramLine blockLine -> editBlockLine(hologram, lineIndex, blockLine, null);
            case final ItemHologramLine itemLine -> editItemLine(hologram, lineIndex, itemLine, null);
            case final EntityHologramLine entityLine -> editEntityLine(hologram, lineIndex, entityLine, null);
            case final PagedHologramLine pagedLine -> editPagedLine(hologram, lineIndex, pagedLine, viewer);
            default -> Dialog.create(builder -> builder.empty()
                    .base(DialogBase.builder(lineLabel(lineIndex, line))
                            .body(List.of(DialogBody.plainMessage(Component.text("This line type cannot be edited in dialogs yet"))))
                            .build())
                    .type(DialogType.multiAction(List.of(remove, back)).columns(1).build()));
        };

    }

    private static DialogLike editTextLine(
            final Hologram hologram,
            final int lineIndex,
            final TextHologramLine line,
            final String initial,
            @Nullable final Component note
    ) {
        final var remove = ActionButton.builder(Component.text("Delete this line", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(deleteLine(hologram, lineIndex, audience));
                }))).width(300).build();

        final var save = ActionButton.builder(Component.text("Save"))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("text");
                    final var text = input != null ? input.trim() : null;
                    if (text == null || text.isBlank()) {
                        show(audience, ignored -> editTextLine(hologram, lineIndex, line, "", Component.text("Text cannot be empty", NamedTextColor.RED)));
                        return;
                    }

                    line.setUnparsedText(saveLineBreaks(text));
                    show(audience, current -> editHologram(hologram, current));
                }, ClickCallback.Options.builder().uses(1).build()))
                .width(300).build();

        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editHologram(hologram, current));
                })))
                .width(300)
                .build();

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Edit the text for this line")));
        if (note != null) body.add(DialogBody.plainMessage(note));

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(lineLabel(lineIndex, line))
                        .body(body)
                        .inputs(List.of(DialogInput.text("text", Component.text("Line text"))
                                .initial(loadLineBreaks(initial))
                                .maxLength(8192)
                                .multiline(TextDialogInput.MultilineOptions.create(null, 120))
                                .build()))
                        .build())
                .type(DialogType.multiAction(List.of(save, remove)).columns(1).exitAction(back).build()));
    }

    private static DialogLike editTextPage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final TextHologramLine page,
            @Nullable final Component note
    ) {
        final var save = ActionButton.builder(Component.text("Save")).width(300)
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("text");
                    final var text = input != null ? input.trim() : null;
                    if (text == null || text.isBlank()) {
                        show(audience, ignored -> editTextPage(hologram, lineIndex, pagedLine, pageIndex, page,
                                Component.text("Text cannot be empty", NamedTextColor.RED)));
                        return;
                    }
                    page.setUnparsedText(saveLineBreaks(text));
                    show(audience, current -> editPagedLine(hologram, lineIndex, pagedLine, current));
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var body = new ArrayList<DialogBody>();
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Page " + (pageIndex + 1)))
                        .body(body)
                        .inputs(List.of(DialogInput.text("text", Component.text("Page text"))
                                .initial(loadLineBreaks(page.getUnparsedText().orElse("")))
                                .maxLength(8192)
                                .multiline(TextDialogInput.MultilineOptions.create(null, 120))
                                .build()))
                        .build())
                .type(DialogType.multiAction(List.of(save, deletePageButton(hologram, lineIndex, pagedLine, pageIndex)))
                        .columns(1)
                        .exitAction(editPagedBackButton(hologram, lineIndex, pagedLine))
                        .build()));
    }

    private static DialogLike editBlockPage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final BlockHologramLine page,
            @Nullable final Component note
    ) {
        final var save = ActionButton.builder(Component.text("Save")).width(300)
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("block");
                    try {
                        page.setBlock(Bukkit.createBlockData(input != null ? input.trim() : ""));
                        show(audience, current -> editPagedLine(hologram, lineIndex, pagedLine, current));
                    } catch (final IllegalArgumentException e) {
                        show(audience, ignored -> editBlockPage(hologram, lineIndex, pagedLine, pageIndex, page,
                                Component.text("Invalid block data", NamedTextColor.RED)));
                    }
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var body = new ArrayList<DialogBody>();
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Page " + (pageIndex + 1)))
                        .body(body)
                        .inputs(List.of(DialogInput.text("block", Component.text("Block data"))
                                .initial(page.getBlock().getAsString())
                                .build()))
                        .build())
                .type(DialogType.multiAction(List.of(save, deletePageButton(hologram, lineIndex, pagedLine, pageIndex)))
                        .columns(1)
                        .exitAction(editPagedBackButton(hologram, lineIndex, pagedLine))
                        .build()));
    }

    private static DialogLike editEntityPage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final EntityHologramLine page,
            @Nullable final Component note
    ) {
        final var save = ActionButton.builder(Component.text("Save")).width(300)
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("entity");
                    final var entityType = parseEntityType(input);
                    if (entityType.error() != null) {
                        show(audience, ignored -> editEntityPage(hologram, lineIndex, pagedLine, pageIndex, page,
                                Component.text(entityType.error(), NamedTextColor.RED)));
                        return;
                    }
                    try {
                        page.setEntityType(entityType.value());
                        show(audience, current -> editPagedLine(hologram, lineIndex, pagedLine, current));
                    } catch (final IllegalArgumentException e) {
                        show(audience, ignored -> editEntityPage(hologram, lineIndex, pagedLine, pageIndex, page,
                                Component.text("Entity type is not spawnable", NamedTextColor.RED)));
                    }
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var body = new ArrayList<DialogBody>();
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Page " + (pageIndex + 1)))
                        .body(body)
                        .inputs(List.of(DialogInput.text("entity", Component.text("Entity type"))
                                .initial(page.getEntityType().key().asString())
                                .build()))
                        .build())
                .type(DialogType.multiAction(List.of(save, deletePageButton(hologram, lineIndex, pagedLine, pageIndex)))
                        .columns(1)
                        .exitAction(editPagedBackButton(hologram, lineIndex, pagedLine))
                        .build()));
    }

    private static DialogLike editItemPage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final ItemHologramLine page,
            @Nullable final Component note
    ) {
        final var save = ActionButton.builder(Component.text("Save")).width(300)
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("item");
                    final var item = parseItemStack(input);
                    if (item.error() != null) {
                        show(audience, ignored -> editItemPage(hologram, lineIndex, pagedLine, pageIndex, page,
                                Component.text(item.error(), NamedTextColor.RED)));
                        return;
                    }

                    page.setItemStack(item.value());
                    show(audience, current -> editPagedLine(hologram, lineIndex, pagedLine, current));
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var setHeld = ActionButton.builder(Component.text("Set to Held Item", NamedTextColor.GREEN)).width(300)
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    if (audience instanceof final Player player) {
                        page.setItemStack(player.getInventory().getItemInMainHand());
                    }
                    show(audience, current -> editPagedLine(hologram, lineIndex, pagedLine, current));
                }))).build();
        final var body = new ArrayList<DialogBody>();
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Page " + (pageIndex + 1)))
                        .body(body)
                        .inputs(List.of(DialogInput.text("item", Component.text("Item material"))
                                .initial(page.getItemStack().getType().key().asString())
                                .build()))
                        .build())
                .type(DialogType.multiAction(List.of(save, setHeld, deletePageButton(hologram, lineIndex, pagedLine, pageIndex)))
                        .columns(1)
                        .exitAction(editPagedBackButton(hologram, lineIndex, pagedLine))
                        .build()));
    }

    private static DialogLike deleteLine(final Hologram hologram, final int lineIndex, final Audience viewer) {
        final var confirm = ActionButton.builder(Component.text("Delete this line", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    hologram.removeLine(lineIndex);
                    show(audience, current -> editHologram(hologram, current));
                }))).build();

        final var cancel = ActionButton.builder(Component.text("Cancel"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editLine(hologram, lineIndex, current));
                }))).build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Delete line " + (lineIndex + 1) + "?"))
                        .body(List.of(DialogBody.plainMessage(Component.text("This cannot be undone"))))
                        .build())
                .type(DialogType.confirmation(confirm, cancel)));
    }

    private static DialogLike editBlockLine(
            final Hologram hologram,
            final int lineIndex,
            final BlockHologramLine line,
            @Nullable final Component note
    ) {
        final var save = ActionButton.builder(Component.text("Save")).width(300)
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("block");
                    try {
                        line.setBlock(Bukkit.createBlockData(input != null ? input.trim() : ""));
                        show(audience, current -> editHologram(hologram, current));
                    } catch (final IllegalArgumentException e) {
                        show(audience, ignored -> editBlockLine(hologram, lineIndex, line,
                                Component.text("Invalid block data", NamedTextColor.RED)));
                    }
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var remove = deleteLineButton(hologram, lineIndex);
        final var back = editHologramBackButton(hologram);
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Edit the block data for this line")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(lineLabel(lineIndex, line))
                        .body(body)
                        .inputs(List.of(DialogInput.text("block", Component.text("Block data"))
                                .initial(line.getBlock().getAsString())
                                .build()))
                        .build())
                .type(DialogType.multiAction(List.of(save, remove)).columns(1).exitAction(back).build()));
    }

    private static DialogLike editEntityLine(
            final Hologram hologram,
            final int lineIndex,
            final EntityHologramLine line,
            @Nullable final Component note
    ) {
        final var save = ActionButton.builder(Component.text("Save")).width(300)
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("entity");
                    final var entityType = parseEntityType(input);
                    if (entityType.error() != null) {
                        show(audience, ignored -> editEntityLine(hologram, lineIndex, line,
                                Component.text(entityType.error(), NamedTextColor.RED)));
                        return;
                    }

                    try {
                        line.setEntityType(entityType.value());
                        show(audience, current -> editHologram(hologram, current));
                    } catch (final IllegalArgumentException e) {
                        show(audience, ignored -> editEntityLine(hologram, lineIndex, line,
                                Component.text("Entity type is not spawnable", NamedTextColor.RED)));
                    }
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var remove = deleteLineButton(hologram, lineIndex);
        final var back = editHologramBackButton(hologram);
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Edit the entity type for this line")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(lineLabel(lineIndex, line))
                        .body(body)
                        .inputs(List.of(DialogInput.text("entity", Component.text("Entity type"))
                                .initial(line.getEntityType().key().asString())
                                .build()))
                        .build())
                .type(DialogType.multiAction(List.of(save, remove)).columns(1).exitAction(back).build()));
    }

    private static DialogLike editItemLine(
            final Hologram hologram,
            final int lineIndex,
            final ItemHologramLine line,
            @Nullable final Component note
    ) {
        final var save = ActionButton.builder(Component.text("Save")).width(300)
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("item");
                    final var item = parseItemStack(input);
                    if (item.error() != null) {
                        show(audience, ignored -> editItemLine(hologram, lineIndex, line,
                                Component.text(item.error(), NamedTextColor.RED)));
                        return;
                    }

                    line.setItemStack(item.value());
                    show(audience, current -> editHologram(hologram, current));
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var setHeld = ActionButton.builder(Component.text("Set to Held Item", NamedTextColor.GREEN)).width(300)
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    if (audience instanceof final Player player) {
                        line.setItemStack(player.getInventory().getItemInMainHand());
                    }
                    show(audience, current -> editHologram(hologram, current));
                }))).build();
        final var playerHead = ActionButton.builder(Component.text("Toggle Player Head", NamedTextColor.YELLOW)).width(300)
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    line.setPlayerHead(!line.isPlayerHead());
                    show(audience, current -> editLine(hologram, lineIndex, current));
                }))).build();
        final var remove = deleteLineButton(hologram, lineIndex);
        final var back = editHologramBackButton(hologram);
        final var body = new ArrayList<DialogBody>();
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(lineLabel(lineIndex, line))
                        .body(body)
                        .inputs(List.of(DialogInput.text("item", Component.text("Item material"))
                                .initial(line.getItemStack().getType().key().asString())
                                .build()))
                        .build())
                .type(DialogType.multiAction(List.of(save, setHeld, playerHead, remove)).columns(1).exitAction(back).build()));
    }

    private static ActionButton deleteLineButton(final Hologram hologram, final int lineIndex) {
        return ActionButton.builder(Component.text("Delete this line", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(deleteLine(hologram, lineIndex, audience));
                }))).width(300).build();
    }

    private static ActionButton editHologramBackButton(final Hologram hologram) {
        return ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editHologram(hologram, current));
                }))).width(300).build();
    }

    private static DialogLike editPagedLine(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final Audience viewer
    ) {
        final var pages = line.getPages().toList();
        final var actions = new ArrayList<ActionButton>();
        for (var index = 0; index < pages.size(); index++) {
            final var page = pages.get(index);
            final var pageIndex = index;
            actions.add(ActionButton.builder(Component.text("Page " + (pageIndex + 1) + ": " + lineDescription(page)))
                    .tooltip(linePreview(page, viewer))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        show(audience, current -> editPage(hologram, lineIndex, line, pageIndex, current));
                    }))).width(300).build());
        }
        actions.add(ActionButton.builder(Component.text("Add Page", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addPageType(hologram, lineIndex, line));
                }))).width(300).build());
        actions.add(deleteLineButton(hologram, lineIndex));
        actions.add(editHologramBackButton(hologram));

        final var body = new ArrayList<DialogBody>();
        if (pages.isEmpty()) body.add(DialogBody.plainMessage(Component.text("No pages have been added yet")));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(lineLabel(lineIndex, line)).body(body).build())
                .type(DialogType.multiAction(actions).columns(1).build()));
    }

    private static DialogLike addPageType(final Hologram hologram, final int lineIndex, final PagedHologramLine line) {
        final var actions = new ArrayList<ActionButton>();
        actions.add(ActionButton.builder(Component.text("Text", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addTextPage(hologram, lineIndex, line, "", null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Item", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addItemPage(hologram, lineIndex, line, "minecraft:stone", null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Block", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addBlockPage(hologram, lineIndex, line, "minecraft:stone", null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Entity", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addEntityPage(hologram, lineIndex, line, "pig", null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                }))).width(300).build());
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Page")).build())
                .type(DialogType.multiAction(actions).columns(1).build()));
    }

    private static DialogLike editPage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final Audience viewer
    ) {
        final var page = pagedLine.getPage(pageIndex).orElse(null);
        return switch (page) {
            case null -> editPagedLine(hologram, lineIndex, pagedLine, viewer);
            case final TextHologramLine textLine ->
                    editTextPage(hologram, lineIndex, pagedLine, pageIndex, textLine, null);
            case final BlockHologramLine blockLine ->
                    editBlockPage(hologram, lineIndex, pagedLine, pageIndex, blockLine, null);
            case final ItemHologramLine itemLine ->
                    editItemPage(hologram, lineIndex, pagedLine, pageIndex, itemLine, null);
            case final EntityHologramLine entityLine ->
                    editEntityPage(hologram, lineIndex, pagedLine, pageIndex, entityLine, null);
            default -> editPagedLine(hologram, lineIndex, pagedLine, viewer);
        };
    }

    private static ActionButton deletePageButton(final Hologram hologram, final int lineIndex, final PagedHologramLine line, final int pageIndex) {
        return ActionButton.builder(Component.text("Delete this page", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    line.removePage(pageIndex);
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                }))).width(300).build();
    }

    private static ActionButton editPagedBackButton(final Hologram hologram, final int lineIndex, final PagedHologramLine line) {
        return ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addPageType(hologram, lineIndex, line));
                }))).width(300).build();
    }

    private static DialogLike addTextPage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final String initial,
            @Nullable final Component note
    ) {
        final var add = ActionButton.builder(Component.text("Add"))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("text");
                    final var text = input != null ? input.trim() : null;
                    if (text == null || text.isBlank()) {
                        show(audience, ignored -> addTextPage(hologram, lineIndex, line, "", Component.text("Text cannot be empty", NamedTextColor.RED)));
                        return;
                    }
                    line.addTextPage().setUnparsedText(saveLineBreaks(text));
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var body = new ArrayList<DialogBody>();
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Text Page"))
                        .body(body)
                        .inputs(List.of(DialogInput.text("text", Component.text("Page text"))
                                .initial(loadLineBreaks(initial))
                                .maxLength(8192)
                                .multiline(TextDialogInput.MultilineOptions.create(null, 120))
                                .build()))
                        .build())
                .type(DialogType.multiAction(List.of(add)).exitAction(editPagedBackButton(hologram, lineIndex, line)).build()));
    }

    private static DialogLike addItemPage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final String initial,
            @Nullable final Component note
    ) {
        final var add = ActionButton.builder(Component.text("Add"))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("item");
                    final var item = parseItemStack(input);
                    if (item.error() != null) {
                        show(audience, ignored -> addItemPage(hologram, lineIndex, line, input != null ? input : initial,
                                Component.text(item.error(), NamedTextColor.RED)));
                        return;
                    }

                    line.addItemPage().setItemStack(item.value());
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var held = ActionButton.builder(Component.text("Use Held Item", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    if (audience instanceof final Player player) {
                        line.addItemPage().setItemStack(player.getInventory().getItemInMainHand());
                    }
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                }))).build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter an item material, for example minecraft:diamond")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Item Page"))
                        .body(body)
                        .inputs(List.of(DialogInput.text("item", Component.text("Item material")).initial(initial).build()))
                        .build())
                .type(DialogType.multiAction(List.of(add, held)).exitAction(editPagedBackButton(hologram, lineIndex, line)).build()));
    }

    private static DialogLike addBlockPage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final String initial,
            @Nullable final Component note
    ) {
        final var add = ActionButton.builder(Component.text("Add"))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("block");
                    try {
                        line.addBlockPage().setBlock(Bukkit.createBlockData(input != null ? input.trim() : ""));
                        show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                    } catch (final IllegalArgumentException e) {
                        show(audience, ignored -> addBlockPage(hologram, lineIndex, line, input != null ? input : initial,
                                Component.text("Invalid block data", NamedTextColor.RED)));
                    }
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var body = new ArrayList<DialogBody>();
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Block Page"))
                        .body(body)
                        .inputs(List.of(DialogInput.text("block", Component.text("Block data")).initial(initial).build()))
                        .build())
                .type(DialogType.multiAction(List.of(add)).exitAction(editPagedBackButton(hologram, lineIndex, line)).build()));
    }

    private static DialogLike addEntityPage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final String initial,
            @Nullable final Component note
    ) {
        final var add = ActionButton.builder(Component.text("Add"))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("entity");
                    final var entityType = parseEntityType(input);
                    if (entityType.error() != null) {
                        show(audience, ignored -> addEntityPage(hologram, lineIndex, line, input != null ? input : initial,
                                Component.text(entityType.error(), NamedTextColor.RED)));
                        return;
                    }
                    try {
                        line.addEntityPage(entityType.value());
                        show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                    } catch (final IllegalArgumentException e) {
                        show(audience, ignored -> addEntityPage(hologram, lineIndex, line, input != null ? input : initial,
                                Component.text("Entity type is not spawnable", NamedTextColor.RED)));
                    }
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var body = new ArrayList<DialogBody>();
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Entity Page"))
                        .body(body)
                        .inputs(List.of(DialogInput.text("entity", Component.text("Entity type")).initial(initial).build()))
                        .build())
                .type(DialogType.multiAction(List.of(add)).exitAction(editPagedBackButton(hologram, lineIndex, line)).build()));
    }

    private static DialogLike editTextLine(
            final Hologram hologram,
            final int lineIndex,
            final TextHologramLine line,
            @Nullable final Component note
    ) {
        return editTextLine(hologram, lineIndex, line, line.getUnparsedText().orElse(""), note);
    }

    private static Component lineLabel(final int lineIndex, final HologramLine line) {
        return Component.text(lineIndex + 1 + ". " + lineDescription(line));
    }

    private static String lineDescription(final HologramLine line) {
        return switch (line) {
            case final TextHologramLine textLine -> textLine.getUnparsedText().filter(text -> !text.isBlank())
                    .orElse(line.getType().name());
            case final BlockHologramLine blockLine -> blockLine.getBlock().getAsString();
            case final ItemHologramLine itemLine -> itemLine.getItemStack().getType().key().asString();
            case final EntityHologramLine entityLine -> entityLine.getEntityType().key().asString();
            case final PagedHologramLine pagedLine -> "PAGED (" + pagedLine.getPageCount() + " pages)";
            default -> line.getType().name();
        };
    }

    private static @Nullable Component linePreview(final HologramLine line, final Audience audience) {
        if (line instanceof final TextHologramLine textLine) {
            return textLine.getText(audience).orElse(null);
        }
        return null;
    }

    private static String loadLineBreaks(final String text) {
        return text.replace("<newline>", "\n").replace("<br>", "\n");
    }

    private static String saveLineBreaks(final String text) {
        return text.replace("\r\n", "\n").replace('\r', '\n').replace("\n", "<newline>");
    }

    @SuppressWarnings("PatternValidation")
    private static ParseResult<EntityType> parseEntityType(final @Nullable String input) {
        try {
            if (input == null || input.isBlank()) return new ParseResult<>(null, "Entity type cannot be empty");
            final var entityType = Registry.ENTITY_TYPE.get(Key.key(input));
            return entityType != null
                    ? new ParseResult<>(entityType, null)
                    : new ParseResult<>(null, "Invalid entity type");
        } catch (final IllegalArgumentException | InvalidKeyException ignored) {
            return new ParseResult<>(null, "Invalid entity type");
        }
    }

    private static ParseResult<ItemStack> parseItemStack(final @Nullable String input) {
        try {
            if (input == null || input.isBlank()) return new ParseResult<>(null, "Item cannot be empty");
            return new ParseResult<>(Bukkit.getServer().getItemFactory().createItemStack(input), null);
        } catch (final IllegalArgumentException e) {
            final var message = e.getMessage();
            return new ParseResult<>(null, message != null && !message.isBlank() ? message : "Invalid item");
        }
    }

    private record ParseResult<T>(@Nullable T value, @Nullable String error) {
    }

    private record LocationInputs(String world, String x, String y, String z, String yaw, String pitch) {
    }

    private static DialogInput locationInput(final String key, final String label, final String initial) {
        return DialogInput.text(key, Component.text(label)).initial(initial).build();
    }

    private static LocationInputs locationInputs(final Location location) {
        return new LocationInputs(
                location.getWorld().key().asString(),
                Double.toString(location.getX()),
                Double.toString(location.getY()),
                Double.toString(location.getZ()),
                Double.toString(location.getYaw()),
                Double.toString(location.getPitch())
        );
    }

    private static LocationInputs locationInputs(final io.papermc.paper.dialog.DialogResponseView response) {
        return new LocationInputs(
                input(response, "world"),
                input(response, "x"),
                input(response, "y"),
                input(response, "z"),
                input(response, "yaw"),
                input(response, "pitch")
        );
    }

    private static String input(final io.papermc.paper.dialog.DialogResponseView response, final String key) {
        final var value = response.getText(key);
        return value != null ? value : "";
    }

    private static ParseResult<Location> parseLocation(
            final Location fallback,
            @Nullable final String world,
            @Nullable final String x,
            @Nullable final String y,
            @Nullable final String z,
            @Nullable final String yaw,
            @Nullable final String pitch
    ) {
        final var parsedWorld = parseWorld(world);
        if (parsedWorld.error() != null) return new ParseResult<>(null, parsedWorld.error());

        final var parsedX = parseDouble("X", x, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (parsedX.error() != null) return new ParseResult<>(null, parsedX.error());
        final var parsedY = parseDouble("Y", y, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (parsedY.error() != null) return new ParseResult<>(null, parsedY.error());
        final var parsedZ = parseDouble("Z", z, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (parsedZ.error() != null) return new ParseResult<>(null, parsedZ.error());
        final var parsedYaw = parseDouble("Yaw", yaw, -180, 180);
        if (parsedYaw.error() != null) return new ParseResult<>(null, parsedYaw.error());
        final var parsedPitch = parseDouble("Pitch", pitch, -90, 90);
        if (parsedPitch.error() != null) return new ParseResult<>(null, parsedPitch.error());

        final var target = fallback.clone();
        target.setWorld(parsedWorld.value());
        target.setX(parsedX.value());
        target.setY(parsedY.value());
        target.setZ(parsedZ.value());
        target.setYaw(parsedYaw.value().floatValue());
        target.setPitch(parsedPitch.value().floatValue());
        return new ParseResult<>(target, null);
    }

    private static ParseResult<Double> parseDouble(
            final String label,
            @Nullable final String input,
            final double min,
            final double max
    ) {
        if (input == null || input.isBlank()) return new ParseResult<>(null, label + " cannot be empty");
        try {
            final var value = Double.parseDouble(input.trim());
            if (value < min || value > max) return new ParseResult<>(null,
                    label + " must be between " + formatNumber(min) + " and " + formatNumber(max));
            return new ParseResult<>(value, null);
        } catch (final NumberFormatException ignored) {
            return new ParseResult<>(null, label + " must be a number");
        }
    }

    private static String formatNumber(final double value) {
        return value % 1 == 0 ? Long.toString((long) value) : Double.toString(value);
    }

    @SuppressWarnings("PatternValidation")
    private static ParseResult<World> parseWorld(@Nullable final String input) {
        try {
            if (input == null || input.isBlank()) return new ParseResult<>(null, "World cannot be empty");
            final var world = Bukkit.getWorld(Key.key(input.trim()));
            return world != null
                    ? new ParseResult<>(world, null)
                    : new ParseResult<>(null, "World is not loaded");
        } catch (final InvalidKeyException ignored) {
            return new ParseResult<>(null, "Invalid world key");
        }
    }
}
