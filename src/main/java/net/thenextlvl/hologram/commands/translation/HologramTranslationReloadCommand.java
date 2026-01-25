package net.thenextlvl.hologram.commands.translation;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class HologramTranslationReloadCommand extends SimpleCommand {
    private HologramTranslationReloadCommand(final HologramPlugin plugin) {
        super(plugin, "reload", "holograms.command.translation.reload");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramTranslationReloadCommand(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var sender = context.getSource().getSender();
        final var success = plugin.translations().read();
        if (success) plugin.updateHologramTextLines(null);
        final var message = success ? "hologram.translation.reload" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message);
        return success ? SINGLE_SUCCESS : 0;
    }
}
