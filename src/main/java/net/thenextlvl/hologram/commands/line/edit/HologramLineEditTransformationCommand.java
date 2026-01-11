package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.arguments.EnumArgumentType;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.ItemHologramLine;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditTransformationCommand extends SimpleCommand {
    private HologramLineEditTransformationCommand(HologramPlugin plugin) {
        super(plugin, "transformation", "holograms.command.line.edit.transformation");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditTransformationCommand(plugin);
        var named = Commands.argument("transformation", new EnumArgumentType<>(ItemDisplayTransform.class));
        return command.create().then(named.executes(command));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var line = hologram.getLine(context.getArgument("line", int.class) - 1);
        var transformation = context.getArgument("transformation", ItemDisplayTransform.class);
        if (line instanceof ItemHologramLine itemLine) {
            itemLine.setItemDisplayTransform(transformation);
        }
        // todo: send message
        return SINGLE_SUCCESS;
    }
}
