package net.thenextlvl.hologram.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
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
        return command.create().then(hologramArgument(plugin).executes(command));
    }

    @Override
    protected boolean canUse(CommandSourceStack source) {
        return super.canUse(source) && source.getSender() instanceof Player;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var sender = (Player) context.getSource().getSender();
        sender.teleportAsync(hologram.getLocation(), COMMAND);
        return SINGLE_SUCCESS;
    }
}
