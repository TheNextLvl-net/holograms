package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.DisplayHologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditInterpolationDurationCommand extends SimpleCommand {
    private HologramLineEditInterpolationDurationCommand(HologramPlugin plugin) {
        super(plugin, "interpolation-duration", "holograms.command.line.edit.interpolation-duration");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditInterpolationDurationCommand(plugin);
        var named = Commands.argument("duration", ArgumentTypes.time());
        return command.create().then(named.executes(command));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var lineNumber = context.getArgument("line", int.class);
        var duration = context.getArgument("duration", int.class);

        var message = hologram.getLine(lineNumber - 1, DisplayHologramLine.class).map(displayLine -> {
            if (displayLine.getInterpolationDuration() == duration) return "nothing.changed";
            displayLine.setInterpolationDuration(duration);
            return "hologram.interpolation-duration";
        }).orElse("hologram.type.display");

        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("hologram", hologram.getName()),
                Formatter.number("line", lineNumber),
                Formatter.number("duration", duration));
        return SINGLE_SUCCESS;
    }
}
