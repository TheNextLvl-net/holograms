package net.thenextlvl.hologram.commands.edit.set;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.EditCommand;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import net.thenextlvl.hologram.commands.suggestions.tags.TagSuggestionProvider;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditSetTextCommand extends EditCommand {
    private EditSetTextCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "text", null, resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditSetTextCommand(plugin, resolver);
        final var text = Commands.argument("text", StringArgumentType.greedyString())
                .suggests(new TagSuggestionProvider<>(plugin));
        return command.create().then(text.executes(command));
    }

    protected int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) {
        return EditSetCommand.setLine(resolver, LineType.TEXT, (line) -> {
            final String text = context.getArgument("text", String.class);
            line.setUnparsedText(text);
        }, Hologram::setTextLine, PagedHologramLine::setTextPage);
    }
}
