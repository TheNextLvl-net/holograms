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
final class HologramLineEditPrependCommand extends SimpleCommand {
    private HologramLineEditPrependCommand(HologramPlugin plugin) {
        super(plugin, "prepend", "holograms.command.line.edit.prepend");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditPrependCommand(plugin);
        var text = Commands.argument("text", StringArgumentType.greedyString());
        return command.create().then(text.executes(command));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var text = MiniMessage.miniMessage().deserialize(context.getArgument("text", String.class));
        var line = hologram.getLine(context.getArgument("line", int.class) - 1, TextHologramLine.class);

        line.ifPresent(textLine -> {
            textLine.getText().map(text::append).ifPresent(textLine::setText);
            // todo: send message
        });
        return SINGLE_SUCCESS;
    }
}
