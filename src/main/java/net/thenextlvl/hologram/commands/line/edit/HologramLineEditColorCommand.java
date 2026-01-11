package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditColorCommand extends SimpleCommand {
    private HologramLineEditColorCommand(HologramPlugin plugin) {
        super(plugin, "color", "holograms.command.line.edit.color");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditColorCommand(plugin);
        var named = Commands.argument("color", ArgumentTypes.namedColor());
        var hex = Commands.argument("hex", ArgumentTypes.hexColor())
                .suggests((context, builder) -> builder.buildFuture());
        return command.create()
                .then(named.executes(command))
                .then(hex.executes(command))
                .then(Commands.literal("reset").executes(command));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var line = hologram.getLine(context.getArgument("line", int.class) - 1, TextHologramLine.class);
        var color = tryGetArgument(context, "hex", TextColor.class)
                .or(() -> tryGetArgument(context, "color", NamedTextColor.class))
                .orElse(null);
        line.ifPresent(textLine -> {
            textLine.getText().map(component -> component.color(color))
                    .ifPresent(textLine::setText);
            // todo: send message
        });
        return SINGLE_SUCCESS;
    }
}
