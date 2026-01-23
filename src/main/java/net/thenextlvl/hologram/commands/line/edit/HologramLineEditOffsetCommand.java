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
    private HologramLineEditOffsetCommand(HologramPlugin plugin) {
        super(plugin, "offset", "holograms.command.line.edit.offset");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditOffsetCommand(plugin);
        var x = Commands.argument("x", FloatArgumentType.floatArg());
        var y = Commands.argument("y", FloatArgumentType.floatArg());
        var z = Commands.argument("z", FloatArgumentType.floatArg());
        return command.create()
                .then(Commands.literal("reset").executes(command))
                .then(x.then(y.then(z.executes(command))));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var lineNumber = context.getArgument("line", int.class);
        var offset = tryGetArgument(context, "x", float.class).map(x -> {
            var y = context.getArgument("y", float.class);
            var z = context.getArgument("z", float.class);
            return new Vector3f(x, y, z);
        }).orElseGet(Vector3f::new);

        var message = hologram.getLine(lineNumber - 1).map(line -> {
            if (line instanceof DisplayHologramLine<?, ?> displayLine) {
                var transformation = displayLine.getTransformation();
                if (transformation.getTranslation().equals(offset)) return "nothing.changed";
                transformation.getTranslation().set(offset);
                displayLine.setTransformation(transformation);
            } else if (line instanceof EntityHologramLine<?> entityLine) {
                if (entityLine.getOffset().equals(offset)) return "nothing.changed";
                entityLine.setOffset(offset);
            }
            return "hologram.offset";
        }).orElse("hologram.line.invalid");

        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("hologram", hologram.getName()),
                Formatter.number("line", lineNumber));
        return SINGLE_SUCCESS;
    }
}
