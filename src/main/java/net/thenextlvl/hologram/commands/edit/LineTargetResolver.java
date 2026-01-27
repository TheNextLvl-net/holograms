package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * Strategy interface for resolving the target line of an edit operation.
 * Implementations handle the difference between editing a direct hologram line
 * vs editing a page within a paged line.
 */
@NullMarked
@FunctionalInterface
public interface LineTargetResolver {
    /**
     * Resolves the target line from the command context.
     * Implementations should send appropriate error messages if resolution fails.
     *
     * @param context the command context
     * @param plugin  the plugin instance for sending messages
     * @return the resolved target, or empty if resolution failed
     */
    Optional<LineEditTarget> resolve(CommandContext<CommandSourceStack> context, HologramPlugin plugin);
}
