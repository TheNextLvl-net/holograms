package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.arguments.EnumArgumentType;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditAlignmentCommand extends SimpleCommand {
    private HologramLineEditAlignmentCommand(HologramPlugin plugin) {
        super(plugin, "alignment", "holograms.command.line.edit.alignment");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditAlignmentCommand(plugin);
        var named = Commands.argument("alignment", new EnumArgumentType<>(TextAlignment.class));
        return command.create().then(named.executes(command));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var line = hologram.getLine(context.getArgument("line", int.class) - 1);
        var alignment = context.getArgument("alignment", TextAlignment.class);
        if (line instanceof TextHologramLine textLine) {
            textLine.setAlignment(alignment);
        }
        // todo: send message
        return SINGLE_SUCCESS;
    }
}
