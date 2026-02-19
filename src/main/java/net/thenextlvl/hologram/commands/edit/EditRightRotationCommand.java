package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import org.bukkit.util.Transformation;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditRightRotationCommand extends EditRotationCommand {
    private EditRightRotationCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "right-rotation", resolver, Transformation::getRightRotation, "hologram.right-rotation.query", "hologram.right-rotation");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        return create(new EditRightRotationCommand(plugin, resolver));
    }
}
