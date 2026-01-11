package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.DisplayHologramLine;
import org.bukkit.entity.Display.Brightness;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditBrightnessCommand extends SimpleCommand {
    private HologramLineEditBrightnessCommand(HologramPlugin plugin) {
        super(plugin, "brightness", "holograms.command.line.edit.brightness");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditBrightnessCommand(plugin);
        var argument = IntegerArgumentType.integer(0, 15);
        var brightness = Commands.argument("brightness", argument);
        var blockLight = Commands.argument("block light", argument);
        var skyLight = Commands.argument("sky light", argument);
        return command.create()
                .then(brightness.executes(command))
                .then(blockLight.then(skyLight.executes(command)))
                .then(Commands.literal("reset").executes(command));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var line = hologram.getLine(context.getArgument("line", int.class) - 1, DisplayHologramLine.class);
        var brightness = tryGetArgument(context, "brightness", int.class).map(
                b -> new Brightness(b, b)
        ).or(() -> tryGetArgument(context, "block light", int.class).map(blockLight -> {
            var skyLight = context.getArgument("sky light", int.class);
            return new Brightness(blockLight, skyLight);
        })).orElse(null);
        line.ifPresent(displayLine -> displayLine.setBrightness(brightness));
        // todo: send message
        return SINGLE_SUCCESS;
    }
}
