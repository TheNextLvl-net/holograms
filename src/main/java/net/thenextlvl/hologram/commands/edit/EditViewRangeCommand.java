package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.arguments.FloatArgumentType;
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
final class EditViewRangeCommand extends EditCommand {
    private EditViewRangeCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "view-range", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditViewRangeCommand(plugin, resolver);
        final var range = Commands.argument("range", FloatArgumentType.floatArg(0f, 100f));
        return command.create().then(range.executes(command)).executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var range = tryGetArgument(context, "range", float.class).map(value -> value / 100f);

            final var message = range.map(value -> {
                return set(line.getViewRange(), value, line::setViewRange, "hologram.view-range");
            }).orElse("hologram.view-range.query");

            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    TagResolver.resolver(placeholders),
                    Formatter.number("range", range.orElseGet(line::getViewRange) * 100f));
            return SINGLE_SUCCESS;
        }, LineType.DISPLAY);
    }
}
