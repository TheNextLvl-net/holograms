package net.thenextlvl.hologram.commands.edit.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.edit.EditCommand;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.bukkit.block.BlockState;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditSetBlockCommand extends EditCommand {
    private EditSetBlockCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "block", null, resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final EditSetBlockCommand command = new EditSetBlockCommand(plugin, resolver);
        final RequiredArgumentBuilder<CommandSourceStack, BlockState> block = Commands.argument("block", ArgumentTypes.blockState());
        return command.create().then(block.executes(command));
    }

    protected int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) {
        return EditSetCommand.setLine(resolver, LineType.BLOCK, (line) -> {
            final BlockState block = context.getArgument("block", BlockState.class);
            line.setBlock(block.getBlockData());
        }, Hologram::setBlockLine, PagedHologramLine::setBlockPage);
    }
}
