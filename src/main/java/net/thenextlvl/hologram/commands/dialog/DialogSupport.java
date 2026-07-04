package net.thenextlvl.hologram.commands.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.action.ActionType;
import net.thenextlvl.hologram.action.ActionTypes;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.action.ClickType;
import net.thenextlvl.hologram.action.PageChange;
import net.thenextlvl.hologram.action.UnparsedTitle;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.DisplayHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.StaticHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import net.thenextlvl.hologram.models.ClickTypes;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@NullMarked
public final class DialogSupport {
    static final int SEARCH_PAGE_SIZE = 20;

    static final ActionTypes ACTION_TYPES = ActionTypes.types();

    static final Map<UUID, Function<Audience, DialogLike>> LAST_DIALOGS = new ConcurrentHashMap<>();

    public static void showLast(final Audience audience) {
        if (audience instanceof final Player player) {
            DialogSupport.show(audience, LAST_DIALOGS.getOrDefault(player.getUniqueId(), ignored -> OverviewDialog.create()));
            return;
        }
        audience.showDialog(DialogControl.resolve(OverviewDialog.create()));
    }

    static void show(final Audience audience, final Function<Audience, DialogLike> dialog) {
        DialogControl.show(audience, LAST_DIALOGS, dialog);
    }

    static ActionButton deleteLineButton(final Hologram hologram, final int lineIndex) {
        return DialogSupport.deleteLineButton(hologram, lineIndex, true);
    }

    static ActionButton deleteLineButton(final Hologram hologram, final int lineIndex, final boolean customWidth) {
        final var builder = ActionButton.builder(Component.text("Delete this line", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(DeleteLineDialog.create(hologram, lineIndex, audience));
                })));
        return customWidth ? builder.width(300).build() : builder.build();
    }

    static ActionButton editHologramBackButton(final Hologram hologram) {
        return DialogControl.backButton(300, audience -> EditHologramDialog.create(hologram, audience));
    }

    static ActionButton deletePageButton(final Hologram hologram, final int lineIndex, final PagedHologramLine line, final int pageIndex) {
        return DialogSupport.deletePageButton(hologram, lineIndex, line, pageIndex, true);
    }

    static ActionButton deletePageButton(
            final Hologram hologram,
            final int lineIndex,
            final PagedHologramLine line,
            final int pageIndex,
            final boolean customWidth
    ) {
        final var builder = ActionButton.builder(Component.text("Delete this page", NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    audience.showDialog(DeletePageDialog.create(hologram, lineIndex, line, pageIndex));
                })));
        return customWidth ? builder.width(300).build() : builder.build();
    }

    static List<DialogBody> bodyLines(final String description, @Nullable final Component note) {
        final var body = new ArrayList<DialogBody>();
        body.add(DialogBody.plainMessage(Component.text(description)));
        if (note != null) body.add(DialogBody.plainMessage(note));
        return body;
    }

    static ActionButton editPagedBackButton(final Hologram hologram, final int lineIndex, final PagedHologramLine line) {
        return DialogControl.backButton(300, ignored -> AddPageTypeDialog.create(hologram, lineIndex, line));
    }

    static ActionButton editPageBackButton(final Hologram hologram, final int lineIndex, final PagedHologramLine line) {
        return DialogControl.backButton(300, audience -> EditPagedLineDialog.create(hologram, lineIndex, line, audience));
    }

    static ActionButton toggleButton(
            final String label,
            final boolean current,
            final BiConsumer<Audience, Boolean> setter
    ) {
        return ActionButton.builder(Component.text(label + ": " + (current ? "On" : "Off"), current ? NamedTextColor.GREEN : NamedTextColor.RED))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> setter.accept(audience, !current))))
                .build();
    }

    static ActionButton clickActionsButton(
            final Hologram hologram,
            final HologramLine line,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        return ActionButton.builder(Component.text("Click Actions", NamedTextColor.LIGHT_PURPLE))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> ClickActionsDialog.create(hologram, line, header, note, reopen));
                })))
                .width(300)
                .build();
    }

    static ActionButton actionButton(
            final Hologram hologram,
            final HologramLine line,
            final Component header,
            final String name,
            final ClickAction<?> action,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        final var label = Component.text(name + ": " + DialogSupport.friendlyName(action.getActionType().name()));
        return ActionButton.builder(label)
                .tooltip(Component.text(DialogSupport.actionSummary(action)))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> EditActionDialog.create(hologram, line, name, action, header, note, reopen));
                })))
                .build();
    }

    static <T> ActionButton actionTypeButton(
            final String label,
            final ActionType<T> type,
            final Hologram hologram,
            final HologramLine line,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        return ActionButton.builder(Component.text(label, NamedTextColor.GREEN))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> AddActionDialog.create(hologram, line, type, DialogSupport.defaultInput(line, type), header, note, "", reopen));
                })))
                .build();
    }

    static ActionButton actionInputButton(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<?> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        return ActionButton.builder(Component.text("Input: " + DialogSupport.actionInputSummary(action)))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> EditActionInputDialog.create(hologram, line, actionName, action, header, note, reopen));
                })))
                .build();
    }

    static ActionButton clickTypesButton(
            final Hologram hologram,
            final HologramLine line,
            final String actionName,
            final ClickAction<?> action,
            final Component header,
            @Nullable final Component note,
            final Function<Audience, DialogLike> reopen
    ) {
        return ActionButton.builder(Component.text("Click Types: " + DialogSupport.clickTypesSummary(action.getClickTypes())))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> SelectClickTypesDialog.create(hologram, line, actionName, action, header, note, reopen));
                })))
                .build();
    }

    static ActionButton visualGlowButton(
            final Hologram hologram,
            final StaticHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        return DialogSupport.toggleButton("Glowing", line.isGlowing(), (audience, value) -> {
            line.setGlowing(value);
            DialogSupport.show(audience, reopen);
        });
    }

    static ActionButton visualGlowColorButton(
            final Hologram hologram,
            final StaticHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        final var current = line.getGlowColor().orElse(null);
        final var label = Component.text("Glow Color: ")
                .append(Component.text(DialogSupport.describeTextColor(current), current != null ? current : NamedTextColor.WHITE));
        return ActionButton.builder(label)
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> EditGlowColorDialog.create(current, null, null, (target, color) -> {
                        line.setGlowColor(color);
                        DialogSupport.show(target, reopen);
                    }, reopen));
                })))
                .build();
    }

    static ActionButton visualBillboardButton(
            final Hologram hologram,
            final StaticHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        return ActionButton.builder(Component.text("Billboard: " + line.getBillboard().name(), NamedTextColor.YELLOW))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditBillboardDialog.create(line, reopen));
                })))
                .build();
    }

    static ActionButton visualBrightnessButton(
            final Hologram hologram,
            final DisplayHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        return ActionButton.builder(Component.text("Brightness"))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditBrightnessDialog.create(line, reopen));
                })))
                .build();
    }

    static ActionButton visualOffsetButton(
            final Hologram hologram,
            final StaticHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        final var offset = line.getOffset();
        return ActionButton.builder(Component.text("Offset: " + DialogSupport.formatDecimal(offset.x()) + ", " + DialogSupport.formatDecimal(offset.y()) + ", " + DialogSupport.formatDecimal(offset.z())))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, current -> EditOffsetDialog.create(line, reopen));
                })))
                .build();
    }

    static ActionButton visualIntButton(
            final String label,
            final int current,
            final int min,
            final int max,
            final BiConsumer<Audience, Integer> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        return ActionButton.builder(Component.text(label + ": " + current))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, currentDialog -> EditIntValueDialog.create(label, current, min, max, null, setter, reopen));
                })))
                .build();
    }

    static ActionButton visualLineWidthButton(
            final int current,
            final BiConsumer<Audience, Integer> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var text = current == Integer.MAX_VALUE ? "default" : Integer.toString(current);
        return ActionButton.builder(Component.text("Line Width: " + text))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> EditLineWidthDialog.create(current, setter, reopen));
                })))
                .build();
    }

    static ActionButton visualFloatButton(
            final String label,
            final double current,
            final double min,
            final double max,
            final BiConsumer<Audience, Double> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        return ActionButton.builder(Component.text(label + ": " + DialogSupport.formatDecimal(current)))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, currentDialog -> EditFloatValueDialog.create(label, current, min, max, null, setter, reopen));
                })))
                .build();
    }

    static ActionButton visualScaleButton(
            final double current,
            final double min,
            final double max,
            final BiConsumer<Audience, Double> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        return ActionButton.builder(Component.text("Scale: " + DialogSupport.formatDecimal(current)))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, currentDialog -> EditScaleDialog.create(current, min, max, null, setter, reopen));
                })))
                .build();
    }

    static ActionButton visualDisplayScaleButton(
            final DisplayHologramLine line,
            final Function<Audience, DialogLike> reopen
    ) {
        final var scale = line.getTransformation().getScale();
        return ActionButton.builder(Component.text("Scale: " + DialogSupport.formatDecimal(scale.x()) + ", " + DialogSupport.formatDecimal(scale.y()) + ", " + DialogSupport.formatDecimal(scale.z())))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> EditDisplayScaleDialog.create(line, null, reopen));
                })))
                .build();
    }

    static ActionButton visualTextColorButton(
            final String label,
            @Nullable final TextColor current,
            final boolean allowReset,
            final BiConsumer<Audience, @Nullable TextColor> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var text = current != null ? "#" + Integer.toHexString(current.value()) : "none";
        return ActionButton.builder(Component.text(label + ": " + text))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, currentDialog -> EditTextColorDialog.create(label, current, allowReset ? Component.text("Blank resets the color") : null, setter, reopen));
                })))
                .build();
    }

    static String describeTextColor(@Nullable final TextColor color) {
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

    static List<NamedTextColor> namedTextColors() {
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

    static <E extends Enum<E>> ActionButton enumButton(
            final String label,
            final E current,
            final Class<E> type,
            final Consumer<E> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        return ActionButton.builder(Component.text(label + ": " + DialogSupport.friendlyName(current.name())))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> EditEnumDialog.create(label, current, type, setter, reopen));
                })))
                .build();
    }

    static List<ActionButton> addBack(final List<ActionButton> actions, final ActionButton back) {
        final var buttons = new ArrayList<>(actions);
        buttons.add(back);
        return buttons;
    }

    static ActionButton visualBackgroundColorButton(
            final String label,
            @Nullable final Color current,
            final boolean allowReset,
            final BiConsumer<Audience, @Nullable Color> setter,
            final Function<Audience, DialogLike> reopen
    ) {
        final var text = current != null ? "#" + Integer.toHexString(current.asARGB()) : "none";
        return ActionButton.builder(Component.text(label + ": " + text))
                .action(DialogAction.staticAction(ClickEvent.callback(audience -> {
                    DialogSupport.show(audience, ignored -> EditBackgroundColorDialog.create(label, current, allowReset ? Component.text("Blank resets the color") : null, null, setter, reopen));
                })))
                .build();
    }

    static ParseResult<Color> parseBackgroundColor(@Nullable final String input) {
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

    static String actionSummary(final ClickAction<?> action) {
        return "Type: " + DialogSupport.friendlyName(action.getActionType().name())
                + " | Input: " + DialogSupport.actionInputSummary(action)
                + " | Click types: " + DialogSupport.clickTypesSummary(action.getClickTypes())
                + " | Chance: " + action.getChance() + "%"
                + " | Cooldown: " + DialogSupport.formatIntervalInput(action.getCooldown())
                + " | Permission: " + action.getPermission().orElse("none");
    }

    static String actionInputSummary(final ClickAction<?> action) {
        final var type = action.getActionType();
        if (type == ACTION_TYPES.sendActionbar() || type == ACTION_TYPES.sendMessage() || type == ACTION_TYPES.runConsoleCommand()
                || type == ACTION_TYPES.runCommand() || type == ACTION_TYPES.connect()) {
            return action.getInput() instanceof final String value && !value.isBlank() ? value : "empty";
        }
        if (type == ACTION_TYPES.transfer()) {
            final var input = (InetSocketAddress) action.getInput();
            return input.getHostString() + ":" + input.getPort();
        }
        if (type == ACTION_TYPES.teleport()) {
            final var input = (Location) action.getInput();
            return input.getWorld() != null
                    ? input.getWorld().key().asString() + " " + DialogSupport.formatDecimal(input.getX()) + ", " + DialogSupport.formatDecimal(input.getY()) + ", " + DialogSupport.formatDecimal(input.getZ())
                    : DialogSupport.formatDecimal(input.getX()) + ", " + DialogSupport.formatDecimal(input.getY()) + ", " + DialogSupport.formatDecimal(input.getZ());
        }
        if (type == ACTION_TYPES.playSound()) {
            final var input = (Sound) action.getInput();
            return input.name().asString() + " @ " + input.source() + " " + DialogSupport.formatDecimal(input.volume()) + "/" + DialogSupport.formatDecimal(input.pitch());
        }
        if (type == ACTION_TYPES.sendTitle()) {
            final var input = (UnparsedTitle) action.getInput();
            return input.title().isBlank() ? "(blank title)" : input.title();
        }
        if (type == ACTION_TYPES.cyclePage() || type == ACTION_TYPES.setPage()) {
            final var input = (PageChange) action.getInput();
            return type == ACTION_TYPES.setPage() ? "Page " + (input.page() + 1) : "Amount " + input.page();
        }
        return String.valueOf(action.getInput());
    }

    static String clickTypesSummary(final EnumSet<ClickType> clickTypes) {
        for (final var preset : ClickTypes.values()) {
            if (preset.getClickTypes().equals(clickTypes)) return DialogSupport.friendlyName(preset.name());
        }
        return clickTypes.stream().map(clickType -> DialogSupport.friendlyName(clickType.name())).sorted(String.CASE_INSENSITIVE_ORDER).reduce((left, right) -> left + ", " + right).orElse("None");
    }

    static Sound.@Nullable Source parseSoundSource(@Nullable final String input) {
        if (input == null || input.isBlank()) return null;
        try {
            return Sound.Source.valueOf(input.trim().toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException ignored) {
            return null;
        }
    }

    static <T> T defaultInput(final HologramLine line, final ActionType<T> type) {
        if (type == ACTION_TYPES.sendActionbar() || type == ACTION_TYPES.sendMessage() || type == ACTION_TYPES.runConsoleCommand()
                || type == ACTION_TYPES.runCommand() || type == ACTION_TYPES.connect()) {
            @SuppressWarnings("unchecked") final var input = (T) "";
            return input;
        }
        if (type == ACTION_TYPES.transfer()) {
            @SuppressWarnings("unchecked") final var input = (T) new InetSocketAddress("localhost", 25565);
            return input;
        }
        if (type == ACTION_TYPES.teleport()) {
            @SuppressWarnings("unchecked") final var input = (T) line.getHologram().getLocation().clone();
            return input;
        }
        if (type == ACTION_TYPES.playSound()) {
            @SuppressWarnings("unchecked") final var input = (T) Sound.sound(Key.key("minecraft:block.note_block.harp"), Sound.Source.MASTER, 1f, 1f);
            return input;
        }
        if (type == ACTION_TYPES.sendTitle()) {
            @SuppressWarnings("unchecked") final var input = (T) new UnparsedTitle("", "", null);
            return input;
        }
        if (type == ACTION_TYPES.cyclePage()) {
            @SuppressWarnings("unchecked") final var input = (T) new PageChange(1);
            return input;
        }
        if (type == ACTION_TYPES.setPage()) {
            @SuppressWarnings("unchecked") final var input = (T) new PageChange(0);
            return input;
        }
        throw new IllegalArgumentException("Unsupported action type: " + type.name());
    }

    static boolean isBlank(@Nullable final String input) {
        return input == null || input.isBlank();
    }

    static ParseResult<TextColor> parseTextColor(@Nullable final String input) {
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

    static ActionButton selectionButton(final Material material, final java.util.function.Consumer<Audience> selection) {
        return ActionButton.builder(Component.text(DialogSupport.friendlyName(material.key().value())))
                .tooltip(Component.text(material.key().asString()))
                .action(DialogAction.staticAction(ClickEvent.callback(selection::accept)))
                .build();
    }

    static ActionButton selectionButton(final EntityType entityType, final java.util.function.Consumer<Audience> selection) {
        return ActionButton.builder(Component.text(DialogSupport.friendlyName(entityType.key().value())))
                .tooltip(Component.text(entityType.key().asString()))
                .action(DialogAction.staticAction(ClickEvent.callback(selection::accept)))
                .build();
    }

    static ActionButton pageButton(final String label, final java.util.function.Consumer<Audience> action) {
        return DialogControl.page(label, action);
    }

    static ActionButton useHeldBlockButton(final BiConsumer<Audience, BlockData> selection) {
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

    static @Nullable ActionButton heldItemButton(
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

    static List<DialogBody> searchBody(
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

    static int pageCount(final List<?> matches) {
        return Math.max(1, (matches.size() + SEARCH_PAGE_SIZE - 1) / SEARCH_PAGE_SIZE);
    }

    static int clampPage(final int page, final int pageCount) {
        return Math.clamp(page, 0, pageCount - 1);
    }

    static List<Material> searchMaterials(final String query, final Predicate<Material> filter) {
        return Arrays.stream(Material.values())
                .filter(material -> !material.isLegacy())
                .filter(filter)
                .filter(material -> DialogSupport.matches(query, material.key().asString(), material.key().value()))
                .sorted(Comparator.comparing(material -> DialogSupport.friendlyName(material.key().value()), String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    static List<EntityType> searchEntities(final String query) {
        return Arrays.stream(EntityType.values())
                .filter(EntityType::isSpawnable)
                .filter(entityType -> DialogSupport.matches(query, entityType.key().asString(), entityType.key().value()))
                .sorted(Comparator.comparing(entityType -> DialogSupport.friendlyName(entityType.key().value()), String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    static boolean matches(final String query, final String key, final String value) {
        if (query.isBlank()) return true;
        return DialogSupport.normalizeSearch(key).contains(query) || DialogSupport.normalizeSearch(value).contains(query);
    }

    static String normalizeSearch(final String input) {
        return input.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
    }

    static String friendlyName(final String key) {
        final var words = key.replace('_', ' ').split(" ");
        final var builder = new StringBuilder();
        for (final var word : words) {
            if (word.isBlank()) continue;
            if (!builder.isEmpty()) builder.append(' ');
            builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return builder.toString();
    }

    static Component lineLabel(final int lineIndex, final HologramLine line) {
        return Component.text(lineIndex + 1 + ". " + DialogSupport.lineDescription(line));
    }

    static String lineDescription(final HologramLine line) {
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

    static @Nullable Component linePreview(final HologramLine line, final Audience audience) {
        if (line instanceof final TextHologramLine textLine) {
            return textLine.getText(audience).orElse(null);
        }
        return null;
    }

    static String loadLineBreaks(final String text) {
        return text.replace("<newline>", "\n").replace("<br>", "\n");
    }

    static String saveLineBreaks(final String text) {
        return text.replace("\r\n", "\n").replace('\r', '\n').replace("\n", "<newline>");
    }

    static String imageTag(final String source, final int height) {
        return "<image:" + DialogSupport.quoteArgument(source) + ":" + height + ">";
    }

    static String quoteArgument(final String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    @SuppressWarnings("HttpUrlsUsage")

    static ParseResult<BufferedImage> parseImageSource(final @Nullable String input) {
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

    static ParseResult<Integer> parseImageHeight(final @Nullable String input) {
        try {
            if (input == null || input.isBlank()) return new ParseResult<>(8, null);
            final var height = Integer.parseInt(input.trim());
            if (height < 1) return new ParseResult<>(null, "Image size must be at least 1");
            return new ParseResult<>(height, null);
        } catch (final NumberFormatException ignored) {
            return new ParseResult<>(null, "Image size must be a number");
        }
    }

    static String formatSeconds(final Duration duration) {
        final var seconds = duration.toMillis() / 1000d;
        return DialogSupport.formatDecimal(seconds);
    }

    static String formatIntervalInput(final Duration duration) {
        final var millis = duration.toMillis();
        if (millis < 1000) return millis + "ms";

        final var seconds = millis / 1000d;
        if (seconds >= 3600) return DialogSupport.formatDecimal(seconds / 3600d) + "h";
        if (seconds >= 60) return DialogSupport.formatDecimal(seconds / 60d) + "m";
        return DialogSupport.formatDecimal(seconds) + "s";
    }

    static String formatDecimal(final double value) {
        if (value == Math.rint(value)) return Long.toString((long) value);
        return Double.toString(value);
    }

    static String getUnit(final String trimmed) {
        var index = trimmed.length();
        while (index > 0 && Character.isLetter(trimmed.charAt(index - 1))) index--;
        return trimmed.substring(index);
    }

    static ParseResult<Duration> parseDuration(final @Nullable String input) {
        return DialogSupport.parseDuration(input, 50);
    }

    static ParseResult<Duration> parseDuration(final @Nullable String input, final long minimumMillis) {
        try {
            final var trimmed = input != null ? input.trim().toLowerCase(Locale.ROOT) : null;
            final var unit = trimmed != null ? DialogSupport.getUnit(trimmed) : null;
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

            if (duration.toMillis() < minimumMillis)
                return new ParseResult<>(null, "Interval must be at least " + minimumMillis + "ms");
            return new ParseResult<>(Duration.ofMillis(Math.round(duration.toMillis() / 50d) * 50), null);
        } catch (final NumberFormatException ignored) {
            return new ParseResult<>(null, "Invalid interval; use a number optionally followed by ms, s, m, or h");
        }
    }

    @SuppressWarnings("PatternValidation")

    static ParseResult<EntityType> parseEntityType(final @Nullable String input) {
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

    static ParseResult<ItemStack> parseItemStack(final @Nullable String input) {
        try {
            if (input == null || input.isBlank()) return new ParseResult<>(null, "Item cannot be empty");
            return new ParseResult<>(Bukkit.getServer().getItemFactory().createItemStack(input), null);
        } catch (final IllegalArgumentException e) {
            final var message = e.getMessage();
            return new ParseResult<>(null, message != null && !message.isBlank() ? message : "Invalid item");
        }
    }

    record ParseResult<T>(@Nullable T value, @Nullable String error) {
    }

    record LocationInputs(String world, String x, String y, String z, String yaw, String pitch) {
    }

    static DialogInput locationInput(final String key, final String label, final String initial) {
        return DialogInput.text(key, Component.text(label)).initial(initial).build();
    }

    static LocationInputs locationInputs(final Location location) {
        return new DialogSupport.LocationInputs(
                location.getWorld().key().asString(),
                Double.toString(location.getX()),
                Double.toString(location.getY()),
                Double.toString(location.getZ()),
                Double.toString(location.getYaw()),
                Double.toString(location.getPitch())
        );
    }

    static LocationInputs locationInputs(final io.papermc.paper.dialog.DialogResponseView response) {
        return new DialogSupport.LocationInputs(
                DialogSupport.input(response, "world"),
                DialogSupport.input(response, "x"),
                DialogSupport.input(response, "y"),
                DialogSupport.input(response, "z"),
                DialogSupport.input(response, "yaw"),
                DialogSupport.input(response, "pitch")
        );
    }

    static String input(final io.papermc.paper.dialog.DialogResponseView response, final String key) {
        final var value = response.getText(key);
        return value != null ? value : "";
    }

    static ParseResult<Location> parseLocation(
            final Location fallback,
            @Nullable final String world,
            @Nullable final String x,
            @Nullable final String y,
            @Nullable final String z,
            @Nullable final String yaw,
            @Nullable final String pitch
    ) {
        final var parsedWorld = DialogSupport.parseWorld(world);
        if (parsedWorld.error() != null) return new ParseResult<>(null, parsedWorld.error());

        final var parsedX = DialogSupport.parseDouble("X", x, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (parsedX.error() != null) return new ParseResult<>(null, parsedX.error());
        final var parsedY = DialogSupport.parseDouble("Y", y, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (parsedY.error() != null) return new ParseResult<>(null, parsedY.error());
        final var parsedZ = DialogSupport.parseDouble("Z", z, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (parsedZ.error() != null) return new ParseResult<>(null, parsedZ.error());
        final var parsedYaw = DialogSupport.parseDouble("Yaw", yaw, -180, 180);
        if (parsedYaw.error() != null) return new ParseResult<>(null, parsedYaw.error());
        final var parsedPitch = DialogSupport.parseDouble("Pitch", pitch, -90, 90);
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

    static ParseResult<Double> parseDouble(
            final String label,
            @Nullable final String input,
            final double min,
            final double max
    ) {
        if (input == null || input.isBlank()) return new ParseResult<>(null, label + " cannot be empty");
        try {
            final var value = Double.parseDouble(input.trim());
            if (value < min || value > max) return new ParseResult<>(null,
                    label + " must be between " + DialogSupport.formatNumber(min) + " and " + DialogSupport.formatNumber(max));
            return new ParseResult<>(value, null);
        } catch (final NumberFormatException ignored) {
            return new ParseResult<>(null, label + " must be a number");
        }
    }

    static String formatNumber(final double value) {
        return value % 1 == 0 ? Long.toString((long) value) : Double.toString(value);
    }

    @SuppressWarnings("PatternValidation")

    static ParseResult<World> parseWorld(@Nullable final String input) {
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

    // todo:
    //  - add a translations management dialog
    //    - list all translations
    //    - add new translations
    //    - edit existing translations
}
