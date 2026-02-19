package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.set.EditSetCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramEditCommand {
    public static RequiredArgumentBuilder<CommandSourceStack, Integer> create(
            final HologramPlugin plugin,
            final RequiredArgumentBuilder<CommandSourceStack, Integer> line,
            final LineTargetResolver.Builder resolver
    ) {
        return line.then(EditAlignmentCommand.create(plugin, resolver))
                .then(EditAppendCommand.create(plugin, resolver))
                .then(EditBackgroundColorCommand.create(plugin, resolver))
                .then(EditBillboardCommand.create(plugin, resolver))
                .then(EditBrightnessCommand.create(plugin, resolver))
                .then(EditDefaultBackgroundCommand.create(plugin, resolver))
                .then(EditDisplayHeightCommand.create(plugin, resolver))
                .then(EditGlowColorCommand.create(plugin, resolver))
                .then(EditGlowingCommand.create(plugin, resolver))
                .then(EditInterpolationDelayCommand.create(plugin, resolver))
                .then(EditInterpolationDurationCommand.create(plugin, resolver))
                .then(EditLeftRotationCommand.create(plugin, resolver))
                .then(EditLineWidthCommand.create(plugin, resolver))
                .then(EditOffsetCommand.create(plugin, resolver))
                .then(EditOpacityCommand.create(plugin, resolver))
                .then(EditPlayerHeadCommand.create(plugin, resolver))
                .then(EditPrependCommand.create(plugin, resolver))
                .then(EditReplaceCommand.create(plugin, resolver))
                .then(EditRightRotationCommand.create(plugin, resolver))
                .then(EditScaleCommand.create(plugin, resolver))
                .then(EditSeeThroughCommand.create(plugin, resolver))
                .then(EditSetCommand.create(plugin, resolver))
                .then(EditShadowedCommand.create(plugin, resolver))
                .then(EditShadowRadiusCommand.create(plugin, resolver))
                .then(EditShadowStrengthCommand.create(plugin, resolver))
                .then(EditTeleportDurationCommand.create(plugin, resolver))
                .then(EditTransformationCommand.create(plugin, resolver))
                .then(EditViewRangeCommand.create(plugin, resolver));
    }
}
