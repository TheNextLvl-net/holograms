package net.thenextlvl.hologram.commands.page;

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
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import net.thenextlvl.hologram.commands.suggestions.LineSuggestionProvider;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
public final class HologramPageAddCommand extends BrigadierCommand {
    private HologramPageAddCommand(final HologramPlugin plugin) {
        super(plugin, "add", "holograms.command.page.add");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageAddCommand(plugin);
        final var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.PAGED_ONLY);
        return command.create().then(hologramArgument(plugin, true).then(line
                .then(command.addPage("block", ArgumentTypes.blockState(), command::addBlockPage))
                .then(command.addPage("entity", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE), command::addEntityPage))
                .then(command.addPage("item", ArgumentTypes.itemStack(), command::addItemPage))
                .then(command.addPage("text", StringArgumentType.greedyString(), command::addTextPage))));
    }

    private LiteralArgumentBuilder<CommandSourceStack> addPage(final String name, final ArgumentType<?> argumentType, final Command<CommandSourceStack> command) {
        return Commands.literal(name).then(Commands.argument(name, argumentType).executes(command));
    }

    private int addBlockPage(final CommandContext<CommandSourceStack> context) {
        final var block = context.getArgument("block", BlockState.class).getBlockData();
        return addPage(context, pagedLine -> pagedLine.addBlockPage().setBlock(block));
    }

    private int addEntityPage(final CommandContext<CommandSourceStack> context) {
        final var entity = context.getArgument("entity", EntityType.class);
        return addPage(context, pagedLine -> pagedLine.addEntityPage(entity));
    }

    private int addItemPage(final CommandContext<CommandSourceStack> context) {
        final var item = context.getArgument("item", ItemStack.class);
        return addPage(context, pagedLine -> pagedLine.addItemPage().setItemStack(item));
    }

    private int addTextPage(final CommandContext<CommandSourceStack> context) {
        final var text = context.getArgument("text", String.class);
        return addPage(context, pagedLine -> pagedLine.addTextPage().setUnparsedText(text));
    }

    private int addPage(final CommandContext<CommandSourceStack> context, final Consumer<PagedHologramLine> consumer) {
        final var sender = context.getSource().getSender();
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineIndex = context.getArgument("line", int.class) - 1;
        final var line = hologram.getLine(lineIndex).orElse(null);

        if (line == null) {
            plugin.bundle().sendMessage(sender, "hologram.line.invalid");
            return 0;
        }
        if (!(line instanceof final PagedHologramLine pagedLine)) {
            plugin.bundle().sendMessage(sender, "hologram.type.paged");
            return 0;
        }

        consumer.accept(pagedLine);
        plugin.bundle().sendMessage(sender, "hologram.page.add",
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineIndex + 1),
                Formatter.number("page", pagedLine.getPageCount()));
        return SINGLE_SUCCESS;
    }
}
