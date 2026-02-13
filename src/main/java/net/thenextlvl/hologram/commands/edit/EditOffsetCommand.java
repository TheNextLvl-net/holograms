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
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditOffsetCommand extends EditCommand {
    private EditOffsetCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "offset", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditOffsetCommand(plugin, resolver);
        final var x = Commands.argument("x", FloatArgumentType.floatArg(-16, 16));
        final var y = Commands.argument("y", FloatArgumentType.floatArg(-16, 16));
        final var z = Commands.argument("z", FloatArgumentType.floatArg(-16, 16));
        return command.create().then(x.then(y.then(z.executes(command)))).executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var offset = tryGetArgument(context, "x", float.class).map(x -> {
                final var y = context.getArgument("y", float.class);
                final var z = context.getArgument("z", float.class);
                return new Vector3f(x, y, z);
            });

            final var ored = offset.orElseGet(line::getOffset);
            final var message = offset.map(vector3f -> {
                return set(line.getOffset(), vector3f, line::setOffset, "hologram.offset");
            }).orElse("hologram.offset.query");
            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    TagResolver.resolver(placeholders),
                    Formatter.number("x", ored.x()),
                    Formatter.number("y", ored.y()),
                    Formatter.number("z", ored.z()));
            return SINGLE_SUCCESS;
        }, LineType.ENTITY);
    }
}
