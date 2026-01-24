package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.arguments.EnumArgumentType;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditAlignmentCommand extends SimpleCommand {
    private HologramLineEditAlignmentCommand(final HologramPlugin plugin) {
        super(plugin, "alignment", "holograms.command.line.edit.alignment");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineEditAlignmentCommand(plugin);
        final var named = Commands.argument("alignment", new EnumArgumentType<>(TextAlignment.class));
        return command.create().then(named.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        final var alignment = context.getArgument("alignment", TextAlignment.class);
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var line = context.getArgument("line", int.class);

        final var message = hologram.getLine(line - 1, TextHologramLine.class).map(textLine -> {
            if (textLine.getAlignment() == alignment) return "nothing.changed";
            textLine.setAlignment(alignment);
            return "hologram.text-alignment";
        }).orElse("hologram.type.text");

        final var alignmentName = plugin.bundle().component(switch (alignment) {
            case LEFT -> "text-alignment.left";
            case CENTER -> "text-alignment.center";
            case RIGHT -> "text-alignment.right";
        }, sender);

        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("hologram", hologram.getName()),
                Placeholder.component("alignment", alignmentName),
                Formatter.number("line", line));
        return SINGLE_SUCCESS;
    }
}
