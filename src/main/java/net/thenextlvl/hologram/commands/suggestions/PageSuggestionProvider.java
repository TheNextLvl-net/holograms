package net.thenextlvl.hologram.commands.suggestions;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.HologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class PageSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    public static final PageSuggestionProvider INSTANCE = new PageSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        final var hologram = context.getLastChild().getArgument("hologram", Hologram.class);
        final var lineIndex = context.getLastChild().getArgument("line", int.class) - 1;
        final var line = hologram.getLine(lineIndex, PagedHologramLine.class);

        if (line.isEmpty()) return builder.buildFuture();

        final var pagedLine = line.get();
        final var pages = pagedLine.getPages();

        for (var index = 1; index <= pages.size(); index++) {
            final var page = String.valueOf(index);
            if (!page.contains(builder.getRemaining())) continue;
            builder.suggest(page, getTooltip(pages.get(index - 1)));
        }
        return builder.buildFuture();
    }

    private static Message getTooltip(final HologramLine page) {
        final var tooltip = switch (page) {
            case final BlockHologramLine blockLine -> Component.text("Block: ")
                    .append(Component.translatable(blockLine.getBlock().getMaterial()));
            case final EntityHologramLine entityLine -> Component.text("Entity: ")
                    .append(Component.translatable(entityLine.getEntityType()));
            case final ItemHologramLine itemLine -> Component.text("Item: ")
                    .append(Component.translatable(itemLine.getItemStack().getType()));
            case final TextHologramLine textLine -> textLine.getUnparsedText().map(component -> {
                return MiniMessage.miniMessage().deserialize("Text: " + component.replace("\n", "\\n"));
            }).orElse(Component.empty());
            default -> Component.text("Unknown");
        };
        return MessageComponentSerializer.message().serialize(tooltip);
    }
}
