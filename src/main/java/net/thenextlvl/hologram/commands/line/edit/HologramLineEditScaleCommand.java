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
final class HologramLineEditScaleCommand extends SimpleCommand {
    private HologramLineEditScaleCommand(HologramPlugin plugin) {
        super(plugin, "scale", "holograms.command.line.edit.scale");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditScaleCommand(plugin);
        var x = Commands.argument("x", FloatArgumentType.floatArg(0.1f));
        var y = Commands.argument("y", FloatArgumentType.floatArg(0.1f));
        var z = Commands.argument("z", FloatArgumentType.floatArg(0.1f));
        var scale = Commands.argument("scale", FloatArgumentType.floatArg(0.1f));
        return command.create()
                .then(scale.executes(command))
                .then(x.then(y.then(z.executes(command))));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var lineNumber = context.getArgument("line", int.class);
        var scale = tryGetArgument(context, "scale", float.class).map(Vector3f::new).orElseGet(() -> {
            var x = context.getArgument("x", float.class);
            var y = context.getArgument("y", float.class);
            var z = context.getArgument("z", float.class);
            return new Vector3f(x, y, z);
        });

        var message = hologram.getLine(lineNumber - 1).map(line -> {
            if (line instanceof DisplayHologramLine<?, ?> displayLine) {
                var transformation = displayLine.getTransformation();
                transformation.getScale().set(scale);
                displayLine.setTransformation(transformation);
            } else if (line instanceof EntityHologramLine<?> entityLine) {
                entityLine.setScale(scale.y());
            } else return "hologram.type.display";
            return "hologram.scale";
        }).orElse("hologram.line.invalid");

        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("hologram", hologram.getName()),
                Formatter.number("line", lineNumber));
        return SINGLE_SUCCESS;
    }
}
