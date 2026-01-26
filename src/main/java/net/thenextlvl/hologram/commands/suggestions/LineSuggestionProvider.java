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
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class LineSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    public static final LineSuggestionProvider INSTANCE = new LineSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        final var hologram = context.getLastChild().getArgument("hologram", Hologram.class);
        for (var index = 1; index <= hologram.getLineCount(); index++) {
            final var line = String.valueOf(index);
            if (!line.contains(builder.getRemaining())) continue;
            builder.suggest(line, getTooltip(hologram, index));
        }
        return builder.buildFuture();
    }

    private static Message getTooltip(final Hologram hologram, final int index) {
        final var tooltip = switch (hologram.getLine(index - 1).orElse(null)) {
            case final BlockHologramLine blockLine -> Component.text("Block: ")
                    .append(Component.translatable(blockLine.getBlock().getMaterial()));
            case final EntityHologramLine entityLine -> Component.text("Entity: ")
                    .append(Component.translatable(entityLine.getEntityType()));
            case final ItemHologramLine itemLine -> Component.text("Item: ")
                    .append(Component.translatable(itemLine.getItemStack().getType()));
            case final TextHologramLine textLine -> textLine.getUnparsedText().map(component -> {
                return MiniMessage.miniMessage().deserialize("Text: " + component.replace("\n", "\\n"));
            }).orElse(Component.empty());
            case null -> Component.empty();
            default -> Component.text("Unknown Line " + index);
        };
        return MessageComponentSerializer.message().serialize(tooltip);
    }
}
