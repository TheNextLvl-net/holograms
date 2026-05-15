package net.thenextlvl.hologram.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramListCommand extends SimpleCommand {
    private HologramListCommand(final HologramPlugin plugin) {
        super(plugin, "list", "holograms.command.list");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramListCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> commandContext) {
        final var sender = commandContext.getSource().getSender();
        final var holograms = plugin.hologramProvider().getHolograms().map(hologram -> {
            return plugin.bundle().component("hologram.list.entry", sender, 
                    Placeholder.parsed("hologram", hologram.getName()));
        }).toList();
        final var message = holograms.isEmpty() ? "hologram.list.empty" : "hologram.list";
        plugin.bundle().sendMessage(sender, message,
                Formatter.number("amount", holograms.size()),
                Formatter.joining("holograms", holograms));
        return SINGLE_SUCCESS;
    }
}
