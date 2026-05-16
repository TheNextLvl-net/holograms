package net.thenextlvl.hologram.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramHelpCommand extends SimpleCommand {
    private static final String DOCS_URL = "https://thenextlvl.net/docs/holograms";

    private HologramHelpCommand(final HologramPlugin plugin) {
        super(plugin, "help", null);
    }

    public static ArgumentBuilder<CommandSourceStack, ?> create(final HologramPlugin plugin) {
        final var command = new HologramHelpCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        plugin.bundle().sendMessage(sender, "hologram.documentation", Placeholder.parsed("url", DOCS_URL));
        return SINGLE_SUCCESS;
    }
}
