package net.thenextlvl.hologram.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;
import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND;

@NullMarked
final class HologramTeleportCommand extends SimpleCommand {
    private HologramTeleportCommand(HologramPlugin plugin) {
        super(plugin, "teleport", "holograms.command.teleport");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramTeleportCommand(plugin);
        var position = Commands.argument("position", ArgumentTypes.finePosition())
                .then(Commands.argument("world", ArgumentTypes.world()).executes(command))
                .executes(command);
        var entity = Commands.argument("target", ArgumentTypes.entity()).executes(command);
        return command.create().then(hologramArgument(plugin)
                .then(position)
                .then(entity)
                .executes(command));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var sender = context.getSource().getSender();

        var hologram = context.getArgument("hologram", Hologram.class);
        var target = resolveArgument(context, "target", EntitySelectorArgumentResolver.class)
                .map(entity -> entity.getFirst().getLocation()).orElse(null);
        var position = target == null ? resolveArgument(context, "position", FinePositionResolver.class).map(resolved -> {
            return resolved.toLocation(tryGetArgument(context, "world", World.class)
                    .orElseGet(context.getSource().getLocation()::getWorld));
        }).orElse(null) : target;

        if (position == null && sender instanceof Player player) {
            player.teleportAsync(hologram.getLocation(), COMMAND).thenAccept(success -> {
                var message = success ? "hologram.teleport.success" : "hologram.teleport.failed";
                plugin.bundle().sendMessage(sender, message, Placeholder.parsed("hologram", hologram.getName()));
            });
            return SINGLE_SUCCESS;
        } else if (position != null) {
            hologram.teleportAsync(position).thenAccept(success -> {
                var message = success ? "hologram.move.success" : "hologram.move.failed";
                plugin.bundle().sendMessage(sender, message, Placeholder.parsed("hologram", hologram.getName()));
            });
            return SINGLE_SUCCESS;
        } else {
            plugin.bundle().sendMessage(sender, "command.sender");
            return 0;
        }
    }
}
