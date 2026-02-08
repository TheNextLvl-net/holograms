package net.thenextlvl.hologram.commands.action.argument;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ActionTypes;
import net.thenextlvl.hologram.commands.action.ActionTargetResolver;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class SendActionbarCommand extends HologramStringActionCommand {
    private SendActionbarCommand(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        super(plugin, ActionTypes.types().sendActionbar(), "send-actionbar", "message", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final ActionTargetResolver.Builder resolver) {
        return new SendActionbarCommand(plugin, resolver).create();
    }
}
