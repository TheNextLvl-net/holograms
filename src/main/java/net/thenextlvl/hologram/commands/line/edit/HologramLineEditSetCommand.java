package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.line.BlockHologramLine;
import net.thenextlvl.hologram.line.EntityHologramLine;
import net.thenextlvl.hologram.line.ItemHologramLine;
import net.thenextlvl.hologram.line.TextHologramLine;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiPredicate;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@NullMarked
final class HologramLineEditSetCommand extends BrigadierCommand {
    private HologramLineEditSetCommand(final HologramPlugin plugin) {
        super(plugin, "set", "holograms.command.line.edit.set");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineEditSetCommand(plugin);
        return command.create()
                .then(command.setLine("block", ArgumentTypes.blockState(), command::setBlockLine))
                .then(command.setLine("entity", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE), command::setEntityLine))
                .then(command.setLine("item", ArgumentTypes.itemStack(), command::setItemLine))
                .then(command.setLine("text", StringArgumentType.greedyString(), command::setTextLine));
    }

    private LiteralArgumentBuilder<CommandSourceStack> setLine(final String name, final ArgumentType<?> argumentType, final Command<CommandSourceStack> command) {
        return Commands.literal(name).then(Commands.argument(name, argumentType).executes(command));
    }

    private int setBlockLine(final CommandContext<CommandSourceStack> context) {
        final var block = context.getArgument("block", BlockState.class).getBlockData();
        return setLine(context, (hologram, line) -> {
            final var blockLine = hologram.getLine(line, BlockHologramLine.class)
                    .orElseGet(() -> hologram.setBlockLine(line));
            if (block.equals(blockLine.getBlock())) return false;
            blockLine.setBlock(block);
            return true;
        });
    }

    private int setEntityLine(final CommandContext<CommandSourceStack> context) {
        final var entity = context.getArgument("entity", EntityType.class);
        return setLine(context, (hologram, line) -> {
            if (entity.equals(hologram.getLine(line, EntityHologramLine.class)
                    .map(EntityHologramLine::getEntityType)
                    .orElse(null))) return false;
            final var scale = hologram.getLine(line, EntityHologramLine.class)
                    .map(EntityHologramLine::getScale);
            final var entityLine = hologram.setEntityLine(entity, line);
            scale.ifPresent(entityLine::setScale);
            return true;
        });
    }

    private int setItemLine(final CommandContext<CommandSourceStack> context) {
        final var item = context.getArgument("item", ItemStack.class);
        return setLine(context, (hologram, line) -> {
            final var itemLine = hologram.getLine(line, ItemHologramLine.class)
                    .orElseGet(() -> hologram.setItemLine(line));
            if (item.equals(itemLine.getItemStack())) return false;
            itemLine.setItemStack(item);
            return true;
        });
    }

    private int setTextLine(final CommandContext<CommandSourceStack> context) {
        final var text = context.getArgument("text", String.class);
        return setLine(context, (hologram, line) -> {
            final var textLine = hologram.getLine(line, TextHologramLine.class)
                    .orElseGet(() -> hologram.setTextLine(line));
            if (text.equals(textLine.getUnparsedText().orElse(null))) return false;
            textLine.setUnparsedText(text);
            return true;
        });
    }

    private <T> int setLine(final CommandContext<CommandSourceStack> context, final BiPredicate<Hologram, Integer> setter) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var line = context.getArgument("line", int.class);
        final var valid = line <= hologram.getLineCount();
        final var success = valid && setter.test(hologram, line - 1);
        final var message = success ? "hologram.line.set" : valid
                ? "nothing.changed" : "hologram.line.invalid";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", line));
        return success ? SINGLE_SUCCESS : 0;
    }
}
