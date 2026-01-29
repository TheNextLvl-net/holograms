package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import org.bukkit.entity.Display;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditBrightnessCommand extends EditCommand {
    private EditBrightnessCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "brightness", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditBrightnessCommand(plugin, resolver);
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
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var brightness = tryGetArgument(context, "brightness", int.class)
                    .map((b) -> new Display.Brightness(b, b))
                    .or(() -> tryGetArgument(context, "block light", int.class).map((blockLight) -> {
                        final var skyLight = context.getArgument("sky light", int.class);
                        return new Display.Brightness(blockLight, skyLight);
                    })).orElse(null);
            final var successKey = brightness != null ? "hologram.brightness" : "hologram.brightness.reset";
            final var message = set(line.getBrightness().orElse(null), brightness, line::setBrightness, successKey);
            plugin.bundle().sendMessage(context.getSource().getSender(), message, placeholders);
            return SINGLE_SUCCESS;
        }, LineType.DISPLAY);
    }
}
