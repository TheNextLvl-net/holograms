package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import org.bukkit.util.Transformation;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditLeftRotationCommand extends EditRotationCommand {
    private EditLeftRotationCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "left-rotation", resolver, Transformation::getLeftRotation, "hologram.left-rotation.query", "hologram.left-rotation");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        return create(new EditLeftRotationCommand(plugin, resolver));
    }
}
