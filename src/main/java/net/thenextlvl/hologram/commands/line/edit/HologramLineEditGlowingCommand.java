package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditGlowingCommand extends SimpleCommand {
    private HologramLineEditGlowingCommand(final HologramPlugin plugin) {
        super(plugin, "glowing", "holograms.command.line.edit.glowing");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineEditGlowingCommand(plugin);
        final var glowing = Commands.argument("glowing", BoolArgumentType.bool());
        return command.create().then(glowing.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        final var glowing = context.getArgument("glowing", boolean.class);
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var line = context.getArgument("line", int.class);

        final var message = hologram.getLine(line - 1).map(hologramLine -> {
            if (hologramLine.isGlowing() == glowing) return "nothing.changed";
            hologramLine.setGlowing(glowing);
            return glowing ? "hologram.line.glow.enabled" : "hologram.line.glow.disabled";
        }).orElse("hologram.line.invalid");

        plugin.bundle().sendMessage(sender, message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", line));
        return SINGLE_SUCCESS;
    }
}
