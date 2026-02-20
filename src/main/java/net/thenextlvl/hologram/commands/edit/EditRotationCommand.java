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
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

@NullMarked
abstract sealed class EditRotationCommand extends EditCommand permits EditLeftRotationCommand, EditRightRotationCommand {
    private final Function<Transformation, Quaternionf> rotationGetter;
    private final String queryKey, successKey;

    protected EditRotationCommand(
            final HologramPlugin plugin, final String name,
            final LineTargetResolver.Builder resolver,
            final Function<Transformation, Quaternionf> rotationGetter,
            final String queryKey, final String successKey
    ) {
        super(plugin, name, resolver);
        this.queryKey = queryKey;
        this.successKey = successKey;
        this.rotationGetter = rotationGetter;
    }

    protected static LiteralArgumentBuilder<CommandSourceStack> create(final EditRotationCommand command) {
        final var x = Commands.argument("x", FloatArgumentType.floatArg());
        final var y = Commands.argument("y", FloatArgumentType.floatArg());
        final var z = Commands.argument("z", FloatArgumentType.floatArg());
        final var w = Commands.argument("w", FloatArgumentType.floatArg());
        return command.create()
                .then(x.then(y.then(z
                        .then(w.executes(command))
                        .executes(command))))
                .executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var rotation = tryGetArgument(context, "x", float.class).map(x -> {
                final var y = context.getArgument("y", float.class);
                final var z = context.getArgument("z", float.class);
                final var w = tryGetArgument(context, "w", float.class).orElse(1f);
                return new Quaternionf(x, y, z, w);
            });

            final var transformation = line.getTransformation();
            final var current = rotationGetter.apply(transformation);
            final var message = rotation.map(quaternion -> {
                return set(quaternion, (v) -> {
                    current.set(v);
                    return line.setTransformation(transformation);
                }, successKey);
            }).orElse(queryKey);

            final var ored = rotationGetter.apply(line.getTransformation());
            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    TagResolver.resolver(placeholders),
                    Formatter.number("x", ored.x()),
                    Formatter.number("y", ored.y()),
                    Formatter.number("z", ored.z()),
                    Formatter.number("w", ored.w()));
            return SINGLE_SUCCESS;
        }, LineType.DISPLAY);
    }
}
