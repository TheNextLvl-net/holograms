package net.thenextlvl.hologram.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.command.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.command.HologramCommand.hologramArgument;

@NullMarked
final class HologramDeleteCommand extends SimpleCommand {
    private HologramDeleteCommand(HologramPlugin plugin) {
        super(plugin, "delete", "holograms.command.delete");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramDeleteCommand(plugin);
        return command.create().then(hologramArgument(plugin).executes(command));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var success = hologram.delete();
        var message = success ? "hologram.delete" : "hologram.delete.failed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("hologram", hologram.getName()));
        return SINGLE_SUCCESS;
    }
}
