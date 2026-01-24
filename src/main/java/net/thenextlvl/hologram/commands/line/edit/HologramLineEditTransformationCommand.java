package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.arguments.EnumArgumentType;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.ItemHologramLine;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditTransformationCommand extends SimpleCommand {
    private HologramLineEditTransformationCommand(final HologramPlugin plugin) {
        super(plugin, "transformation", "holograms.command.line.edit.transformation");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineEditTransformationCommand(plugin);
        final var named = Commands.argument("transformation", new EnumArgumentType<>(ItemDisplayTransform.class));
        return command.create().then(named.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineNumber = context.getArgument("line", int.class);
        final var transformation = context.getArgument("transformation", ItemDisplayTransform.class);

        final var message = hologram.getLine(lineNumber - 1, ItemHologramLine.class).map(itemLine -> {
            if (itemLine.getItemDisplayTransform() == transformation) return "nothing.changed";
            itemLine.setItemDisplayTransform(transformation);
            return "hologram.transformation";
        }).orElse("hologram.type.item");

        final var transformationName = plugin.bundle().component(switch (transformation) {
            case FIRSTPERSON_LEFTHAND -> "transformation.firstperson-lefthand";
            case FIRSTPERSON_RIGHTHAND -> "transformation.firstperson-righthand";
            case FIXED -> "transformation.fixed";
            case GROUND -> "transformation.ground";
            case GUI -> "transformation.gui";
            case HEAD -> "transformation.head";
            case NONE -> "transformation.none";
            case THIRDPERSON_LEFTHAND -> "transformation.thirdperson-lefthand";
            case THIRDPERSON_RIGHTHAND -> "transformation.thirdperson-righthand";
        }, sender);

        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("hologram", hologram.getName()),
                Placeholder.component("transformation", transformationName),
                Formatter.number("line", lineNumber));
        return SINGLE_SUCCESS;
    }
}
