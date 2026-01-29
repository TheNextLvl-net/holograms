package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import net.thenextlvl.hologram.line.DisplayHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditScaleCommand extends EditCommand {
    private EditScaleCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "scale", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditScaleCommand(plugin, resolver);
        final var x = Commands.argument("x", FloatArgumentType.floatArg(0.1F, 100.0F));
        final var y = Commands.argument("y", FloatArgumentType.floatArg(0.1F, 100.0F));
        final var z = Commands.argument("z", FloatArgumentType.floatArg(0.1F, 100.0F));
        final var scale = Commands.argument("scale", FloatArgumentType.floatArg(0.1F, 100.0F));
        return command.create()
                .then(scale.executes(command))
                .then(x.then(y.then(z.executes(command))));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var scale = tryGetArgument(context, "scale", float.class).map(Vector3f::new).orElseGet(() -> {
                final Float x = context.getArgument("x", float.class);
                final Float y = context.getArgument("y", float.class);
                final Float z = context.getArgument("z", float.class);
                return new Vector3f(x, y, z);
            });

            final String message;
            if (line instanceof final DisplayHologramLine displayLine) {
                final var transformation = displayLine.getTransformation();
                message = set(transformation.getScale(), scale, (vector3f) -> {
                    transformation.getScale().set(vector3f);
                    displayLine.setTransformation(transformation);
                }, "hologram.scale");
            } else if (line instanceof final EntityHologramLine entityLine) {
                message = set(entityLine.getScale(), scale.y(), value -> {
                    entityLine.setScale(scale.y());
                }, "hologram.scale");
            } else throw new IllegalArgumentException("Invalid line type");

            plugin.bundle().sendMessage(context.getSource().getSender(), message, placeholders);
            return SINGLE_SUCCESS;
        }, LineType.STATIC);
    }
}
