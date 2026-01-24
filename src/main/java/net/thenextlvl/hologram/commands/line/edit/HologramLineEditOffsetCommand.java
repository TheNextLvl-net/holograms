package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.DisplayHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditOffsetCommand extends SimpleCommand {
    private HologramLineEditOffsetCommand(final HologramPlugin plugin) {
        super(plugin, "offset", "holograms.command.line.edit.offset");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineEditOffsetCommand(plugin);
        final var x = Commands.argument("x", FloatArgumentType.floatArg());
        final var y = Commands.argument("y", FloatArgumentType.floatArg());
        final var z = Commands.argument("z", FloatArgumentType.floatArg());
        return command.create()
                .then(Commands.literal("reset").executes(command))
                .then(x.then(y.then(z.executes(command))));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineNumber = context.getArgument("line", int.class);
        final var offset = tryGetArgument(context, "x", float.class).map(x -> {
            final var y = context.getArgument("y", float.class);
            final var z = context.getArgument("z", float.class);
            return new Vector3f(x, y, z);
        }).orElseGet(Vector3f::new);

        final var message = hologram.getLine(lineNumber - 1).map(line -> {
            if (line instanceof final DisplayHologramLine<?, ?> displayLine) {
                final var transformation = displayLine.getTransformation();
                if (transformation.getTranslation().equals(offset)) return "nothing.changed";
                transformation.getTranslation().set(offset);
                displayLine.setTransformation(transformation);
            } else if (line instanceof final EntityHologramLine<?> entityLine) {
                if (entityLine.getOffset().equals(offset)) return "nothing.changed";
                entityLine.setOffset(offset);
            }
            return "hologram.offset";
        }).orElse("hologram.line.invalid");

        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineNumber));
        return SINGLE_SUCCESS;
    }
}
