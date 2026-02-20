package net.thenextlvl.hologram.commands.line;

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
import net.thenextlvl.hologram.commands.suggestions.tags.TagSuggestionProvider;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
public final class HologramLineAddCommand extends BrigadierCommand {
    private HologramLineAddCommand(final HologramPlugin plugin) {
        super(plugin, "add", "holograms.command.line.add");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineAddCommand(plugin);
        return command.create().then(hologramArgument(plugin)
                .then(command.addLine("block", ArgumentTypes.blockState(), command::addBlockLine))
                .then(command.addLine("entity", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE), command::addEntityLine))
                .then(command.addLine("item", ArgumentTypes.itemStack(), command::addItemLine))
                .then(Commands.literal("text").then(Commands.argument("text", StringArgumentType.greedyString())
                        .suggests(new TagSuggestionProvider<>(plugin))
                        .executes(command::addTextLine)))
                .then(Commands.literal("paged").executes(command::addPagedLine)));
    }

    private LiteralArgumentBuilder<CommandSourceStack> addLine(final String name, final ArgumentType<?> argumentType, final Command<CommandSourceStack> command) {
        return Commands.literal(name).then(Commands.argument(name, argumentType).executes(command));
    }

    private int addBlockLine(final CommandContext<CommandSourceStack> context) {
        final var block = context.getArgument("block", BlockState.class).getBlockData();
        return addLine(context, hologram -> hologram.addBlockLine().setBlock(block));
    }

    private int addEntityLine(final CommandContext<CommandSourceStack> context) {
        final var entity = context.getArgument("entity", EntityType.class);
        return addLine(context, hologram -> hologram.addEntityLine(entity));
    }

    private int addItemLine(final CommandContext<CommandSourceStack> context) {
        final var item = context.getArgument("item", ItemStack.class);
        return addLine(context, hologram -> hologram.addItemLine().setItemStack(item));
    }

    private int addTextLine(final CommandContext<CommandSourceStack> context) {
        final var text = context.getArgument("text", String.class);
        return addLine(context, hologram -> hologram.addTextLine().setUnparsedText(text));
    }

    private int addPagedLine(final CommandContext<CommandSourceStack> context) {
        return addLine(context, Hologram::addPagedLine);
    }

    private int addLine(final CommandContext<CommandSourceStack> context, final Consumer<Hologram> consumer) {
        final var hologram = context.getArgument("hologram", Hologram.class);
        consumer.accept(hologram);
        plugin.bundle().sendMessage(context.getSource().getSender(), "hologram.line.add",
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", hologram.getLineCount()));
        return SINGLE_SUCCESS;
    }
}
