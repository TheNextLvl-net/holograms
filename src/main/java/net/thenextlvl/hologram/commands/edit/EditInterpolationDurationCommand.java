package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditInterpolationDurationCommand extends EditCommand {
    private EditInterpolationDurationCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "interpolation-duration", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditInterpolationDurationCommand(plugin, resolver);
        final var named = Commands.argument("duration", ArgumentTypes.time());
        return command.create().then(named.executes(command)).executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var duration = tryGetArgument(context, "duration", int.class);

            final var message = duration.map(value -> {
                return set(line.getInterpolationDuration(), value, line::setInterpolationDuration, "hologram.interpolation-duration");
            }).orElse("hologram.interpolation-duration.query");

            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    TagResolver.resolver(placeholders),
                    Formatter.number("duration", duration.orElseGet(line::getInterpolationDuration)));
            return SINGLE_SUCCESS;
        }, LineType.DISPLAY);
    }
}
