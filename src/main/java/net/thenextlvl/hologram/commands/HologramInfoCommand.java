package net.thenextlvl.hologram.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.stream.IntStream;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
final class HologramInfoCommand extends SimpleCommand {
    private static final PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.plainText();
    private static final int PREVIEW_LENGTH = 40;

    private HologramInfoCommand(final HologramPlugin plugin) {
        super(plugin, "info", "holograms.command.info");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramInfoCommand(plugin);
        return command.create().then(hologramArgument(plugin)
                .executes(command)
                .then(Commands.argument("page", IntegerArgumentType.integer(1)).executes(command)));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var requestedPage = tryGetArgument(context, "page", int.class).orElse(1);
        final var maxPages = Math.max(1, hologram.getLines()
                .filter(PagedHologramLine.class::isInstance)
                .map(PagedHologramLine.class::cast)
                .mapToInt(PagedHologramLine::getPageCount)
                .max().orElse(1));
        final var page = Math.min(requestedPage, maxPages);

        plugin.bundle().sendMessage(sender, "hologram.info.header",
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.booleanChoice("plural", hologram.getLineCount() != 1),
                Formatter.number("amount", hologram.getLineCount()));

        IntStream.range(0, hologram.getLineCount()).forEach(index -> {
            final var line = hologram.getLine(index).orElse(null);
            if (line == null) return;
            final var lineNumber = index + 1;
            plugin.bundle().sendMessage(sender, "hologram.info.line",
                    Placeholder.parsed("tree", index + 1 == hologram.getLineCount() ? "└" : "├"),
                    Placeholder.component("line", getInfoLine(sender, hologram, line, lineNumber, page)));
        });

        if (maxPages > 1) {
            plugin.bundle().sendMessage(sender, "hologram.info.page",
                    Formatter.number("page", page),
                    Formatter.number("pages", maxPages),
                    Placeholder.component("previous", getPageButton(hologram, "<-", Math.max(1, page - 1), page > 1)),
                    Placeholder.component("next", getPageButton(hologram, "->", Math.min(maxPages, page + 1), page < maxPages)));
        }
        return SINGLE_SUCCESS;
    }

    private LinePreview getLinePreview(final Audience audience, final HologramLine line, final int page) {
        if (line instanceof final PagedHologramLine pagedLine) {
            return pagedLine.getPage(page).map(pageLine -> getStaticLinePreview(audience, pageLine))
                    .orElseGet(() -> LinePreview.of(Component.text("(empty)")));
        }
        return getStaticLinePreview(audience, line);
    }

    private LinePreview getStaticLinePreview(final Audience audience, final HologramLine line) {
        return switch (line) {
            case final TextHologramLine textLine -> LinePreview.of(textLine.getText(audience)
                    .orElse(Component.text("(empty)")));
            case final ItemHologramLine itemLine -> {
                if (itemLine.isPlayerHead() && audience instanceof final Player player) {
                    yield LinePreview.of(Component.object(ObjectContents.playerHead(player)));
                } else {
                    final var item = itemLine.getItemStack();
                    yield LinePreview.of(Component.translatable(item).hoverEvent(item.asHoverEvent()));
                }
            }
            case final BlockHologramLine blockLine ->
                    LinePreview.of(Component.translatable(blockLine.getBlock().getMaterial())
                            .hoverEvent(HoverEvent.showItem(blockLine.getBlock().getMaterial(), 1)));
            case final EntityHologramLine entityLine ->
                    LinePreview.of(Component.translatable(entityLine.getEntityType()));
            default -> LinePreview.of(Component.text("?"));
        };
    }

    private static Component summarize(final Component component) {
        final var text = PLAIN_TEXT.serialize(component);
        final var plain = text.replace('\n', ' ');
        final var preview = plain.length() > PREVIEW_LENGTH ? plain.substring(0, PREVIEW_LENGTH) + "…" : plain;
        return Component.text(preview);
    }

    private String getLineType(final HologramLine line) {
        return switch (line) {
            case final TextHologramLine ignored -> "text";
            case final ItemHologramLine ignored -> "item";
            case final BlockHologramLine ignored -> "block";
            case final EntityHologramLine ignored -> "entity";
            case final PagedHologramLine pagedLine -> "paged (" + pagedLine.getPageCount() + ")";
            default -> "unknown";
        };
    }

    private Component getPageButton(final Hologram hologram, final String label, final int page, final boolean enabled) {
        if (!enabled) return Component.text(label);
        return Component.text(label, NamedTextColor.GREEN)
                .clickEvent(ClickEvent.runCommand("/hologram info " + hologram.getName() + " " + page))
                .hoverEvent(Component.text("Page " + page, NamedTextColor.GRAY));
    }

    private Component getInfoLine(final Audience audience, final Hologram hologram, final HologramLine line,
                                  final int lineNumber, final int page) {
        return Component.text()
                .append(Component.text(lineNumber, NamedTextColor.GREEN))
                .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                .append(Component.text("[", NamedTextColor.DARK_GRAY))
                .append(getLineType(audience, hologram, line, lineNumber))
                .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                .append(getLinePreview(audience, line, page - 1).summary())
                .clickEvent(isEmptyPage(line, page) ? null : ClickEvent.suggestCommand(getEditCommand(hologram, line, lineNumber, page)))
                .hoverEvent(HoverEvent.showText(getEditHover(audience, line, page)))
                .build();
    }

    private Component getLineType(final Audience audience, final Hologram hologram, final HologramLine line,
                                  final int lineNumber) {
        final var type = Component.text(getLineType(line), NamedTextColor.DARK_GRAY);
        if (!(line instanceof final PagedHologramLine pagedLine)) return type;
        return type.clickEvent(ClickEvent.suggestCommand("/holo page settings " + hologram.getName() + " " + lineNumber + " "))
                .hoverEvent(HoverEvent.showText(getPageSettingsHover(audience, pagedLine)));
    }

    private Component getPageSettingsHover(final Audience audience, final PagedHologramLine line) {
        return plugin.bundle().component("hologram.info.setting.hover", audience)
                .append(Component.newline())
                .append(Component.newline())
                .append(plugin.bundle().component("hologram.info.setting.interval", audience,
                        Placeholder.unparsed("value", formatSeconds(line.getInterval()))))
                .append(Component.newline())
                .append(plugin.bundle().component("hologram.info.setting.paused", audience,
                        Placeholder.unparsed("value", Boolean.toString(line.isPaused()))))
                .append(Component.newline())
                .append(plugin.bundle().component("hologram.info.setting.randomized", audience,
                        Placeholder.unparsed("value", Boolean.toString(line.isRandomOrder()))));
    }

    private String formatSeconds(final java.time.Duration duration) {
        final var seconds = duration.toMillis() / 1000d;
        if (seconds == Math.rint(seconds)) return Long.toString((long) seconds);
        return Double.toString(seconds);
    }

    private boolean isEmptyPage(final HologramLine line, final int page) {
        return line instanceof final PagedHologramLine pagedLine && pagedLine.getPage(page - 1).isEmpty();
    }

    private Component getEditHover(final Audience audience, final HologramLine line, final int page) {
        return plugin.bundle().component(line instanceof PagedHologramLine
                        ? "hologram.info.hover.page"
                        : "hologram.info.hover.line", audience,
                Placeholder.component("preview", getLinePreview(audience, line, page - 1).full()));
    }

    private record LinePreview(Component summary, Component full) {
        private static LinePreview of(final Component component) {
            return new LinePreview(summarize(component), component);
        }
    }

    private String getEditCommand(final Hologram hologram, final HologramLine line, final int lineNumber, final int page) {
        if (line instanceof PagedHologramLine) {
            final var command = "/holo page edit " + hologram.getName() + " " + lineNumber + " " + page + " ";
            return command + getTypedEditCommand(getPagedLine(line, page));
        }
        final var command = "/holo line edit " + hologram.getName() + " " + lineNumber + " ";
        return command + getTypedEditCommand(line);
    }

    private HologramLine getPagedLine(final HologramLine line, final int page) {
        if (line instanceof final PagedHologramLine pagedLine) {
            return pagedLine.getPage(page - 1).map(HologramLine.class::cast).orElse(line);
        }
        return line;
    }

    private String getTypedEditCommand(final HologramLine line) {
        return switch (line) {
            case final TextHologramLine textLine -> "set text " + textLine.getUnparsedText().orElse("");
            case final BlockHologramLine blockLine -> {
                final var block = blockLine.getBlock();
                yield "set block " + (block.getMaterial().isAir() ? "" : block.getAsString(true));
            }
            case final EntityHologramLine entityLine -> "set entity " + entityLine.getEntityType().getKey().asString();
            case final ItemHologramLine itemLine -> "set item " + toString(itemLine.getItemStack());
            case final PagedHologramLine ignored -> "set paged";
            default -> "";
        };
    }

    private String toString(final ItemStack item) {
        if (item.isEmpty()) return "";
        final var components = item.getItemMeta().getAsComponentString();
        final var key = item.getType().getKey().asString();
        if (components.equals("[]")) return key;
        return key + components;
    }
}
