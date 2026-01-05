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
import static net.thenextlvl.hologram.commands.HologramCommand.nameArgument;

@NullMarked
public final class HologramRenameCommand extends SimpleCommand {
    private HologramRenameCommand(HologramPlugin plugin) {
        super(plugin, "rename", "holograms.command.rename");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramRenameCommand(plugin);
        return command.create().then(hologramArgument(plugin)
                .then(nameArgument().executes(command)));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var hologram = context.getArgument("hologram", Hologram.class);
        var name = context.getArgument("name", String.class);
        var sender = context.getSource().getSender();

        if (plugin.hologramProvider().hasHologram(name)) {
            plugin.bundle().sendMessage(sender, "hologram.exists", Placeholder.parsed("hologram", name));
            return 0;
        }

        var oldName = hologram.getName();
        var success = hologram.setName(name);
        var message = success ? "hologram.renamed" : "nothing.changed";

        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("hologram", oldName),
                Placeholder.parsed("name", name));
        return success ? SINGLE_SUCCESS : 0;
    }
}
