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
final class HologramLineEditInterpolationDelayCommand extends SimpleCommand {
    private HologramLineEditInterpolationDelayCommand(final HologramPlugin plugin) {
        super(plugin, "interpolation-delay", "holograms.command.line.edit.interpolation-delay");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineEditInterpolationDelayCommand(plugin);
        final var named = Commands.argument("delay", ArgumentTypes.time());
        return command.create().then(named.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineNumber = context.getArgument("line", int.class);
        final var delay = context.getArgument("delay", int.class);

        final var message = hologram.getLine(lineNumber - 1, DisplayHologramLine.class).map(displayLine -> {
            if (displayLine.getInterpolationDelay() == delay) return "nothing.changed";
            displayLine.setInterpolationDelay(delay);
            return "hologram.interpolation-delay";
        }).orElse("hologram.type.display");

        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineNumber),
                Formatter.number("delay", delay));
        return SINGLE_SUCCESS;
    }
}
