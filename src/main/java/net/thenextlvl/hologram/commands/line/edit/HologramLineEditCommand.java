package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.suggestions.LineSuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
public final class HologramLineEditCommand extends BrigadierCommand {
    private HologramLineEditCommand(final HologramPlugin plugin) {
        super(plugin, "edit", "holograms.command.line.edit");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineEditCommand(plugin);
        final var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.INSTANCE);
        return command.create().then(hologramArgument(plugin).then(line
                .then(HologramLineEditAlignmentCommand.create(plugin))
                .then(HologramLineEditAppendCommand.create(plugin))
                .then(HologramLineEditBackgroundColorCommand.create(plugin))
                .then(HologramLineEditBillboardCommand.create(plugin))
                .then(HologramLineEditBrightnessCommand.create(plugin))
                .then(HologramLineEditGlowColorCommand.create(plugin))
                .then(HologramLineEditGlowingCommand.create(plugin))
                .then(HologramLineEditInterpolationDelayCommand.create(plugin))
                .then(HologramLineEditInterpolationDurationCommand.create(plugin))
                .then(HologramLineEditOffsetCommand.create(plugin))
                .then(HologramLineEditPrependCommand.create(plugin))
                .then(HologramLineEditReplaceCommand.create(plugin))
                .then(HologramLineEditScaleCommand.create(plugin))
                .then(HologramLineEditSetCommand.create(plugin))
                .then(HologramLineEditTeleportDurationCommand.create(plugin))
                .then(HologramLineEditTransformationCommand.create(plugin))));
    }
}
