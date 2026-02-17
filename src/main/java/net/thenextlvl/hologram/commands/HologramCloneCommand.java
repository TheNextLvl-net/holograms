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
final class HologramCloneCommand extends SimpleCommand {
    private HologramCloneCommand(final HologramPlugin plugin) {
        super(plugin, "clone", "holograms.command.clone");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramCloneCommand(plugin);
        return command.create().then(hologramArgument(plugin)
                .then(nameArgument().executes(command)));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var name = context.getArgument("name", String.class);
        final var sender = context.getSource().getSender();

        final var placeholder = Placeholder.parsed("hologram", name);
        if (plugin.hologramProvider().hasHologram(name)) {
            plugin.bundle().sendMessage(sender, "hologram.exists", placeholder);
            return 0;
        }

        plugin.hologramProvider().spawnHologram(name, context.getSource().getLocation(), clone -> clone.copyFrom(hologram));
        plugin.bundle().sendMessage(sender, "hologram.cloned", placeholder);
        return SINGLE_SUCCESS;
    }
}
