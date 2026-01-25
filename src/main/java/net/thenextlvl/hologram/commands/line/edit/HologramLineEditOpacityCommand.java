package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditOpacityCommand extends SimpleCommand {
    private HologramLineEditOpacityCommand(final HologramPlugin plugin) {
        super(plugin, "opacity", "holograms.command.line.edit.opacity");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineEditOpacityCommand(plugin);
        final var opacity = Commands.argument("opacity", IntegerArgumentType.integer(0, 100));
        return command.create().then(opacity.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        final var opacity = context.getArgument("opacity", int.class);
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var line = context.getArgument("line", int.class);

        final var message = hologram.getLine(line - 1).map(hologramLine -> {
            if (!(hologramLine instanceof final TextHologramLine textLine))
                return "hologram.type.text";
            if (textLine.getTextOpacity() == opacity) return "nothing.changed";
            textLine.setTextOpacity(opacity);
            return "hologram.line.opacity";
        }).orElse("hologram.line.invalid");

        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", line),
                Formatter.number("opacity", opacity));
        return SINGLE_SUCCESS;
    }
}
