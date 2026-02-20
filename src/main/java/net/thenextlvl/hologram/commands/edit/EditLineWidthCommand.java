package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditLineWidthCommand extends EditCommand {
    private EditLineWidthCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "line-width", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditLineWidthCommand(plugin, resolver);
        final var width = Commands.argument("width", IntegerArgumentType.integer(1));
        return command.create()
                .then(width.executes(command))
                .then(Commands.literal("reset").executes(command::reset))
                .executes(command);
    }

    private int reset(final CommandContext<CommandSourceStack> context) {
        final var resolver = this.resolver.build(context, this.plugin);
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var message = set(Integer.MAX_VALUE, line::setLineWidth, "hologram.line-width.reset");
            plugin.bundle().sendMessage(context.getSource().getSender(), message, placeholders);
            return SINGLE_SUCCESS;
        }, LineType.TEXT);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var width = tryGetArgument(context, "width", int.class);

            final var message = width.map(value -> {
                return set(value, line::setLineWidth, "hologram.line-width");
            }).orElse("hologram.line-width.query");

            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    TagResolver.resolver(placeholders),
                    Formatter.number("width", width.orElseGet(line::getLineWidth)));
            return SINGLE_SUCCESS;
        }, LineType.TEXT);
    }
}
