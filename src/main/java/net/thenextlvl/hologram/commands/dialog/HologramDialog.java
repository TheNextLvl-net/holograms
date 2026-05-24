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
import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramProvider;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.DisplayHologramLine;
import net.thenextlvl.hologram.line.StaticHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.nio.file.Path;

@NullMarked
public final class HologramDialog {
    private static final int SEARCH_PAGE_SIZE = 20;
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
        if (lines.size() > 1)
            actions.add(ActionButton.builder(Component.text("Change order", NamedTextColor.LIGHT_PURPLE))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        show(audience, current -> changeLineOrder(hologram, current));
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
        final var image = ActionButton.builder(Component.text("Image", NamedTextColor.AQUA))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addTextImageLine(hologram, initial, "", "8", note));
                })))
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
                .type(DialogType.multiAction(List.of(add, image)).exitAction(back).build()));
    }

    private static DialogLike addLineType(final Hologram hologram) {
        final var actions = new ArrayList<ActionButton>();
        actions.add(ActionButton.builder(Component.text("Text", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addLine(hologram, "", null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Item", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> addItemLine(hologram, "", null, current));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Block", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addBlockLine(hologram, "", null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Entity", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addEntityLine(hologram, "", null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Paged", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    final var lineIndex = hologram.getLines().toList().size();
                    final var line = hologram.addPagedLine();
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
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
                }))).build();
        final var held = useHeldBlockButton((audience, block) -> {
            hologram.addBlockLine().setBlock(block);
            show(audience, current -> editHologram(hologram, current));
        });
        return blockSearchDialog("Add Block Line", initial, note, List.of(held), back, (audience, block) -> {
            hologram.addBlockLine().setBlock(block);
            show(audience, current -> editHologram(hologram, current));
        });
    }

    private static DialogLike addItemLine(final Hologram hologram, final String initial, @Nullable final Component note, final Audience viewer) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addLineType(hologram));
                }))).build();
        final var actions = new ArrayList<ActionButton>();
        actions.add(ActionButton.builder(Component.text("Player head", NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    final var line = hologram.addItemLine();
                    line.setItemStack(ItemStack.of(Material.PLAYER_HEAD));
                    line.setPlayerHead(true);
                    show(audience, current -> editHologram(hologram, current));
                }))).build());
        final var held = heldItemButton(viewer, "Use Held Item", (audience, item) -> {
            hologram.addItemLine().setItemStack(item);
            show(audience, current -> editHologram(hologram, current));
        });
        if (held != null) actions.add(held);
        return itemSearchDialog("Add Item Line", initial, note, actions, back, (audience, item) -> {
            hologram.addItemLine().setItemStack(item);
            show(audience, current -> editHologram(hologram, current));
        });
    }

    private static DialogLike addEntityLine(final Hologram hologram, final String initial, @Nullable final Component note) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addLineType(hologram));
                }))).build();
        return entitySearchDialog("Add Entity Line", initial, note, back, (audience, entityType) -> {
            hologram.addEntityLine(entityType);
            show(audience, current -> editHologram(hologram, current));
        });
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
            case final ItemHologramLine itemLine -> editItemLine(hologram, lineIndex, itemLine, null, viewer);
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
        final var visual = ActionButton.builder(Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editTextLineVisuals(hologram, lineIndex, line, current));
                }))).width(300).build();

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
                .type(DialogType.multiAction(List.of(save, visual, remove)).columns(1).exitAction(back).build()));
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
        final var visual = ActionButton.builder(Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, current));
                }))).width(300).build();
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
                .type(DialogType.multiAction(List.of(save, visual, deletePageButton(hologram, lineIndex, pagedLine, pageIndex)))
                        .columns(1)
                        .exitAction(editPageBackButton(hologram, lineIndex, pagedLine))
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
        final var held = useHeldBlockButton((audience, block) -> {
            page.setBlock(block);
            show(audience, current -> editPagedLine(hologram, lineIndex, pagedLine, current));
        });
        final var visual = ActionButton.builder(Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editBlockPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
                }))).build();
        final var delete = deletePageButton(hologram, lineIndex, pagedLine, pageIndex, false);
        return blockSearchDialog("Page " + (pageIndex + 1), page.getBlock().getMaterial().key().asString(), note,
                List.of(held, visual, delete), editPageBackButton(hologram, lineIndex, pagedLine), (audience, block) -> {
                    page.setBlock(block);
                    show(audience, current -> editPagedLine(hologram, lineIndex, pagedLine, current));
                });
    }

    private static DialogLike editEntityPage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final EntityHologramLine page,
            @Nullable final Component note
    ) {
        final var visual = ActionButton.builder(Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editEntityPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
                }))).build();
        return entitySearchDialog("Page " + (pageIndex + 1), page.getEntityType().key().asString(), note,
                editPageBackButton(hologram, lineIndex, pagedLine), (audience, entityType) -> {
                    page.setEntityType(entityType);
                    show(audience, current -> editPagedLine(hologram, lineIndex, pagedLine, current));
                }, visual, deletePageButton(hologram, lineIndex, pagedLine, pageIndex, false));
    }

    private static DialogLike editItemPage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final ItemHologramLine page,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var actions = new ArrayList<ActionButton>();
        final var setHeld = heldItemButton(viewer, "Set to Held Item", (audience, item) -> {
            page.setItemStack(item);
            show(audience, current -> editPagedLine(hologram, lineIndex, pagedLine, current));
        });
        if (setHeld != null) actions.add(setHeld);
        final var visual = ActionButton.builder(Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editItemPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
                }))).build();
        final var playerHead = ActionButton.builder(Component.text(
                        "Player head: " + (page.isPlayerHead() ? "On" : "Off"),
                        page.isPlayerHead() ? NamedTextColor.GREEN : NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    page.setPlayerHead(!page.isPlayerHead());
                    show(audience, current -> editItemPage(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
                }))).build();
        actions.add(visual);
        actions.add(playerHead);
        final var delete = deletePageButton(hologram, lineIndex, pagedLine, pageIndex, false);
        actions.add(delete);
        return itemSearchDialog("Page " + (pageIndex + 1), page.getItemStack().getType().key().asString(), note,
                actions, editPageBackButton(hologram, lineIndex, pagedLine), (audience, item) -> {
                    page.setItemStack(item);
                    show(audience, current -> editPagedLine(hologram, lineIndex, pagedLine, current));
                });
    }

    private static DialogLike editTextLineVisuals(
            final Hologram hologram,
            final int lineIndex,
            final TextHologramLine line,
            final Audience viewer
    ) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> editTextLine(hologram, lineIndex, line, line.getUnparsedText().orElse(""), null));
                })))
                .build();

        final var actions = new ArrayList<ActionButton>();
        actions.add(visualGlowButton(hologram, line, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(visualGlowColorButton(hologram, line, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(visualBillboardButton(hologram, line, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(visualOffsetButton(hologram, line, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(visualDisplayScaleButton(line, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(visualBrightnessButton(hologram, line, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(visualFloatButton("View Range", line.getViewRange(), 0.0d, 1000.0d, (audience, value) -> {
            line.setViewRange(value.floatValue());
            show(audience, current -> editTextLineVisuals(hologram, lineIndex, line, current));
        }, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(visualFloatButton("Shadow Radius", line.getShadowRadius(), 0.0d, 1000.0d, (audience, value) -> {
            line.setShadowRadius(value.floatValue());
            show(audience, current -> editTextLineVisuals(hologram, lineIndex, line, current));
        }, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(visualFloatButton("Shadow Strength", line.getShadowStrength(), 0.0d, 10.0d, (audience, value) -> {
            line.setShadowStrength(value.floatValue());
            show(audience, current -> editTextLineVisuals(hologram, lineIndex, line, current));
        }, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(visualFloatButton("Display Width", line.getDisplayWidth(), 0.0d, 4096.0d, (audience, value) -> {
            line.setDisplayWidth(value.floatValue());
            show(audience, current -> editTextLineVisuals(hologram, lineIndex, line, current));
        }, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(visualFloatButton("Display Height", line.getDisplayHeight(), 0.0d, 4096.0d, (audience, value) -> {
            line.setDisplayHeight(value.floatValue());
            show(audience, current -> editTextLineVisuals(hologram, lineIndex, line, current));
        }, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(visualIntButton("Interpolation Delay", line.getInterpolationDelay(), 0, 6000, (audience, value) -> {
            line.setInterpolationDelay(value);
            show(audience, current -> editTextLineVisuals(hologram, lineIndex, line, current));
        }, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(visualIntButton("Interpolation Duration", line.getInterpolationDuration(), 0, 6000, (audience, value) -> {
            line.setInterpolationDuration(value);
            show(audience, current -> editTextLineVisuals(hologram, lineIndex, line, current));
        }, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(visualIntButton("Teleport Duration", line.getTeleportDuration(), 0, 59, (audience, value) -> {
            line.setTeleportDuration(value);
            show(audience, current -> editTextLineVisuals(hologram, lineIndex, line, current));
        }, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(visualBackgroundColorButton("Background Color", line.getBackgroundColor().orElse(null), true, (audience, color) -> {
            line.setBackgroundColor(color);
            show(audience, current -> editTextLineVisuals(hologram, lineIndex, line, current));
        }, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(visualLineWidthButton(line.getLineWidth(), (audience, value) -> {
            line.setLineWidth(value);
            show(audience, current -> editTextLineVisuals(hologram, lineIndex, line, current));
        }, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(visualIntButton("Text Opacity", line.getTextOpacity(), 0, 100, (audience, value) -> {
            line.setTextOpacity(value);
            show(audience, current -> editTextLineVisuals(hologram, lineIndex, line, current));
        }, audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        actions.add(toggleButton("Shadowed", line.isShadowed(), (audience, value) -> {
            line.setShadowed(value);
            show(audience, current -> editTextLineVisuals(hologram, lineIndex, line, current));
        }));
        actions.add(toggleButton("See Through", line.isSeeThrough(), (audience, value) -> {
            line.setSeeThrough(value);
            show(audience, current -> editTextLineVisuals(hologram, lineIndex, line, current));
        }));
        actions.add(toggleButton("Default Background", line.isDefaultBackground(), (audience, value) -> {
            line.setDefaultBackground(value);
            show(audience, current -> editTextLineVisuals(hologram, lineIndex, line, current));
        }));
        actions.add(enumButton("Alignment", line.getAlignment(), TextAlignment.class, line::setAlignment,
                audience -> editTextLineVisuals(hologram, lineIndex, line, audience)));
        return visualOptionsDialog("Visual Options", lineLabel(lineIndex, line), null, actions, back);
    }

    private static DialogLike editTextPageVisuals(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final TextHologramLine page,
            final Audience viewer
    ) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> editTextPage(hologram, lineIndex, pagedLine, pageIndex, page, null));
                })))
                .build();

        final var actions = new ArrayList<ActionButton>();
        actions.add(visualGlowButton(hologram, page, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(visualGlowColorButton(hologram, page, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(visualBillboardButton(hologram, page, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(visualOffsetButton(hologram, page, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(visualDisplayScaleButton(page, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(visualBrightnessButton(hologram, page, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(visualFloatButton("View Range", page.getViewRange(), 0.0d, 1000.0d, (audience, value) -> {
            page.setViewRange(value.floatValue());
            show(audience, current -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(visualFloatButton("Shadow Radius", page.getShadowRadius(), 0.0d, 1000.0d, (audience, value) -> {
            page.setShadowRadius(value.floatValue());
            show(audience, current -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(visualFloatButton("Shadow Strength", page.getShadowStrength(), 0.0d, 10.0d, (audience, value) -> {
            page.setShadowStrength(value.floatValue());
            show(audience, current -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(visualFloatButton("Display Width", page.getDisplayWidth(), 0.0d, 4096.0d, (audience, value) -> {
            page.setDisplayWidth(value.floatValue());
            show(audience, current -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(visualFloatButton("Display Height", page.getDisplayHeight(), 0.0d, 4096.0d, (audience, value) -> {
            page.setDisplayHeight(value.floatValue());
            show(audience, current -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(visualIntButton("Interpolation Delay", page.getInterpolationDelay(), 0, 6000, (audience, value) -> {
            page.setInterpolationDelay(value);
            show(audience, current -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(visualIntButton("Interpolation Duration", page.getInterpolationDuration(), 0, 6000, (audience, value) -> {
            page.setInterpolationDuration(value);
            show(audience, current -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(visualIntButton("Teleport Duration", page.getTeleportDuration(), 0, 59, (audience, value) -> {
            page.setTeleportDuration(value);
            show(audience, current -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(visualBackgroundColorButton("Background Color", page.getBackgroundColor().orElse(null), true, (audience, color) -> {
            page.setBackgroundColor(color);
            show(audience, current -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(visualLineWidthButton(page.getLineWidth(), (audience, value) -> {
            page.setLineWidth(value);
            show(audience, current -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(visualIntButton("Text Opacity", page.getTextOpacity(), 0, 100, (audience, value) -> {
            page.setTextOpacity(value);
            show(audience, current -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }, audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        actions.add(toggleButton("Shadowed", page.isShadowed(), (audience, value) -> {
            page.setShadowed(value);
            show(audience, current -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }));
        actions.add(toggleButton("See Through", page.isSeeThrough(), (audience, value) -> {
            page.setSeeThrough(value);
            show(audience, current -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }));
        actions.add(toggleButton("Default Background", page.isDefaultBackground(), (audience, value) -> {
            page.setDefaultBackground(value);
            show(audience, current -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, current));
        }));
        actions.add(enumButton("Alignment", page.getAlignment(), TextAlignment.class, page::setAlignment,
                audience -> editTextPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, audience)));
        return visualOptionsDialog("Visual Options", Component.text("Page " + (pageIndex + 1)), null, actions, back);
    }

    private static DialogLike editItemLineVisuals(
            final Hologram hologram,
            final int lineIndex,
            final ItemHologramLine line,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> editItemLine(hologram, lineIndex, line, note, viewer));
                })))
                .build();

        final var actions = new ArrayList<ActionButton>();
        final var held = heldItemButton(viewer, "Set to Held Item", (audience, item) -> {
            line.setItemStack(item);
            show(audience, current -> editItemLineVisuals(hologram, lineIndex, line, note, current));
        });
        if (held != null) actions.add(held);
        actions.add(toggleButton("Player Head", line.isPlayerHead(), (audience, value) -> {
            line.setPlayerHead(value);
            show(audience, current -> editItemLineVisuals(hologram, lineIndex, line, note, current));
        }));
        actions.add(enumButton("Item Display", line.getItemDisplayTransform(), ItemDisplayTransform.class, line::setItemDisplayTransform,
                audience -> editItemLineVisuals(hologram, lineIndex, line, note, audience)));
        actions.add(visualGlowButton(hologram, line, audience -> editItemLineVisuals(hologram, lineIndex, line, note, audience)));
        actions.add(visualGlowColorButton(hologram, line, audience -> editItemLineVisuals(hologram, lineIndex, line, note, audience)));
        actions.add(visualBillboardButton(hologram, line, audience -> editItemLineVisuals(hologram, lineIndex, line, note, audience)));
        actions.add(visualOffsetButton(hologram, line, audience -> editItemLineVisuals(hologram, lineIndex, line, note, audience)));
        actions.add(visualDisplayScaleButton(line, audience -> editItemLineVisuals(hologram, lineIndex, line, note, audience)));
        actions.add(visualBrightnessButton(hologram, line, audience -> editItemLineVisuals(hologram, lineIndex, line, note, audience)));
        return visualOptionsDialog("Visual Options", lineLabel(lineIndex, line), note, actions, back);
    }

    private static DialogLike editItemPageVisuals(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final ItemHologramLine page,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editItemPage(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
                })))
                .build();

        final var actions = new ArrayList<ActionButton>();
        final var held = heldItemButton(viewer, "Set to Held Item", (audience, item) -> {
            page.setItemStack(item);
            show(audience, current -> editItemPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
        });
        if (held != null) actions.add(held);
        actions.add(toggleButton("Player Head", page.isPlayerHead(), (audience, value) -> {
            page.setPlayerHead(value);
            show(audience, current -> editItemPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
        }));
        actions.add(enumButton("Item Display", page.getItemDisplayTransform(), ItemDisplayTransform.class, page::setItemDisplayTransform,
                audience -> editItemPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(visualGlowButton(hologram, page, audience -> editItemPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(visualGlowColorButton(hologram, page, audience -> editItemPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(visualBillboardButton(hologram, page, audience -> editItemPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(visualOffsetButton(hologram, page, audience -> editItemPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(visualDisplayScaleButton(page, audience -> editItemPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(visualBrightnessButton(hologram, page, audience -> editItemPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        return visualOptionsDialog("Visual Options", Component.text("Page " + (pageIndex + 1)), note, actions, back);
    }

    private static DialogLike editBlockLineVisuals(
            final Hologram hologram,
            final int lineIndex,
            final BlockHologramLine line,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> editBlockLine(hologram, lineIndex, line, note));
                })))
                .build();

        final var actions = new ArrayList<ActionButton>();
        final var held = useHeldBlockButton((audience, block) -> {
            line.setBlock(block);
            show(audience, current -> editBlockLineVisuals(hologram, lineIndex, line, note, current));
        });
        if (held != null) actions.add(held);
        actions.add(visualGlowButton(hologram, line, audience -> editBlockLineVisuals(hologram, lineIndex, line, note, audience)));
        actions.add(visualGlowColorButton(hologram, line, audience -> editBlockLineVisuals(hologram, lineIndex, line, note, audience)));
        actions.add(visualBillboardButton(hologram, line, audience -> editBlockLineVisuals(hologram, lineIndex, line, note, audience)));
        actions.add(visualOffsetButton(hologram, line, audience -> editBlockLineVisuals(hologram, lineIndex, line, note, audience)));
        actions.add(visualDisplayScaleButton(line, audience -> editBlockLineVisuals(hologram, lineIndex, line, note, audience)));
        actions.add(visualBrightnessButton(hologram, line, audience -> editBlockLineVisuals(hologram, lineIndex, line, note, audience)));
        return visualOptionsDialog("Visual Options", lineLabel(lineIndex, line), note, actions, back);
    }

    private static DialogLike editBlockPageVisuals(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final BlockHologramLine page,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editBlockPage(hologram, lineIndex, pagedLine, pageIndex, page, note));
                })))
                .build();

        final var actions = new ArrayList<ActionButton>();
        final var held = useHeldBlockButton((audience, block) -> {
            page.setBlock(block);
            show(audience, current -> editBlockPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
        });
        if (held != null) actions.add(held);
        actions.add(visualGlowButton(hologram, page, audience -> editBlockPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(visualGlowColorButton(hologram, page, audience -> editBlockPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(visualBillboardButton(hologram, page, audience -> editBlockPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(visualOffsetButton(hologram, page, audience -> editBlockPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(visualDisplayScaleButton(page, audience -> editBlockPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(visualBrightnessButton(hologram, page, audience -> editBlockPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        return visualOptionsDialog("Visual Options", Component.text("Page " + (pageIndex + 1)), note, actions, back);
    }

    private static DialogLike editEntityLineVisuals(
            final Hologram hologram,
            final int lineIndex,
            final EntityHologramLine line,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> editEntityLine(hologram, lineIndex, line, note));
                })))
                .build();

        final var actions = new ArrayList<ActionButton>();
        actions.add(visualGlowButton(hologram, line, audience -> editEntityLineVisuals(hologram, lineIndex, line, note, audience)));
        actions.add(visualGlowColorButton(hologram, line, audience -> editEntityLineVisuals(hologram, lineIndex, line, note, audience)));
        actions.add(visualBillboardButton(hologram, line, audience -> editEntityLineVisuals(hologram, lineIndex, line, note, audience)));
        actions.add(visualOffsetButton(hologram, line, audience -> editEntityLineVisuals(hologram, lineIndex, line, note, audience)));
        actions.add(visualScaleButton(line.getScale(), 0.1d, 100.0d, (audience, value) -> {
            line.setScale(value);
            show(audience, current -> editEntityLineVisuals(hologram, lineIndex, line, note, current));
        }, audience -> editEntityLineVisuals(hologram, lineIndex, line, note, audience)));
        return visualOptionsDialog("Visual Options", lineLabel(lineIndex, line), note, actions, back);
    }

    private static DialogLike editEntityPageVisuals(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine pagedLine,
            final int pageIndex,
            final EntityHologramLine page,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editEntityPage(hologram, lineIndex, pagedLine, pageIndex, page, note));
                })))
                .build();

        final var actions = new ArrayList<ActionButton>();
        actions.add(visualGlowButton(hologram, page, audience -> editEntityPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(visualGlowColorButton(hologram, page, audience -> editEntityPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(visualBillboardButton(hologram, page, audience -> editEntityPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(visualOffsetButton(hologram, page, audience -> editEntityPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        actions.add(visualScaleButton(page.getScale(), 0.1d, 100.0d, (audience, value) -> {
            page.setScale(value);
            show(audience, current -> editEntityPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, current));
        }, audience -> editEntityPageVisuals(hologram, lineIndex, pagedLine, pageIndex, page, note, audience)));
        return visualOptionsDialog("Visual Options", Component.text("Page " + (pageIndex + 1)), note, actions, back);
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

    private static DialogLike changeLineOrder(final Hologram hologram, final Audience viewer) {
        final var swap = ActionButton.builder(Component.text("Swap two lines", NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> selectLineToSwap(hologram, current));
                }))).width(300).build();
        final var move = ActionButton.builder(Component.text("Move line above another", NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> selectLineToMove(hologram, current));
                }))).width(300).build();
        final var back = editHologramBackButton(hologram);

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Change order"))
                        .body(List.of(DialogBody.plainMessage(Component.text("Choose whether to swap two lines or move one line above another"))))
                        .build())
                .type(DialogType.multiAction(List.of(swap, move)).columns(1).exitAction(back).build()));
    }

    private static DialogLike selectLineToSwap(final Hologram hologram, final Audience viewer) {
        return selectLine(hologram, viewer, "Select first line",
                "Select the first line. It will trade places with the second line you choose.", -1,
                audience -> changeLineOrder(hologram, audience),
                lineIndex -> audience -> {
                    show(audience, current -> selectLineSwapTarget(hologram, lineIndex, current));
                });
    }

    private static DialogLike selectLineSwapTarget(final Hologram hologram, final int first, final Audience viewer) {
        return selectLine(hologram, viewer, "Select second line",
                "Select the second line. The two selected lines will swap positions.", first,
                audience -> selectLineToSwap(hologram, audience),
                second -> audience -> {
                    if (second == first) {
                        show(audience, ignored -> selectLineSwapTarget(hologram, first, viewer,
                                Component.text("You cannot swap a line with itself", NamedTextColor.RED)));
                        return;
                    }
                    hologram.swapLines(first, second);
                    show(audience, current -> editHologram(hologram, current));
                });
    }

    private static DialogLike selectLineToMove(final Hologram hologram, final Audience viewer) {
        return selectLine(hologram, viewer, "Select line to move",
                "Select the line that should be moved.", -1,
                audience -> changeLineOrder(hologram, audience),
                lineIndex -> audience -> {
                    show(audience, current -> selectLineMoveTarget(hologram, lineIndex, current));
                });
    }

    private static DialogLike selectLineMoveTarget(final Hologram hologram, final int from, final Audience viewer) {
        return selectLine(hologram, viewer, "Move above line",
                "Select the target line. The moved line will be placed directly above it.", from,
                audience -> selectLineToMove(hologram, audience),
                target -> audience -> {
                    if (target == from) {
                        show(audience, ignored -> selectLineMoveTarget(hologram, from, viewer,
                                Component.text("You cannot move a line above itself", NamedTextColor.RED)));
                        return;
                    }
                    hologram.moveLine(from, from < target ? target - 1 : target);
                    show(audience, current -> editHologram(hologram, current));
                });
    }

    private static DialogLike selectLineSwapTarget(
            final Hologram hologram,
            final int first,
            final Audience viewer,
            @Nullable final Component note
    ) {
        return selectLine(hologram, viewer, "Select second line",
                "Select the second line. The two selected lines will swap positions.", first,
                audience -> selectLineToSwap(hologram, audience), note,
                second -> audience -> {
                    if (second == first) {
                        show(audience, ignored -> selectLineSwapTarget(hologram, first, viewer,
                                Component.text("You cannot swap a line with itself", NamedTextColor.RED)));
                        return;
                    }
                    hologram.swapLines(first, second);
                    show(audience, current -> editHologram(hologram, current));
                });
    }

    private static DialogLike selectLineMoveTarget(
            final Hologram hologram,
            final int from,
            final Audience viewer,
            @Nullable final Component note
    ) {
        return selectLine(hologram, viewer, "Move above line",
                "Select the target line. The moved line will be placed directly above it.", from,
                audience -> selectLineToMove(hologram, audience), note,
                target -> audience -> {
                    if (target == from) {
                        show(audience, ignored -> selectLineMoveTarget(hologram, from, viewer,
                                Component.text("You cannot move a line above itself", NamedTextColor.RED)));
                        return;
                    }
                    hologram.moveLine(from, from < target ? target - 1 : target);
                    show(audience, current -> editHologram(hologram, current));
                });
    }

    private static DialogLike selectLine(
            final Hologram hologram,
            final Audience viewer,
            final String title,
            final String description,
            final int excludedIndex,
            final Function<Audience, DialogLike> backDialog,
            final Function<Integer, java.util.function.Consumer<Audience>> selection
    ) {
        return selectLine(hologram, viewer, title, description, excludedIndex, backDialog, null, selection);
    }

    private static DialogLike selectLine(
            final Hologram hologram,
            final Audience viewer,
            final String title,
            final String description,
            final int excludedIndex,
            final Function<Audience, DialogLike> backDialog,
            @Nullable final Component note,
            final Function<Integer, java.util.function.Consumer<Audience>> selection
    ) {
        final var lines = hologram.getLines().toList();
        final var actions = new ArrayList<ActionButton>();
        for (var index = 0; index < lines.size(); index++) {
            final var line = lines.get(index);
            final var lineIndex = index;
            actions.add(ActionButton.builder(index == excludedIndex
                            ? lineLabel(lineIndex, line).color(NamedTextColor.GOLD)
                            : lineLabel(lineIndex, line))
                    .tooltip(linePreview(line, viewer))
                    .action(DialogAction.staticAction(ClickEvent.callback(selection.apply(lineIndex)::accept)))
                    .width(300)
                    .build());
        }
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, backDialog);
                }))).width(300).build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(title))
                        .body(bodyLines(description, note))
                        .build())
                .type(DialogType.multiAction(actions).columns(1).exitAction(back).build()));
    }

    private static DialogLike editBlockLine(
            final Hologram hologram,
            final int lineIndex,
            final BlockHologramLine line,
            @Nullable final Component note
    ) {
        final var held = useHeldBlockButton((audience, block) -> {
            line.setBlock(block);
            show(audience, current -> editHologram(hologram, current));
        });
        final var remove = deleteLineButton(hologram, lineIndex, false);
        final var visual = ActionButton.builder(Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editBlockLineVisuals(hologram, lineIndex, line, note, current));
                }))).build();
        final var back = editHologramBackButton(hologram);
        return blockSearchDialog(lineLabel(lineIndex, line), line.getBlock().getMaterial().key().asString(), note,
                List.of(held, visual, remove), back, (audience, block) -> {
                    line.setBlock(block);
                    show(audience, current -> editHologram(hologram, current));
                });
    }

    private static DialogLike editEntityLine(
            final Hologram hologram,
            final int lineIndex,
            final EntityHologramLine line,
            @Nullable final Component note
    ) {
        final var remove = deleteLineButton(hologram, lineIndex, false);
        final var visual = ActionButton.builder(Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editEntityLineVisuals(hologram, lineIndex, line, note, current));
                }))).build();
        final var back = editHologramBackButton(hologram);
        return entitySearchDialog(lineLabel(lineIndex, line), line.getEntityType().key().asString(), note, back,
                (audience, entityType) -> {
                    line.setEntityType(entityType);
                    show(audience, current -> editHologram(hologram, current));
                }, visual, remove);
    }

    private static DialogLike editItemLine(
            final Hologram hologram,
            final int lineIndex,
            final ItemHologramLine line,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var actions = new ArrayList<ActionButton>();
        final var setHeld = heldItemButton(viewer, "Set to Held Item", (audience, item) -> {
            line.setItemStack(item);
            show(audience, current -> editHologram(hologram, current));
        });
        if (setHeld != null) actions.add(setHeld);
        final var visual = ActionButton.builder(Component.text("Visual Options", NamedTextColor.LIGHT_PURPLE))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editItemLineVisuals(hologram, lineIndex, line, note, current));
                }))).build();
        final var playerHead = ActionButton.builder(Component.text(
                        "Player head: " + (line.isPlayerHead() ? "On" : "Off"),
                        line.isPlayerHead() ? NamedTextColor.GREEN : NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    line.setPlayerHead(!line.isPlayerHead());
                    show(audience, current -> editItemLine(hologram, lineIndex, line, note, current));
                }))).build();
        final var remove = deleteLineButton(hologram, lineIndex, false);
        actions.add(visual);
        actions.add(playerHead);
        actions.add(remove);
        final var back = editHologramBackButton(hologram);
        return itemSearchDialog(lineLabel(lineIndex, line), line.getItemStack().getType().key().asString(), note,
                actions, back, (audience, item) -> {
                    line.setItemStack(item);
                    show(audience, current -> editHologram(hologram, current));
                });
    }

    private static ActionButton deleteLineButton(final Hologram hologram, final int lineIndex) {
        return deleteLineButton(hologram, lineIndex, true);
    }

    private static ActionButton deleteLineButton(final Hologram hologram, final int lineIndex, final boolean customWidth) {
        final var builder = ActionButton.builder(Component.text("Delete this line", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(deleteLine(hologram, lineIndex, audience));
                })));
        return customWidth ? builder.width(300).build() : builder.build();
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
        if (pages.size() > 1)
            actions.add(ActionButton.builder(Component.text("Change order", NamedTextColor.LIGHT_PURPLE))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        show(audience, current -> changePageOrder(hologram, lineIndex, line, current));
                    }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Page Settings", NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> editPageSettings(hologram, lineIndex, line, null, null));
                }))).width(300).build());
        actions.add(deleteLineButton(hologram, lineIndex));
        actions.add(editHologramBackButton(hologram));

        final var body = new ArrayList<DialogBody>();
        if (pages.isEmpty()) body.add(DialogBody.plainMessage(Component.text("No pages have been added yet")));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(lineLabel(lineIndex, line)).body(body).build())
                .type(DialogType.multiAction(actions).columns(1).build()));
    }

    private static DialogLike editPageSettings(final Hologram hologram, final int lineIndex, final PagedHologramLine line) {
        return editPageSettings(hologram, lineIndex, line, null, null);
    }

    private static DialogLike editPageSettings(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            @Nullable final Component note,
            @Nullable final String intervalInput
    ) {
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("interval");
                    final var interval = parseDuration(input);
                    if (interval.error() != null) {
                        show(audience, ignored -> editPageSettings(hologram, lineIndex, line,
                                Component.text(interval.error(), NamedTextColor.RED), input));
                        return;
                    }

                    line.setInterval(interval.value());
                    final var random = response.getBoolean("random");
                    if (random != null) line.setRandomOrder(random);
                    final var paused = response.getBoolean("paused");
                    if (paused != null) line.setPaused(paused);
                    show(audience, ignored -> editPageSettings(hologram, lineIndex, line, null, null));
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();

        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                }))).build();

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Current interval: " + formatIntervalInput(line.getInterval()))));
        body.add(DialogBody.plainMessage(Component.text("Allowed units are millis (ms), seconds (s), minutes (m), and hours (h)")));
        body.add(DialogBody.plainMessage(Component.text("Numbers without a unit are seconds")));
        if (note != null) body.add(DialogBody.plainMessage(note));

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Page Settings"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("interval", Component.text("Time between cycles"))
                                        .initial(intervalInput != null ? intervalInput : formatIntervalInput(line.getInterval()))
                                        .build(),
                                DialogInput.bool("random", Component.text("Random Order"))
                                        .initial(line.isRandomOrder())
                                        .build(),
                                DialogInput.bool("paused", Component.text("Paused"))
                                        .initial(line.isPaused())
                                        .build()
                        ))
                        .build())
                .type(DialogType.confirmation(save, back)));
    }

    private static DialogLike addPageType(final Hologram hologram, final int lineIndex, final PagedHologramLine line) {
        final var actions = new ArrayList<ActionButton>();
        actions.add(ActionButton.builder(Component.text("Text", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addTextPage(hologram, lineIndex, line, "", null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Item", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> addItemPage(hologram, lineIndex, line, "", null, current));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Block", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addBlockPage(hologram, lineIndex, line, "", null));
                }))).width(300).build());
        actions.add(ActionButton.builder(Component.text("Entity", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addEntityPage(hologram, lineIndex, line, "", null));
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
                    editItemPage(hologram, lineIndex, pagedLine, pageIndex, itemLine, null, viewer);
            case final EntityHologramLine entityLine ->
                    editEntityPage(hologram, lineIndex, pagedLine, pageIndex, entityLine, null);
            default -> editPagedLine(hologram, lineIndex, pagedLine, viewer);
        };
    }

    private static ActionButton deletePageButton(final Hologram hologram, final int lineIndex, final PagedHologramLine line, final int pageIndex) {
        return deletePageButton(hologram, lineIndex, line, pageIndex, true);
    }

    private static ActionButton deletePageButton(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final int pageIndex,
            final boolean customWidth
    ) {
        final var builder = ActionButton.builder(Component.text("Delete this page", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(deletePage(hologram, lineIndex, line, pageIndex));
                })));
        return customWidth ? builder.width(300).build() : builder.build();
    }

    private static DialogLike deletePage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final int pageIndex
    ) {
        final var confirm = ActionButton.builder(Component.text("Delete this page", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    line.removePage(pageIndex);
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                }))).build();

        final var cancel = ActionButton.builder(Component.text("Cancel"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editPage(hologram, lineIndex, line, pageIndex, current));
                }))).build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Delete page " + (pageIndex + 1) + "?"))
                        .body(List.of(DialogBody.plainMessage(Component.text("This cannot be undone"))))
                        .build())
                .type(DialogType.confirmation(confirm, cancel)));
    }

    private static DialogLike changePageOrder(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final Audience viewer
    ) {
        final var swap = ActionButton.builder(Component.text("Swap two pages", NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> selectPageToSwap(hologram, lineIndex, line, current));
                }))).width(300).build();
        final var move = ActionButton.builder(Component.text("Move page above another", NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> selectPageToMove(hologram, lineIndex, line, current));
                }))).width(300).build();
        final var back = editPageBackButton(hologram, lineIndex, line);

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Change order"))
                        .body(List.of(DialogBody.plainMessage(Component.text("Choose whether to swap two pages or move one page above another"))))
                        .build())
                .type(DialogType.multiAction(List.of(swap, move)).columns(1).exitAction(back).build()));
    }

    private static DialogLike selectPageToSwap(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final Audience viewer
    ) {
        return selectPage(hologram, lineIndex, line, viewer, "Select first page",
                "Select the first page. It will trade places with the second page you choose.", -1,
                audience -> changePageOrder(hologram, lineIndex, line, audience),
                pageIndex -> audience -> {
                    show(audience, current -> selectPageSwapTarget(hologram, lineIndex, line, pageIndex, current));
                });
    }

    private static DialogLike selectPageSwapTarget(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final int first,
            final Audience viewer
    ) {
        return selectPage(hologram, lineIndex, line, viewer, "Select second page",
                "Select the second page. The two selected pages will swap positions.", first,
                audience -> selectPageToSwap(hologram, lineIndex, line, audience),
                second -> audience -> {
                    if (second == first) {
                        show(audience, ignored -> selectPageSwapTarget(hologram, lineIndex, line, first, viewer,
                                Component.text("You cannot swap a page with itself", NamedTextColor.RED)));
                        return;
                    }
                    line.swapPages(first, second);
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                });
    }

    private static DialogLike selectPageToMove(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final Audience viewer
    ) {
        return selectPage(hologram, lineIndex, line, viewer, "Select page to move",
                "Select the page that should be moved.", -1,
                audience -> changePageOrder(hologram, lineIndex, line, audience),
                pageIndex -> audience -> {
                    show(audience, current -> selectPageMoveTarget(hologram, lineIndex, line, pageIndex, current));
                });
    }

    private static DialogLike selectPageMoveTarget(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final int from,
            final Audience viewer
    ) {
        return selectPage(hologram, lineIndex, line, viewer, "Move above page",
                "Select the target page. The moved page will be placed directly above it.", from,
                audience -> selectPageToMove(hologram, lineIndex, line, audience),
                target -> audience -> {
                    if (target == from) {
                        show(audience, ignored -> selectPageMoveTarget(hologram, lineIndex, line, from, viewer,
                                Component.text("You cannot move a page above itself", NamedTextColor.RED)));
                        return;
                    }
                    line.movePage(from, from < target ? target - 1 : target);
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                });
    }

    private static DialogLike selectPageSwapTarget(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final int first,
            final Audience viewer,
            @Nullable final Component note
    ) {
        return selectPage(hologram, lineIndex, line, viewer, "Select second page",
                "Select the second page. The two selected pages will swap positions.", first,
                audience -> selectPageToSwap(hologram, lineIndex, line, audience), note,
                second -> audience -> {
                    if (second == first) {
                        show(audience, ignored -> selectPageSwapTarget(hologram, lineIndex, line, first, viewer,
                                Component.text("You cannot swap a page with itself", NamedTextColor.RED)));
                        return;
                    }
                    line.swapPages(first, second);
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                });
    }

    private static DialogLike selectPageMoveTarget(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final int from,
            final Audience viewer,
            @Nullable final Component note
    ) {
        return selectPage(hologram, lineIndex, line, viewer, "Move above page",
                "Select the target page. The moved page will be placed directly above it.", from,
                audience -> selectPageToMove(hologram, lineIndex, line, audience), note,
                target -> audience -> {
                    if (target == from) {
                        show(audience, ignored -> selectPageMoveTarget(hologram, lineIndex, line, from, viewer,
                                Component.text("You cannot move a page above itself", NamedTextColor.RED)));
                        return;
                    }
                    line.movePage(from, from < target ? target - 1 : target);
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                });
    }

    private static DialogLike selectPage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final Audience viewer,
            final String title,
            final String description,
            final int excludedIndex,
            final Function<Audience, DialogLike> backDialog,
            final Function<Integer, java.util.function.Consumer<Audience>> selection
    ) {
        return selectPage(hologram, lineIndex, line, viewer, title, description, excludedIndex, backDialog, null, selection);
    }

    private static DialogLike selectPage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final Audience viewer,
            final String title,
            final String description,
            final int excludedIndex,
            final Function<Audience, DialogLike> backDialog,
            @Nullable final Component note,
            final Function<Integer, java.util.function.Consumer<Audience>> selection
    ) {
        final var pages = line.getPages().toList();
        final var actions = new ArrayList<ActionButton>();
        for (var index = 0; index < pages.size(); index++) {
            final var page = pages.get(index);
            final var pageIndex = index;
            actions.add(ActionButton.builder(index == excludedIndex
                            ? Component.text("Page " + (pageIndex + 1) + ": " + lineDescription(page), NamedTextColor.GOLD)
                            : Component.text("Page " + (pageIndex + 1) + ": " + lineDescription(page)))
                    .tooltip(linePreview(page, viewer))
                    .action(DialogAction.staticAction(ClickEvent.callback(selection.apply(pageIndex)::accept)))
                    .width(300)
                    .build());
        }
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, backDialog);
                }))).width(300).build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(title))
                        .body(bodyLines(description, note))
                        .build())
                .type(DialogType.multiAction(actions).columns(1).exitAction(back).build()));
    }

    private static List<DialogBody> bodyLines(final String description, @Nullable final Component note) {
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text(description)));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return body;
    }

    private static ActionButton editPagedBackButton(final Hologram hologram, final int lineIndex, final PagedHologramLine line) {
        return ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addPageType(hologram, lineIndex, line));
                }))).width(300).build();
    }

    private static ActionButton editPageBackButton(final Hologram hologram, final int lineIndex, final PagedHologramLine line) {
        return ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
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
        final var image = ActionButton.builder(Component.text("Image", NamedTextColor.AQUA))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addTextImagePage(hologram, lineIndex, line, initial, "", "8", note));
                })))
                .build();
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
                .type(DialogType.multiAction(List.of(add, image)).exitAction(editPagedBackButton(hologram, lineIndex, line)).build()));
    }

    private static DialogLike addItemPage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final String initial,
            @Nullable final Component note,
            final Audience viewer
    ) {
        final var actions = new ArrayList<ActionButton>();
        actions.add(ActionButton.builder(Component.text("Player head", NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    final var created = line.addItemPage();
                    created.setItemStack(ItemStack.of(Material.PLAYER_HEAD));
                    created.setPlayerHead(true);
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                }))).build());
        final var held = heldItemButton(viewer, "Use Held Item", (audience, item) -> {
            line.addItemPage().setItemStack(item);
            show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
        });
        if (held != null) actions.add(held);
        return itemSearchDialog("Add Item Page", initial, note, actions, editPagedBackButton(hologram, lineIndex, line),
                (audience, item) -> {
                    line.addItemPage().setItemStack(item);
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                });
    }

    private static DialogLike addTextImageLine(
            final Hologram hologram,
            final String textInitial,
            final String sourceInitial,
            final String sizeInitial,
            @Nullable final Component note
    ) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addLine(hologram, textInitial, note));
                })))
                .width(150)
                .build();

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter the URL or file path for the image")));
        if (note != null) body.add(DialogBody.plainMessage(note));

        final var add = ActionButton.builder(Component.text("Add"))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("source");
                    final var source = input != null ? input.trim() : null;
                    if (source == null || source.isBlank()) {
                        show(audience, ignored -> addTextImageLine(hologram, textInitial, "", sizeInitial, Component.text("Image source cannot be empty", NamedTextColor.RED)));
                        return;
                    }

                    final var size = response.getText("size");
                    final var height = parseImageHeight(size);
                    if (height.error() != null) {
                        show(audience, ignored -> addTextImageLine(hologram, textInitial, source, size != null ? size : sizeInitial,
                                Component.text(height.error(), NamedTextColor.RED)));
                        return;
                    }

                    final var image = parseImageSource(source);
                    if (image.error() != null) {
                        show(audience, ignored -> addTextImageLine(hologram, textInitial, source, size != null ? size : sizeInitial,
                                Component.text(image.error(), NamedTextColor.RED)));
                        return;
                    }

                    hologram.addTextLine().setUnparsedText(imageTag(source, height.value()));
                    show(audience, current -> editHologram(hologram, current));
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Image Line"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("source", Component.text("Image source"))
                                        .initial(sourceInitial)
                                        .maxLength(8192)
                                        .build(),
                                DialogInput.text("size", Component.text("Image size"))
                                        .initial(sizeInitial)
                                        .build()
                        )).build())
                .type(DialogType.multiAction(List.of(add)).exitAction(back).build()));
    }

    private static DialogLike addTextImagePage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final String textInitial,
            final String sourceInitial,
            final String sizeInitial,
            @Nullable final Component note
    ) {
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> addTextPage(hologram, lineIndex, line, textInitial, note));
                })))
                .width(150)
                .build();

        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter the URL or file path for the image")));
        if (note != null) body.add(DialogBody.plainMessage(note));

        final var add = ActionButton.builder(Component.text("Add"))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("source");
                    final var source = input != null ? input.trim() : null;
                    if (source == null || source.isBlank()) {
                        show(audience, ignored -> addTextImagePage(hologram, lineIndex, line, textInitial, "", sizeInitial, Component.text("Image source cannot be empty", NamedTextColor.RED)));
                        return;
                    }

                    final var size = response.getText("size");
                    final var height = parseImageHeight(size);
                    if (height.error() != null) {
                        show(audience, ignored -> addTextImagePage(hologram, lineIndex, line, textInitial, source, size != null ? size : sizeInitial,
                                Component.text(height.error(), NamedTextColor.RED)));
                        return;
                    }

                    final var image = parseImageSource(source);
                    if (image.error() != null) {
                        show(audience, ignored -> addTextImagePage(hologram, lineIndex, line, textInitial, source, size != null ? size : sizeInitial,
                                Component.text(image.error(), NamedTextColor.RED)));
                        return;
                    }

                    line.addTextPage().setUnparsedText(imageTag(source, height.value()));
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Add Image Page"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("source", Component.text("Image source"))
                                        .initial(sourceInitial)
                                        .maxLength(8192)
                                        .build(),
                                DialogInput.text("size", Component.text("Image size"))
                                        .initial(sizeInitial)
                                        .build()
                        )).build())
                .type(DialogType.multiAction(List.of(add)).exitAction(back).build()));
    }

    private static ActionButton toggleButton(
            final String label,
            final boolean current,
            final BiConsumer<Audience, Boolean> setter
    ) {
        return ActionButton.builder(Component.text(label + ": " + (current ? "On" : "Off"), current ? NamedTextColor.GREEN : NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> setter.accept(audience, !current))))
                .build();
    }

    private static ActionButton visualGlowButton(
            final Hologram hologram,
            final StaticHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        return toggleButton("Glowing", line.isGlowing(), (audience, value) -> {
            line.setGlowing(value);
            show(audience, reopen);
        });
    }

    private static ActionButton visualGlowColorButton(
            final Hologram hologram,
            final StaticHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        final var current = line.getGlowColor().orElse(null);
        final var label = Component.text("Glow Color: ")
                .append(Component.text(describeTextColor(current), current != null ? current : NamedTextColor.WHITE));
        return ActionButton.builder(label)
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> editGlowColorDialog(current, null, null, (target, color) -> {
                        line.setGlowColor(color);
                        show(target, reopen);
                    }, reopen));
                })))
                .build();
    }

    private static ActionButton visualBillboardButton(
            final Hologram hologram,
            final StaticHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        return ActionButton.builder(Component.text("Billboard: " + line.getBillboard().name(), NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editBillboardDialog(line, reopen));
                })))
                .build();
    }

    private static ActionButton visualBrightnessButton(
            final Hologram hologram,
            final DisplayHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        return ActionButton.builder(Component.text("Brightness"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editBrightnessDialog(line, reopen));
                })))
                .build();
    }

    private static ActionButton visualOffsetButton(
            final Hologram hologram,
            final StaticHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        final var offset = line.getOffset();
        return ActionButton.builder(Component.text("Offset: " + formatDecimal(offset.x()) + ", " + formatDecimal(offset.y()) + ", " + formatDecimal(offset.z())))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, current -> editOffsetDialog(line, reopen));
                })))
                .build();
    }

    private static ActionButton visualIntButton(
            final String label,
            final int current,
            final int min,
            final int max,
            final BiConsumer<Audience, Integer> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        return ActionButton.builder(Component.text(label + ": " + current))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, currentDialog -> editIntValueDialog(label, current, min, max, null, setter, reopen));
                })))
                .build();
    }

    private static ActionButton visualLineWidthButton(
            final int current,
            final BiConsumer<Audience, Integer> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var text = current == Integer.MAX_VALUE ? "default" : Integer.toString(current);
        return ActionButton.builder(Component.text("Line Width: " + text))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> editLineWidthDialog(current, setter, reopen));
                })))
                .build();
    }

    private static ActionButton visualFloatButton(
            final String label,
            final double current,
            final double min,
            final double max,
            final BiConsumer<Audience, Double> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        return ActionButton.builder(Component.text(label + ": " + formatDecimal(current)))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, currentDialog -> editFloatValueDialog(label, current, min, max, null, setter, reopen));
                })))
                .build();
    }

    private static ActionButton visualScaleButton(
            final double current,
            final double min,
            final double max,
            final BiConsumer<Audience, Double> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        return ActionButton.builder(Component.text("Scale: " + formatDecimal(current)))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, currentDialog -> editScaleDialog(current, min, max, null, setter, reopen));
                })))
                .build();
    }

    private static ActionButton visualDisplayScaleButton(
            final DisplayHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        final var scale = line.getTransformation().getScale();
        return ActionButton.builder(Component.text("Scale: " + formatDecimal(scale.x()) + ", " + formatDecimal(scale.y()) + ", " + formatDecimal(scale.z())))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> editDisplayScaleDialog(line, null, reopen));
                })))
                .build();
    }

    private static ActionButton visualTextColorButton(
            final String label,
            @Nullable final TextColor current,
            final boolean allowReset,
            final BiConsumer<Audience, @Nullable TextColor> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var text = current != null ? "#" + Integer.toHexString(current.value()) : "none";
        return ActionButton.builder(Component.text(label + ": " + text))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, currentDialog -> editTextColorDialog(label, current, allowReset ? Component.text("Blank resets the color") : null, setter, reopen));
                })))
                .build();
    }

    private static String describeTextColor(@Nullable final TextColor color) {
        if (color == null) return "None";
        if (color == NamedTextColor.BLACK) return "Black";
        if (color == NamedTextColor.DARK_BLUE) return "Dark Blue";
        if (color == NamedTextColor.DARK_GREEN) return "Dark Green";
        if (color == NamedTextColor.DARK_AQUA) return "Dark Aqua";
        if (color == NamedTextColor.DARK_RED) return "Dark Red";
        if (color == NamedTextColor.DARK_PURPLE) return "Dark Purple";
        if (color == NamedTextColor.GOLD) return "Gold";
        if (color == NamedTextColor.GRAY) return "Gray";
        if (color == NamedTextColor.DARK_GRAY) return "Dark Gray";
        if (color == NamedTextColor.BLUE) return "Blue";
        if (color == NamedTextColor.GREEN) return "Green";
        if (color == NamedTextColor.AQUA) return "Aqua";
        if (color == NamedTextColor.RED) return "Red";
        if (color == NamedTextColor.LIGHT_PURPLE) return "Light Purple";
        if (color == NamedTextColor.YELLOW) return "Yellow";
        if (color == NamedTextColor.WHITE) return "White";
        return "#" + Integer.toHexString(color.value());
    }

    private static List<NamedTextColor> namedTextColors() {
        return List.of(
                NamedTextColor.BLACK,
                NamedTextColor.DARK_BLUE,
                NamedTextColor.DARK_GREEN,
                NamedTextColor.DARK_AQUA,
                NamedTextColor.DARK_RED,
                NamedTextColor.DARK_PURPLE,
                NamedTextColor.GOLD,
                NamedTextColor.GRAY,
                NamedTextColor.DARK_GRAY,
                NamedTextColor.BLUE,
                NamedTextColor.GREEN,
                NamedTextColor.AQUA,
                NamedTextColor.RED,
                NamedTextColor.LIGHT_PURPLE,
                NamedTextColor.YELLOW,
                NamedTextColor.WHITE
        );
    }

    private static DialogLike editGlowColorDialog(
            @Nullable final TextColor current,
            @Nullable final Component note,
            @Nullable final String currentInput,
            final BiConsumer<Audience, @Nullable TextColor> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var actions = new ArrayList<ActionButton>();
        for (final var color : namedTextColors()) {
            actions.add(ActionButton.builder(Component.text(describeTextColor(color), color))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        setter.accept(audience, color);
                    })))
                    .build());
        }

        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("hex");
                    if (input == null || input.isBlank()) {
                        show(audience, ignored -> editGlowColorDialog(current, Component.text("Hex value cannot be empty", NamedTextColor.RED), input, setter, reopen));
                        return;
                    }
                    final var parsed = parseTextColor(input);
                    if (parsed.error() != null || parsed.value() == null) {
                        show(audience, ignored -> editGlowColorDialog(current, Component.text("Invalid color", NamedTextColor.RED), input, setter, reopen));
                        return;
                    }
                    setter.accept(audience, parsed.value());
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var reset = ActionButton.builder(Component.text("Reset", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    setter.accept(audience, null);
                })))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> show(audience, reopen))))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Pick a named color or enter a hex value")));
        body.add(DialogBody.plainMessage(Component.text("Use Reset to clear the glow color")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        actions.add(save);
        actions.add(reset);
        actions.add(back);
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Glow Color"))
                        .body(body)
                        .inputs(List.of(DialogInput.text("hex", Component.text("Hex value"))
                                .initial(currentInput != null ? currentInput : current != null ? "#" + Integer.toHexString(current.value()) : "")
                                .build()))
                        .build())
                .type(DialogType.multiAction(actions).build()));
    }

    private static <E extends Enum<E>> ActionButton enumButton(
            final String label,
            final E current,
            final Class<E> type,
            final Consumer<E> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        return ActionButton.builder(Component.text(label + ": " + friendlyName(current.name())))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> editEnumDialog(label, current, type, setter, reopen));
                })))
                .build();
    }

    private static DialogLike visualOptionsDialog(
            final String title,
            final Component header,
            @Nullable final Component note,
            final List<ActionButton> actions,
            final ActionButton back
    ) {
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(header));
        body.add(DialogBody.plainMessage(Component.text("Edit how this line looks")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(title))
                        .body(body)
                        .build())
                .type(DialogType.multiAction(addBack(actions, back)).build()));
    }

    private static List<ActionButton> addBack(final List<ActionButton> actions, final ActionButton back) {
        final var buttons = new ArrayList<>(actions);
        buttons.add(back);
        return buttons;
    }

    private static <E extends Enum<E>> DialogLike editEnumDialog(
            final String title,
            final E current,
            final Class<E> type,
            final Consumer<E> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var actions = new ArrayList<ActionButton>();
        for (final var value : type.getEnumConstants()) {
            actions.add(ActionButton.builder(Component.text(friendlyName(value.name()),
                            value == current ? NamedTextColor.GREEN : NamedTextColor.WHITE))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        setter.accept(value);
                        show(audience, reopen);
                    })))
                    .build());
        }
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> show(audience, reopen))))
                .build();
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(title))
                        .body(List.of(DialogBody.plainMessage(Component.text("Choose a value"))))
                        .build())
                .type(DialogType.multiAction(actions).exitAction(back).build()));
    }

    private static ActionButton visualBackgroundColorButton(
            final String label,
            @Nullable final Color current,
            final boolean allowReset,
            final BiConsumer<Audience, @Nullable Color> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var text = current != null ? "#" + Integer.toHexString(current.asARGB()) : "none";
        return ActionButton.builder(Component.text(label + ": " + text))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, ignored -> editBackgroundColorDialog(label, current, allowReset ? Component.text("Blank resets the color") : null, null, setter, reopen));
                })))
                .build();
    }

    private static DialogLike editBackgroundColorDialog(
            final String label,
            @Nullable final Color current,
            @Nullable final Component note,
            @Nullable final String currentInput,
            final BiConsumer<Audience, @Nullable Color> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var actions = new ArrayList<ActionButton>();
        for (final var color : namedTextColors()) {
            actions.add(ActionButton.builder(Component.text(describeTextColor(color), color))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        setter.accept(audience, Color.fromRGB(color.value()));
                    })))
                    .build());
        }

        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("value");
                    final var parsed = parseBackgroundColor(input);
                    if (parsed.error() != null) {
                        show(audience, ignored -> editBackgroundColorDialog(label, current, Component.text(parsed.error(), NamedTextColor.RED), input, setter, reopen));
                        return;
                    }
                    setter.accept(audience, parsed.value());
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> show(audience, reopen))))
                .build();
        final var reset = ActionButton.builder(Component.text("Reset", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    setter.accept(audience, null);
                    show(audience, reopen);
                })))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Pick a named color or enter a hex value")));
        body.add(DialogBody.plainMessage(Component.text("Use Reset to clear the background color")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        actions.add(save);
        actions.add(reset);
        actions.add(back);
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(label))
                        .body(body)
                        .inputs(List.of(DialogInput.text("value", Component.text(label))
                                .initial(currentInput != null ? currentInput : current != null ? "#" + Integer.toHexString(current.asARGB()) : "")
                                .build()))
                        .build())
                .type(DialogType.multiAction(actions).build()));
    }

    private static ParseResult<@Nullable Color> parseBackgroundColor(@Nullable final String input) {
        try {
            if (input == null || input.isBlank()) return new ParseResult<>(null, null);
            final var trimmed = input.trim();
            final var named = NamedTextColor.NAMES.value(trimmed.toLowerCase(Locale.ROOT));
            if (named != null) return new ParseResult<>(Color.fromRGB(named.value()), null);
            final var hex = trimmed.startsWith("#") ? trimmed.substring(1) : trimmed;
            if (hex.length() != 6 && hex.length() != 8) return new ParseResult<>(null, "Invalid color");
            final var value = Integer.parseUnsignedInt(hex, 16);
            return new ParseResult<>(Color.fromARGB(hex.length() == 6 ? 0xFF000000 | value : value), null);
        } catch (final IllegalArgumentException ignored) {
            return new ParseResult<>(null, "Invalid color");
        }
    }

    private static DialogLike editIntValueDialog(
            final String label,
            final int current,
            final int min,
            final int max,
            @Nullable final Component note,
            final BiConsumer<Audience, Integer> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var parsed = parseDouble(label, response.getText("value"), min, max);
                    if (parsed.error() != null) {
                        show(audience, ignored -> editIntValueDialog(label, current, min, max, Component.text(parsed.error(), NamedTextColor.RED), setter, reopen));
                        return;
                    }
                    setter.accept(audience, parsed.value().intValue());
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    show(audience, reopen);
                })))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter the new " + label.toLowerCase(Locale.ROOT))));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(label))
                        .body(body)
                        .inputs(List.of(DialogInput.text("value", Component.text(label)).initial(Integer.toString(current)).build()))
                        .build())
                .type(DialogType.multiAction(List.of(save)).exitAction(back).build()));
    }

    private static DialogLike editFloatValueDialog(
            final String label,
            final double current,
            final double min,
            final double max,
            @Nullable final Component note,
            final BiConsumer<Audience, Double> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var parsed = parseDouble(label, response.getText("value"), min, max);
                    if (parsed.error() != null) {
                        show(audience, ignored -> editFloatValueDialog(label, current, min, max, Component.text(parsed.error(), NamedTextColor.RED), setter, reopen));
                        return;
                    }
                    setter.accept(audience, parsed.value());
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> show(audience, reopen))))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter the new " + label.toLowerCase(Locale.ROOT))));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(label))
                        .body(body)
                        .inputs(List.of(DialogInput.text("value", Component.text(label)).initial(Double.toString(current)).build()))
                        .build())
                .type(DialogType.multiAction(List.of(save)).exitAction(back).build()));
    }

    private static DialogLike editLineWidthDialog(
            final int current,
            final BiConsumer<Audience, Integer> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var parsed = parseDouble("Line Width", response.getText("value"), 1, Integer.MAX_VALUE);
                    if (parsed.error() != null) {
                        show(audience, ignored -> editLineWidthDialog(current, setter, reopen));
                        return;
                    }
                    setter.accept(audience, parsed.value().intValue());
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var reset = ActionButton.builder(Component.text("Reset", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    setter.accept(audience, Integer.MAX_VALUE);
                    show(audience, reopen);
                })))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> show(audience, reopen))))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter the line width or use Reset to restore the default")));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Line Width"))
                        .body(body)
                        .inputs(List.of(DialogInput.text("value", Component.text("Line Width"))
                                .initial(current == Integer.MAX_VALUE ? "" : Integer.toString(current))
                                .build()))
                        .build())
                .type(DialogType.multiAction(List.of(save, reset)).exitAction(back).build()));
    }

    private static DialogLike editScaleDialog(
            final double current,
            final double min,
            final double max,
            @Nullable final Component note,
            final BiConsumer<Audience, Double> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var parsed = parseDouble("Scale", response.getText("value"), min, max);
                    if (parsed.error() != null) {
                        show(audience, ignored -> editScaleDialog(current, min, max, Component.text(parsed.error(), NamedTextColor.RED), setter, reopen));
                        return;
                    }
                    if (!Double.isFinite(parsed.value())) {
                        show(audience, ignored -> editScaleDialog(current, min, max, Component.text("Scale must be a finite number", NamedTextColor.RED), setter, reopen));
                        return;
                    }
                    setter.accept(audience, parsed.value());
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> show(audience, reopen))))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter a scale value between 0.1 and 100")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Scale"))
                        .body(body)
                        .inputs(List.of(DialogInput.text("value", Component.text("Scale")).initial(Double.toString(current)).build()))
                        .build())
                .type(DialogType.multiAction(List.of(save)).exitAction(back).build()));
    }

    private static DialogLike editDisplayScaleDialog(
            final DisplayHologramLine line,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        final var transformation = line.getTransformation();
        final var scale = transformation.getScale();
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var x = parseDouble("X", response.getText("x"), 0.1d, 100.0d);
                    final var y = parseDouble("Y", response.getText("y"), 0.1d, 100.0d);
                    final var z = parseDouble("Z", response.getText("z"), 0.1d, 100.0d);
                    if (x.error() != null || y.error() != null || z.error() != null) {
                        final var error = x.error() != null ? x.error() : y.error() != null ? y.error() : z.error();
                        show(audience, ignored -> editDisplayScaleDialog(line, Component.text(error, NamedTextColor.RED), reopen));
                        return;
                    }
                    final var updated = new Transformation(
                            transformation.getTranslation(),
                            transformation.getLeftRotation(),
                            new Vector3f(x.value().floatValue(), y.value().floatValue(), z.value().floatValue()),
                            transformation.getRightRotation()
                    );
                    line.setTransformation(updated);
                    show(audience, reopen);
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> show(audience, reopen))))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter X, Y, and Z scale values")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Scale"))
                        .body(body)
                        .inputs(List.of(
                                DialogInput.text("x", Component.text("X")).initial(Double.toString(scale.x())).build(),
                                DialogInput.text("y", Component.text("Y")).initial(Double.toString(scale.y())).build(),
                                DialogInput.text("z", Component.text("Z")).initial(Double.toString(scale.z())).build()
                        ))
                        .build())
                .type(DialogType.multiAction(List.of(save)).exitAction(back).build()));
    }

    private static DialogLike editTextColorDialog(
            final String label,
            @Nullable final TextColor current,
            @Nullable final Component note,
            final BiConsumer<Audience, @Nullable TextColor> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = response.getText("value");
                    final var parsed = parseTextColor(input);
                    if (parsed.error() != null) {
                        show(audience, ignored -> editTextColorDialog(label, current, Component.text(parsed.error(), NamedTextColor.RED), setter, reopen));
                        return;
                    }
                    setter.accept(audience, parsed.value());
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> show(audience, reopen))))
                .build();
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text("Enter a named color or hex value; blank resets it")));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(label))
                        .body(body)
                        .inputs(List.of(DialogInput.text("value", Component.text(label)).initial(current != null ? "#" + Integer.toHexString(current.value()) : "").build()))
                        .build())
                .type(DialogType.multiAction(List.of(save)).exitAction(back).build()));
    }

    private static DialogLike editBrightnessDialog(
            final DisplayHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        final var current = line.getBrightness().orElse(null);
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var blockLight = parseDouble("Block light", response.getText("block"), 0, 15);
                    final var skyLight = parseDouble("Sky light", response.getText("sky"), 0, 15);
                    if (blockLight.error() != null || skyLight.error() != null) {
                        final var message = blockLight.error() != null ? blockLight.error() : skyLight.error();
                        show(audience, ignored -> editBrightnessDialog(line, reopen));
                        return;
                    }
                    line.setBrightness(new Display.Brightness(blockLight.value().intValue(), skyLight.value().intValue()));
                    show(audience, reopen);
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var clear = ActionButton.builder(Component.text("Reset"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    line.setBrightness(null);
                    show(audience, reopen);
                })))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> show(audience, reopen))))
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

    private static DialogLike editOffsetDialog(
            final StaticHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        final var current = line.getOffset();
        final var save = ActionButton.builder(Component.text("Save", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var x = parseDouble("X", response.getText("x"), -1000, 1000);
                    final var y = parseDouble("Y", response.getText("y"), -1000, 1000);
                    final var z = parseDouble("Z", response.getText("z"), -1000, 1000);
                    if (x.error() != null || y.error() != null || z.error() != null) {
                        show(audience, ignored -> editOffsetDialog(line, reopen));
                        return;
                    }
                    line.setOffset(new Vector3f(x.value().floatValue(), y.value().floatValue(), z.value().floatValue()));
                    show(audience, reopen);
                }, ClickCallback.Options.builder().uses(1).build()))
                .build();
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> show(audience, reopen))))
                .build();
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Offset"))
                        .body(List.of(DialogBody.plainMessage(Component.text("Set the entity offset"))))
                        .inputs(List.of(
                                DialogInput.text("x", Component.text("X")).initial(Float.toString(current.x())).build(),
                                DialogInput.text("y", Component.text("Y")).initial(Float.toString(current.y())).build(),
                                DialogInput.text("z", Component.text("Z")).initial(Float.toString(current.z())).build()
                        )).build())
                .type(DialogType.multiAction(List.of(save)).exitAction(back).build()));
    }

    private static DialogLike editBillboardDialog(
            final StaticHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        final var actions = new ArrayList<ActionButton>();
        for (final var billboard : Display.Billboard.values()) {
            actions.add(ActionButton.builder(Component.text(friendlyName(billboard.name())))
                    .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                        line.setBillboard(billboard);
                        show(audience, reopen);
                    }))).build());
        }
        final var back = ActionButton.builder(Component.text("Back"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> show(audience, reopen))))
                .build();
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Billboard"))
                        .body(List.of(DialogBody.plainMessage(Component.text("Choose the billboard mode"))))
                        .build())
                .type(DialogType.multiAction(actions).exitAction(back).build()));
    }

    private static ParseResult<@Nullable TextColor> parseTextColor(@Nullable final String input) {
        try {
            if (input == null || input.isBlank()) return new ParseResult<>(null, null);
            final var trimmed = input.trim();
            final var named = NamedTextColor.NAMES.value(trimmed.toLowerCase(Locale.ROOT));
            if (named != null) return new ParseResult<>(named, null);
            final var hex = trimmed.startsWith("#") ? trimmed : "#" + trimmed;
            final var color = TextColor.fromHexString(hex);
            return color != null ? new ParseResult<>(color, null) : new ParseResult<>(null, "Invalid color");
        } catch (final IllegalArgumentException ignored) {
            return new ParseResult<>(null, "Invalid color");
        }
    }

    private static DialogLike addBlockPage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final String initial,
            @Nullable final Component note
    ) {
        final var held = useHeldBlockButton((audience, block) -> {
            line.addBlockPage().setBlock(block);
            show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
        });
        return blockSearchDialog("Add Block Page", initial, note, List.of(held), editPagedBackButton(hologram, lineIndex, line),
                (audience, block) -> {
                    line.addBlockPage().setBlock(block);
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                });
    }

    private static DialogLike addEntityPage(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final String initial,
            @Nullable final Component note
    ) {
        return entitySearchDialog("Add Entity Page", initial, note, editPagedBackButton(hologram, lineIndex, line),
                (audience, entityType) -> {
                    line.addEntityPage(entityType);
                    show(audience, current -> editPagedLine(hologram, lineIndex, line, current));
                });
    }

    private static DialogLike itemSearchDialog(
            final String title,
            final String initial,
            @Nullable final Component note,
            final List<ActionButton> extraActions,
            final ActionButton back,
            final BiConsumer<Audience, ItemStack> selection
    ) {
        return itemSearchDialog(Component.text(title), initial, note, extraActions, back, selection);
    }

    private static DialogLike itemSearchDialog(
            final Component title,
            final String initial,
            @Nullable final Component note,
            final List<ActionButton> extraActions,
            final ActionButton back,
            final BiConsumer<Audience, ItemStack> selection
    ) {
        return itemSearchDialog(title, initial, note, extraActions, back, selection, 0);
    }

    private static DialogLike itemSearchDialog(
            final Component title,
            final String initial,
            @Nullable final Component note,
            final List<ActionButton> extraActions,
            final ActionButton back,
            final BiConsumer<Audience, ItemStack> selection,
            final int page
    ) {
        final var query = normalizeSearch(initial);
        final var matches = searchMaterials(query, Material::isItem);
        final var pageCount = pageCount(matches);
        final var currentPage = clampPage(page, pageCount);
        final var search = ActionButton.builder(Component.text("Search", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = input(response, "search");
                    final var result = searchMaterials(input, Material::isItem);
                    final var message = result.isEmpty() ? Component.text("No matching items found", NamedTextColor.RED) : null;
                    show(audience, ignored -> itemSearchDialog(title, input, message, extraActions, back, selection));
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var actions = new ArrayList<ActionButton>();
        if (currentPage > 0) actions.add(pageButton("Previous Page", audience -> {
            show(audience, ignored -> itemSearchDialog(title, initial, note, extraActions, back, selection, currentPage - 1));
        }));
        if (currentPage + 1 < pageCount) actions.add(pageButton("Next Page", audience -> {
            show(audience, ignored -> itemSearchDialog(title, initial, note, extraActions, back, selection, currentPage + 1));
        }));
        actions.add(search);
        actions.addAll(extraActions);
        matches.stream().skip((long) currentPage * SEARCH_PAGE_SIZE).limit(SEARCH_PAGE_SIZE).map(material -> selectionButton(material, audience -> {
            selection.accept(audience, ItemStack.of(material));
        })).forEach(actions::add);

        final var body = searchBody("Search by friendly name or key", note, matches, currentPage, pageCount);
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(title)
                        .body(body)
                        .inputs(List.of(DialogInput.text("search", Component.text("Search")).initial(initial).build()))
                        .build())
                .type(DialogType.multiAction(actions).exitAction(back).build()));
    }

    private static DialogLike blockSearchDialog(
            final String title,
            final String initial,
            @Nullable final Component note,
            final List<ActionButton> extraActions,
            final ActionButton back,
            final BiConsumer<Audience, BlockData> selection
    ) {
        return blockSearchDialog(Component.text(title), initial, note, extraActions, back, selection);
    }

    private static DialogLike blockSearchDialog(
            final Component title,
            final String initial,
            @Nullable final Component note,
            final List<ActionButton> extraActions,
            final ActionButton back,
            final BiConsumer<Audience, BlockData> selection
    ) {
        return blockSearchDialog(title, initial, note, extraActions, back, selection, 0);
    }

    private static DialogLike blockSearchDialog(
            final Component title,
            final String initial,
            @Nullable final Component note,
            final List<ActionButton> extraActions,
            final ActionButton back,
            final BiConsumer<Audience, BlockData> selection,
            final int page
    ) {
        final var query = normalizeSearch(initial);
        final var matches = searchMaterials(query, Material::isBlock);
        final var pageCount = pageCount(matches);
        final var currentPage = clampPage(page, pageCount);
        final var search = ActionButton.builder(Component.text("Search", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = input(response, "search");
                    final var result = searchMaterials(input, Material::isBlock);
                    final var message = result.isEmpty() ? Component.text("No matching blocks found", NamedTextColor.RED) : null;
                    show(audience, ignored -> blockSearchDialog(title, input, message, extraActions, back, selection));
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var actions = new ArrayList<ActionButton>();
        if (currentPage > 0) actions.add(pageButton("Previous Page", audience -> {
            show(audience, ignored -> blockSearchDialog(title, initial, note, extraActions, back, selection, currentPage - 1));
        }));
        if (currentPage + 1 < pageCount) actions.add(pageButton("Next Page", audience -> {
            show(audience, ignored -> blockSearchDialog(title, initial, note, extraActions, back, selection, currentPage + 1));
        }));
        actions.add(search);
        actions.addAll(extraActions);
        matches.stream().skip((long) currentPage * SEARCH_PAGE_SIZE).limit(SEARCH_PAGE_SIZE).map(material -> selectionButton(material, audience -> {
            selection.accept(audience, material.createBlockData());
        })).forEach(actions::add);

        final var body = searchBody("Search by friendly name or key", note, matches, currentPage, pageCount);
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(title)
                        .body(body)
                        .inputs(List.of(DialogInput.text("search", Component.text("Search")).initial(initial).build()))
                        .build())
                .type(DialogType.multiAction(actions).exitAction(back).build()));
    }

    private static DialogLike entitySearchDialog(
            final String title,
            final String initial,
            @Nullable final Component note,
            final ActionButton back,
            final BiConsumer<Audience, EntityType> selection,
            final ActionButton... extraActions
    ) {
        return entitySearchDialog(Component.text(title), initial, note, back, selection, extraActions);
    }

    private static DialogLike entitySearchDialog(
            final Component title,
            final String initial,
            @Nullable final Component note,
            final ActionButton back,
            final BiConsumer<Audience, EntityType> selection,
            final ActionButton... extraActions
    ) {
        return entitySearchDialog(title, initial, note, back, selection, 0, extraActions);
    }

    private static DialogLike entitySearchDialog(
            final Component title,
            final String initial,
            @Nullable final Component note,
            final ActionButton back,
            final BiConsumer<Audience, EntityType> selection,
            final int page,
            final ActionButton... extraActions
    ) {
        final var query = normalizeSearch(initial);
        final var matches = searchEntities(query);
        final var pageCount = pageCount(matches);
        final var currentPage = clampPage(page, pageCount);
        final var search = ActionButton.builder(Component.text("Search", NamedTextColor.GREEN))
                .action(DialogAction.customClick((response, audience) -> {
                    final var input = input(response, "search");
                    final var result = searchEntities(input);
                    final var message = result.isEmpty() ? Component.text("No matching entities found", NamedTextColor.RED) : null;
                    show(audience, ignored -> entitySearchDialog(title, input, message, back, selection, extraActions));
                }, ClickCallback.Options.builder().uses(1).build())).build();
        final var actions = new ArrayList<ActionButton>();
        if (currentPage > 0) actions.add(pageButton("Previous Page", audience -> {
            show(audience, ignored -> entitySearchDialog(title, initial, note, back, selection, currentPage - 1, extraActions));
        }));
        if (currentPage + 1 < pageCount) actions.add(pageButton("Next Page", audience -> {
            show(audience, ignored -> entitySearchDialog(title, initial, note, back, selection, currentPage + 1, extraActions));
        }));
        actions.add(search);
        actions.addAll(Arrays.asList(extraActions));
        matches.stream().skip((long) currentPage * SEARCH_PAGE_SIZE).limit(SEARCH_PAGE_SIZE).map(entity -> selectionButton(entity, audience -> {
            selection.accept(audience, entity);
        })).forEach(actions::add);

        final var body = searchBody("Search by friendly name or key", note, matches, currentPage, pageCount);
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(title)
                        .body(body)
                        .inputs(List.of(DialogInput.text("search", Component.text("Search")).initial(initial).build()))
                        .build())
                .type(DialogType.multiAction(actions).exitAction(back).build()));
    }

    private static ActionButton selectionButton(final Material material, final java.util.function.Consumer<Audience> selection) {
        return ActionButton.builder(Component.text(friendlyName(material.key().value())))
                .tooltip(Component.text(material.key().asString()))
                .action(DialogAction.staticAction(ClickEvent.callback(selection::accept)))
                .build();
    }

    private static ActionButton selectionButton(final EntityType entityType, final java.util.function.Consumer<Audience> selection) {
        return ActionButton.builder(Component.text(friendlyName(entityType.key().value())))
                .tooltip(Component.text(entityType.key().asString()))
                .action(DialogAction.staticAction(ClickEvent.callback(selection::accept)))
                .build();
    }

    private static ActionButton pageButton(final String label, final java.util.function.Consumer<Audience> action) {
        return ActionButton.builder(Component.text(label, NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(action::accept)))
                .build();
    }

    private static ActionButton useHeldBlockButton(final BiConsumer<Audience, BlockData> selection) {
        return ActionButton.builder(Component.text("Use Held Block", NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    if (!(audience instanceof final Player player)) {
                        audience.sendMessage(Component.text("Only players can select a held block", NamedTextColor.RED));
                        return;
                    }
                    final var material = player.getInventory().getItemInMainHand().getType();
                    if (!material.isBlock()) {
                        audience.sendMessage(Component.text("Held item is not a block", NamedTextColor.RED));
                        return;
                    }
                    selection.accept(audience, material.createBlockData());
                }))).build();
    }

    private static @Nullable ActionButton heldItemButton(
            final Audience viewer,
            final String label,
            final BiConsumer<Audience, ItemStack> selection
    ) {
        if (!(viewer instanceof final Player player)) return null;
        final var item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) return null;
        return ActionButton.builder(Component.text(label, NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    selection.accept(audience, item);
                })))
                .build();
    }

    private static List<DialogBody> searchBody(
            final String prompt,
            @Nullable final Component note,
            final List<?> matches,
            final int currentPage,
            final int pageCount
    ) {
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text(prompt)));
        if (note != null) body.add(DialogBody.plainMessage(note));
        if (matches.isEmpty()) body.add(DialogBody.plainMessage(Component.text("No results")));
        else if (pageCount > 1)
            body.add(DialogBody.plainMessage(Component.text("Page " + (currentPage + 1) + " of " + pageCount)));
        return body;
    }

    private static int pageCount(final List<?> matches) {
        return Math.max(1, (matches.size() + SEARCH_PAGE_SIZE - 1) / SEARCH_PAGE_SIZE);
    }

    private static int clampPage(final int page, final int pageCount) {
        return Math.clamp(page, 0, pageCount - 1);
    }

    private static List<Material> searchMaterials(final String query, final Predicate<Material> filter) {
        return Arrays.stream(Material.values())
                .filter(material -> !material.isLegacy())
                .filter(filter)
                .filter(material -> matches(query, material.key().asString(), material.key().value()))
                .sorted(Comparator.comparing(material -> friendlyName(material.key().value()), String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private static List<EntityType> searchEntities(final String query) {
        return Arrays.stream(EntityType.values())
                .filter(EntityType::isSpawnable)
                .filter(entityType -> matches(query, entityType.key().asString(), entityType.key().value()))
                .sorted(Comparator.comparing(entityType -> friendlyName(entityType.key().value()), String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private static boolean matches(final String query, final String key, final String value) {
        if (query.isBlank()) return true;
        return normalizeSearch(key).contains(query) || normalizeSearch(value).contains(query);
    }

    private static String normalizeSearch(final String input) {
        return input.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
    }

    private static String friendlyName(final String key) {
        final var words = key.replace('_', ' ').split(" ");
        final var builder = new StringBuilder();
        for (final var word : words) {
            if (word.isBlank()) continue;
            if (!builder.isEmpty()) builder.append(' ');
            builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return builder.toString();
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
            case final ItemHologramLine itemLine -> itemLine.isPlayerHead()
                    ? "Player Head"
                    : itemLine.getItemStack().getType().key().asString();
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

    private static String imageTag(final String source, final int height) {
        return "<image:" + quoteArgument(source) + ":" + height + ">";
    }

    private static String quoteArgument(final String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    @SuppressWarnings("HttpUrlsUsage")
    private static ParseResult<BufferedImage> parseImageSource(final @Nullable String input) {
        try {
            if (input == null || input.isBlank()) return new ParseResult<>(null, "Image source cannot be empty");
            final var source = input.trim();
            final var image = source.startsWith("http://") || source.startsWith("https://")
                    ? ImageIO.read(URI.create(source).toURL())
                    : ImageIO.read(Path.of(source).toFile());
            return image != null
                    ? new ParseResult<>(image, null)
                    : new ParseResult<>(null, "Could not read image");
        } catch (final IOException | IllegalArgumentException ignored) {
            return new ParseResult<>(null, "Could not read image");
        }
    }

    private static ParseResult<Integer> parseImageHeight(final @Nullable String input) {
        try {
            if (input == null || input.isBlank()) return new ParseResult<>(8, null);
            final var height = Integer.parseInt(input.trim());
            if (height < 1) return new ParseResult<>(null, "Image size must be at least 1");
            return new ParseResult<>(height, null);
        } catch (final NumberFormatException ignored) {
            return new ParseResult<>(null, "Image size must be a number");
        }
    }

    private static String formatSeconds(final Duration duration) {
        final var seconds = duration.toMillis() / 1000d;
        return formatDecimal(seconds);
    }

    private static String formatIntervalInput(final Duration duration) {
        final var millis = duration.toMillis();
        if (millis < 1000) return millis + "ms";

        final var seconds = millis / 1000d;
        if (seconds >= 3600) return formatDecimal(seconds / 3600d) + "h";
        if (seconds >= 60) return formatDecimal(seconds / 60d) + "m";
        return formatDecimal(seconds) + "s";
    }

    private static String formatDecimal(final double value) {
        if (value == Math.rint(value)) return Long.toString((long) value);
        return Double.toString(value);
    }

    private static String getUnit(final String trimmed) {
        var index = trimmed.length();
        while (index > 0 && Character.isLetter(trimmed.charAt(index - 1))) index--;
        return trimmed.substring(index);
    }

    private static ParseResult<Duration> parseDuration(final @Nullable String input) {
        try {
            final var trimmed = input != null ? input.trim().toLowerCase(Locale.ROOT) : null;
            final var unit = trimmed != null ? getUnit(trimmed) : null;
            final var number = unit != null ? trimmed.substring(0, trimmed.length() - unit.length()) : null;

            if (number == null) return new ParseResult<>(null, "Interval cannot be empty");

            final var value = Double.parseDouble(number);
            final var duration = switch (unit) {
                case "ms" -> Duration.ofMillis(Math.round(value));
                case "", "s" -> Duration.ofMillis(Math.round(Duration.ofSeconds(1).toMillis() * value));
                case "m" -> Duration.ofMillis(Math.round(Duration.ofMinutes(1).toMillis() * value));
                case "h" -> Duration.ofMillis(Math.round(Duration.ofHours(1).toMillis() * value));
                default -> null;
            };
            if (duration == null)
                return new ParseResult<>(null, "Invalid interval; use a number optionally followed by ms, s, m, or h");

            if (duration.toMillis() < 50) return new ParseResult<>(null, "Interval must be at least 50ms");
            return new ParseResult<>(Duration.ofMillis(Math.round(duration.toMillis() / 50d) * 50), null);
        } catch (final NumberFormatException ignored) {
            return new ParseResult<>(null, "Invalid interval; use a number optionally followed by ms, s, m, or h");
        }
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
