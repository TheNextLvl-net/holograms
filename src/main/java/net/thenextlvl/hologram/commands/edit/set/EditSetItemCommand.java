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
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditSetItemCommand extends EditCommand {
    private EditSetItemCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "item", null, resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final EditSetItemCommand command = new EditSetItemCommand(plugin, resolver);
        final RequiredArgumentBuilder<CommandSourceStack, ItemStack> item = Commands.argument("item", ArgumentTypes.itemStack());
        return command.create().then(item.executes(command));
    }

    protected int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) {
        return EditSetCommand.setLine(resolver, LineType.ITEM, (line) -> {
            final ItemStack item = context.getArgument("item", ItemStack.class);
            line.setItemStack(item);
        }, Hologram::setItemLine, PagedHologramLine::setItemPage);
    }
}
