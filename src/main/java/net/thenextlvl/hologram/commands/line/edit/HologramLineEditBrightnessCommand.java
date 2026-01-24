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
import net.thenextlvl.hologram.line.DisplayHologramLine;
import org.bukkit.entity.Display.Brightness;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditBrightnessCommand extends SimpleCommand {
    private HologramLineEditBrightnessCommand(final HologramPlugin plugin) {
        super(plugin, "brightness", "holograms.command.line.edit.brightness");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineEditBrightnessCommand(plugin);
        final var argument = IntegerArgumentType.integer(0, 15);
        final var brightness = Commands.argument("brightness", argument);
        final var blockLight = Commands.argument("block light", argument);
        final var skyLight = Commands.argument("sky light", argument);
        return command.create()
                .then(brightness.executes(command))
                .then(blockLight.then(skyLight.executes(command)))
                .then(Commands.literal("reset").executes(command));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int run(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var line = context.getArgument("line", int.class);
        final var brightness = tryGetArgument(context, "brightness", int.class).map(
                b -> new Brightness(b, b)
        ).or(() -> tryGetArgument(context, "block light", int.class).map(blockLight -> {
            final var skyLight = context.getArgument("sky light", int.class);
            return new Brightness(blockLight, skyLight);
        })).orElse(null);

        final var message = hologram.getLine(line - 1, DisplayHologramLine.class).map(displayLine -> {
            if (displayLine.getBrightness().orElse(null) == brightness) return "nothing.changed";
            displayLine.setBrightness(brightness);
            return brightness != null ? "hologram.brightness" : "hologram.brightness.reset";
        }).orElse("hologram.type.display");

        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("hologram", hologram.getName()),
                Formatter.number("line", line));
        return SINGLE_SUCCESS;
    }
}
