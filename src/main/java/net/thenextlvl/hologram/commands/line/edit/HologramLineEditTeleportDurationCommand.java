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
final class HologramLineEditTeleportDurationCommand extends SimpleCommand {
    private HologramLineEditTeleportDurationCommand(final HologramPlugin plugin) {
        super(plugin, "teleport-duration", "holograms.command.line.edit.teleport-duration");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineEditTeleportDurationCommand(plugin);
        final var named = Commands.argument("duration", ArgumentTypes.time());
        return command.create().then(named.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineNumber = context.getArgument("line", int.class);
        final var duration = context.getArgument("duration", int.class);

        final var message = hologram.getLine(lineNumber - 1, DisplayHologramLine.class).map(displayLine -> {
            if (displayLine.getTeleportDuration() == duration) return "nothing.changed";
            displayLine.setTeleportDuration(duration);
            return "hologram.teleport-duration";
        }).orElse("hologram.type.display");

        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineNumber),
                Formatter.number("duration", duration));
        return SINGLE_SUCCESS;
    }
}
