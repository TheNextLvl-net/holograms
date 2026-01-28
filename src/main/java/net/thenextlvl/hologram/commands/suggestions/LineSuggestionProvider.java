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
import net.thenextlvl.hologram.line.LineType;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class LineSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    public static final LineSuggestionProvider ANY_LINE = new LineSuggestionProvider(false);
    public static final LineSuggestionProvider PAGED_ONLY = new LineSuggestionProvider(true);

    private final boolean pagedOnly;

    private LineSuggestionProvider(final boolean pagedOnly) {
        this.pagedOnly = pagedOnly;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        final var hologram = context.getLastChild().getArgument("hologram", Hologram.class);
        for (var index = 1; index <= hologram.getLineCount(); index++) {
            final var line = String.valueOf(index);
            if (!line.contains(builder.getRemaining())) continue;
            final var hologramLine = hologram.getLine(index - 1).orElse(null);
            if (hologramLine == null || (pagedOnly && hologramLine.getType() != LineType.PAGED)) continue;
            builder.suggest(line, getTooltip(hologramLine));
        }
        return builder.buildFuture();
    }

    static Message getTooltip(final HologramLine line) {
        final var tooltip = switch (line) {
            case final BlockHologramLine blockLine -> Component.text("Block: ")
                    .append(Component.translatable(blockLine.getBlock().getMaterial()));
            case final EntityHologramLine entityLine -> Component.text("Entity: ")
                    .append(Component.translatable(entityLine.getEntityType()));
            case final ItemHologramLine itemLine -> Component.text("Item: ")
                    .append(Component.translatable(itemLine.getItemStack().getType()));
            case final TextHologramLine textLine -> textLine.getUnparsedText().map(component -> {
                return MiniMessage.miniMessage().deserialize("Text: " + component.replace("\n", "\\n"));
            }).orElse(Component.empty());
            case final PagedHologramLine pagedLine -> Component.text("Paged: ")
                    .append(Component.text(pagedLine.getPageCount()));
            default -> Component.text("Unknown Line " + line.getClass().getName());
        };
        return MessageComponentSerializer.message().serialize(tooltip);
    }
}
