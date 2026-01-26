package net.thenextlvl.hologram.commands.page;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.suggestions.LineSuggestionProvider;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
public final class HologramPageListCommand extends BrigadierCommand {
    private HologramPageListCommand(final HologramPlugin plugin) {
        super(plugin, "list", "holograms.command.page.list");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageListCommand(plugin);
        final var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.INSTANCE);
        return command.create().then(hologramArgument(plugin).then(line
                .executes(command::listPages)));
    }

    private int listPages(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineIndex = context.getArgument("line", int.class) - 1;
        final var line = hologram.getLine(lineIndex, PagedHologramLine.class);

        if (line.isEmpty()) {
            plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.type.paged");
            return 0;
        }

        final var pagedLine = line.get();
        final var sender = context.getSource().getSender();

        plugin.bundle().sendMessage(sender, "hologram.page.list.header",
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineIndex + 1),
                Formatter.number("count", pagedLine.getPageCount()));

        final var pages = pagedLine.getPages();
        for (var i = 0; i < pages.size(); i++) {
            final var page = pages.get(i);
            plugin.bundle().sendMessage(sender, "hologram.page.list.entry",
                    Formatter.number("index", i + 1),
                    Placeholder.unparsed("type", getPageType(page)),
                    Placeholder.component("preview", getPagePreview(page)));
        }

        return SINGLE_SUCCESS;
    }

    private String getPageType(final HologramLine page) {
        return switch (page) {
            case final TextHologramLine ignored -> "text";
            case final ItemHologramLine ignored -> "item";
            case final BlockHologramLine ignored -> "block";
            case final EntityHologramLine ignored -> "entity";
            default -> "unknown";
        };
    }

    private Component getPagePreview(final HologramLine page) {
        return switch (page) {
            case final TextHologramLine textLine -> Component.text(textLine.getUnparsedText().orElse("(empty)"));
            case final ItemHologramLine itemLine -> Component.translatable(itemLine.getItemStack());
            case final BlockHologramLine blockLine -> Component.text(blockLine.getBlock().getMaterial().name());
            case final EntityHologramLine entityLine -> Component.text(entityLine.getEntityType().name());
            default -> Component.text("?");
        };
    }
}
