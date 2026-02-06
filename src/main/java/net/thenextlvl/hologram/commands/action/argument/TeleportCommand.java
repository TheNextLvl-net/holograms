package net.thenextlvl.hologram.commands.action.argument;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.RotationResolver;
import io.papermc.paper.math.Rotation;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ActionTypes;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class TeleportCommand extends HologramActionCommand<Location> {
    private TeleportCommand(final HologramPlugin plugin) {
        super(plugin, ActionTypes.types().teleport(), "teleport");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new TeleportCommand(plugin);
        final var position = Commands.argument("position", ArgumentTypes.finePosition()).executes(command);
        final var rotation = Commands.argument("rotation", ArgumentTypes.rotation()).executes(command);
        final var world = Commands.argument("world", ArgumentTypes.world()).executes(command);
        return command.create().then(position.then(rotation.then(world)));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var position = context.getArgument("position", FinePositionResolver.class).resolve(context.getSource());
        final var rotationResolver = tryGetArgument(context, "rotation", RotationResolver.class).orElse(null);

        final var world = tryGetArgument(context, "world", World.class).orElseGet(() -> context.getSource().getLocation().getWorld());
        final var rotation = rotationResolver != null ? rotationResolver.resolve(context.getSource()) : Rotation.rotation(0, 0);

        final var location = position.toLocation(world).setRotation(rotation);
        return addAction(context, location);
    }
}
