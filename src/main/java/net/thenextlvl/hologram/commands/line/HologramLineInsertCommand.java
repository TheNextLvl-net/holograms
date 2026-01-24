package net.thenextlvl.hologram.commands.line;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.suggestions.LineSuggestionProvider;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiConsumer;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
public final class HologramLineInsertCommand extends BrigadierCommand {
    private HologramLineInsertCommand(final HologramPlugin plugin) {
        super(plugin, "insert", "holograms.command.line.insert");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineInsertCommand(plugin);
        final var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.INSTANCE);
        return command.create().then(hologramArgument(plugin).then(line
                .then(command.createLine("block", ArgumentTypes.blockState(), command::insertBlockLine))
                .then(command.createLine("entity", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE), command::insertEntityLine))
                .then(command.createLine("item", ArgumentTypes.itemStack(), command::insertItemLine))
                .then(command.createLine("text", StringArgumentType.greedyString(), command::insertTextLine))));
    }

    private LiteralArgumentBuilder<CommandSourceStack> createLine(final String name, final ArgumentType<?> argumentType, final Command<CommandSourceStack> command) {
        return Commands.literal(name).then(Commands.argument(name, argumentType).executes(command));
    }

    private int insertBlockLine(final CommandContext<CommandSourceStack> context) {
        final var block = context.getArgument("block", BlockState.class).getBlockData();
        return insertLine(context, (hologram, line) -> hologram.addBlockLine(line).setBlock(block));
    }

    private int insertEntityLine(final CommandContext<CommandSourceStack> context) {
        final var entity = context.getArgument("entity", EntityType.class);
        return insertLine(context, (hologram, line) -> hologram.addEntityLine(entity, line));
    }

    private int insertItemLine(final CommandContext<CommandSourceStack> context) {
        final var item = context.getArgument("item", ItemStack.class);
        return insertLine(context, (hologram, line) -> hologram.addItemLine(line).setItemStack(item));
    }

    private int insertTextLine(final CommandContext<CommandSourceStack> context) {
        final var text = MiniMessage.miniMessage().deserialize(context.getArgument("text", String.class));
        return insertLine(context, (hologram, line) -> hologram.addTextLine(line).setText(text));
    }

    private int insertLine(final CommandContext<CommandSourceStack> context, final BiConsumer<Hologram, Integer> consumer) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var line = context.getArgument("line", int.class);
        consumer.accept(hologram, line - 1);
        plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.line.insert",
                Placeholder.parsed("hologram", hologram.getName()),
                Formatter.number("line", line));
        return SINGLE_SUCCESS;
    }
}
