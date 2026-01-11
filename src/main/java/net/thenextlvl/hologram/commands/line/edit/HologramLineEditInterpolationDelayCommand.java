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
final class HologramLineEditInterpolationDelayCommand extends SimpleCommand {
    private HologramLineEditInterpolationDelayCommand(HologramPlugin plugin) {
        super(plugin, "interpolation-delay", "holograms.command.line.edit.interpolation-delay");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditInterpolationDelayCommand(plugin);
        var named = Commands.argument("delay", ArgumentTypes.time());
        return command.create().then(named.executes(command));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var line = hologram.getLine(context.getArgument("line", int.class) - 1, DisplayHologramLine.class);
        var delay = context.getArgument("delay", int.class);
        line.ifPresent(displayLine -> displayLine.setInterpolationDelay(delay));
        // todo: send message
        return SINGLE_SUCCESS;
    }
}
