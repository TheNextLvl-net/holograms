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
final class EditAppendCommand extends EditCommand {
    private EditAppendCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "append", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditAppendCommand(plugin, resolver);
        final var text = Commands.argument("text", StringArgumentType.greedyString())
                .suggests(new TagSuggestionProvider<>(plugin));
        return command.create().then(text.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var text = context.getArgument("text", String.class);
            final var current = line.getUnparsedText().orElse(null);
            final var newText = current == null ? text : current.concat(text);
            final var message = set(current, newText, line::setUnparsedText, "hologram.text.set");
            plugin.bundle().sendMessage(context.getSource().getSender(), message, placeholders);
            return SINGLE_SUCCESS;
        }, LineType.TEXT);
    }
}
