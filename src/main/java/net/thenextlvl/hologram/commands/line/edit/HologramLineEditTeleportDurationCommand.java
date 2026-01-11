package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.DisplayHologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditTeleportDurationCommand extends SimpleCommand {
    private HologramLineEditTeleportDurationCommand(HologramPlugin plugin) {
        super(plugin, "teleport-duration", "holograms.command.line.edit.teleport-duration");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditTeleportDurationCommand(plugin);
        var named = Commands.argument("duration", ArgumentTypes.time());
        return command.create().then(named.executes(command));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var line = hologram.getLine(context.getArgument("line", int.class) - 1);
        var duration = context.getArgument("duration", int.class);
        if (line instanceof DisplayHologramLine<?, ?> displayLine) {
            displayLine.setTeleportDuration(duration);
        }
        // todo: send message
        return SINGLE_SUCCESS;
    }
}
