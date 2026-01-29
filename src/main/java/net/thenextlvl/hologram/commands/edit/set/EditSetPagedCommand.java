package net.thenextlvl.hologram.commands.edit.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.EditCommand;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditSetPagedCommand extends EditCommand {
    private EditSetPagedCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "paged", null, resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        if (resolver.equals(LineTargetResolver.PAGE)) {
            throw new IllegalArgumentException("Cannot set paged line for page");
        } else {
            final EditSetPagedCommand command = new EditSetPagedCommand(plugin, resolver);
            return command.create().executes(command);
        }
    }

    protected int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) {
        return EditSetCommand.setLine(resolver, LineType.PAGED, (line) -> {
        }, Hologram::setPagedLine, (idk, id) -> {
            throw new IllegalArgumentException("Cannot set paged line for paged hologram");
        });
    }
}
