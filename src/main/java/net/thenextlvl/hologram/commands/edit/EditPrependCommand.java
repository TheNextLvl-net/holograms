package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import net.thenextlvl.hologram.commands.suggestions.tags.TagSuggestionProvider;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditPrependCommand extends EditCommand {
    private EditPrependCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "prepend", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditPrependCommand(plugin, resolver);
        final var text = Commands.argument("text", StringArgumentType.greedyString())
                .suggests(new TagSuggestionProvider<>(plugin));
        return command.create().then(text.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var text = context.getArgument("text", String.class);
            final var current = line.getUnparsedText().orElse(null);
            final var newText = current == null ? text : text.concat(current);
            final var message = set(newText, line::setUnparsedText, "hologram.text.set");
            plugin.bundle().sendMessage(context.getSource().getSender(), message, placeholders);
            return SINGLE_SUCCESS;
        }, LineType.TEXT);
    }
}
