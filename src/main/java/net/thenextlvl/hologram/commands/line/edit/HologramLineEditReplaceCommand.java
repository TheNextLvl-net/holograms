package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditReplaceCommand extends SimpleCommand {
    private HologramLineEditReplaceCommand(HologramPlugin plugin) {
        super(plugin, "replace", "holograms.command.line.edit.replace");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditReplaceCommand(plugin);
        var match = Commands.argument("match", StringArgumentType.string());
        var text = Commands.argument("text", StringArgumentType.string());
        return command.create().then(match.then(text.executes(command)));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var match = context.getArgument("match", String.class);
        var text = context.getArgument("text", String.class);
        var line = hologram.getLine(hologram.getLineCount() - context.getArgument("line", int.class));

        if (line instanceof TextHologramLine textLine) {
            textLine.getText().map(MiniMessage.miniMessage()::serialize)
                    .map(s -> s.replace(match, text))
                    .map(MiniMessage.miniMessage()::deserialize)
                    .ifPresent(textLine::setText);
            // todo: send message
        }
        return SINGLE_SUCCESS;
    }
}
