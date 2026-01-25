package net.thenextlvl.hologram.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
final class HologramDeleteCommand extends SimpleCommand {
    private HologramDeleteCommand(final HologramPlugin plugin) {
        super(plugin, "delete", "holograms.command.delete");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramDeleteCommand(plugin);
        return command.create().then(hologramArgument(plugin).executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var success = plugin.hologramProvider().deleteHologram(hologram);
        final var message = success ? "hologram.delete" : "hologram.delete.failed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("hologram", hologram.getName()));
        return SINGLE_SUCCESS;
    }
}
