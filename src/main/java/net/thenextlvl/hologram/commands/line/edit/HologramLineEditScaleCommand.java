package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
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
        var x = Commands.argument("x", FloatArgumentType.floatArg(0));
        var y = Commands.argument("y", FloatArgumentType.floatArg(0));
        var z = Commands.argument("z", FloatArgumentType.floatArg(0));
        var scale = Commands.argument("scale", FloatArgumentType.floatArg(0));
        return command.create()
                .then(scale.executes(command))
                .then(x.then(y.then(z.executes(command))));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var scale = tryGetArgument(context, "scale", float.class).map(Vector3f::new).orElseGet(() -> {
            var x = context.getArgument("x", float.class);
            var y = context.getArgument("y", float.class);
            var z = context.getArgument("z", float.class);
            return new Vector3f(x, y, z);
        });
        
        var line = hologram.getLine(hologram.getLineCount() - context.getArgument("line", int.class));
        if (line instanceof DisplayHologramLine<?, ?> displayLine) {
            var transformation = displayLine.getTransformation();
            transformation.getScale().set(scale);
            displayLine.setTransformation(transformation);
        } else if (line instanceof EntityHologramLine<?> entityLine) {
            entityLine.setScale(scale.y());
        }
        // todo: send message
        return SINGLE_SUCCESS;
    }
}
