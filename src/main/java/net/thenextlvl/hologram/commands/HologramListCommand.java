package net.thenextlvl.hologram.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramListCommand extends SimpleCommand {
    private HologramListCommand(HologramPlugin plugin) {
        super(plugin, "list", "holograms.command.list");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramListCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> commandContext) {
        var sender = commandContext.getSource().getSender();
        var holograms = plugin.hologramController().getHolograms()
                .map(Hologram::getName)
                .map(Component::text)
                .toList();
        var message = holograms.isEmpty() ? "hologram.list.empty" : "hologram.list";
        plugin.bundle().sendMessage(sender, message,
                Formatter.number("amount", holograms.size()),
                Formatter.joining("holograms", holograms));
        return SINGLE_SUCCESS;
    }
}
