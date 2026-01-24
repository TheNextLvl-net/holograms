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
    private HologramRenameCommand(final HologramPlugin plugin) {
        super(plugin, "rename", "holograms.command.rename");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramRenameCommand(plugin);
        return command.create().then(hologramArgument(plugin)
                .then(nameArgument().executes(command)));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var name = context.getArgument("name", String.class);
        final var sender = context.getSource().getSender();

        if (plugin.hologramProvider().hasHologram(name)) {
            plugin.bundle().sendMessage(sender, "hologram.exists", Placeholder.parsed("hologram", name));
            return 0;
        }

        final var oldName = hologram.getName();
        final var success = hologram.setName(name);
        final var message = success ? "hologram.renamed" : "nothing.changed";

        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("hologram", oldName),
                Placeholder.parsed("name", name));
        return success ? SINGLE_SUCCESS : 0;
    }
}
