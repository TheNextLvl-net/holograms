package net.thenextlvl.hologram.commands.page;

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
import net.thenextlvl.hologram.commands.suggestions.PageSuggestionProvider;
import net.thenextlvl.hologram.commands.suggestions.tags.TagSuggestionProvider;
import net.thenextlvl.hologram.line.PagedHologramLine;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiConsumer;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.thenextlvl.hologram.commands.HologramCommand.hologramArgument;

@NullMarked
public final class HologramPageInsertCommand extends BrigadierCommand {
    private HologramPageInsertCommand(final HologramPlugin plugin) {
        super(plugin, "insert", "holograms.command.page.insert");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramPageInsertCommand(plugin);
        final var line = Commands.argument("line", IntegerArgumentType.integer(1))
                .suggests(LineSuggestionProvider.PAGED_ONLY);
        final var page = Commands.argument("page", IntegerArgumentType.integer(1))
                .suggests(PageSuggestionProvider.INSTANCE);
        return command.create().then(hologramArgument(plugin, true).then(line.then(page
                .then(command.insertPage("block", ArgumentTypes.blockState(), command::insertBlockPage, plugin))
                .then(command.insertPage("entity", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE), command::insertEntityPage, plugin))
                .then(command.insertPage("item", ArgumentTypes.itemStack(), command::insertItemPage, plugin))
                .then(Commands.literal("text").then(Commands.argument("text", StringArgumentType.greedyString())
                        .suggests(new TagSuggestionProvider<>(plugin))
                        .executes(context -> command.insertPage(context, plugin, command::insertTextPage)))))));
    }

    private LiteralArgumentBuilder<CommandSourceStack> insertPage(
            final String name, final ArgumentType<?> argumentType,
            final BiConsumer<PagedHologramLine, CommandContext<CommandSourceStack>> inserter,
            final HologramPlugin plugin
    ) {
        return Commands.literal(name).then(Commands.argument(name, argumentType)
                .executes(context -> insertPage(context, plugin, inserter)));
    }

    private int insertPage(
            final CommandContext<CommandSourceStack> context,
            final HologramPlugin plugin,
            final BiConsumer<PagedHologramLine, CommandContext<CommandSourceStack>> inserter
    ) {
        final var sender = context.getSource().getSender();
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var lineIndex = context.getArgument("line", int.class) - 1;
        final var pageIndex = context.getArgument("page", int.class) - 1;
        final var line = hologram.getLine(lineIndex).orElse(null);

        if (line == null) {
            plugin.bundle().sendMessage(sender, "hologram.line.invalid");
            return 0;
        }
        if (!(line instanceof final PagedHologramLine pagedLine)) {
            plugin.bundle().sendMessage(sender, "hologram.type.paged");
            return 0;
        }

        if (pageIndex < 0 || pageIndex > pagedLine.getPageCount()) {
            plugin.bundle().sendMessage(sender, "hologram.page.invalid",
                    Placeholder.unparsed("hologram", hologram.getName()),
                    Formatter.number("line", lineIndex + 1),
                    Formatter.number("page", pageIndex + 1));
            return 0;
        }

        inserter.accept(pagedLine, context);
        plugin.bundle().sendMessage(sender, "hologram.page.insert",
                Placeholder.unparsed("hologram", hologram.getName()),
                Formatter.number("line", lineIndex + 1),
                Formatter.number("page", pageIndex + 1));
        return SINGLE_SUCCESS;
    }

    private void insertBlockPage(final PagedHologramLine pagedLine, final CommandContext<CommandSourceStack> context) {
        final var block = context.getArgument("block", BlockState.class).getBlockData();
        final var pageIndex = context.getArgument("page", int.class) - 1;
        pagedLine.insertBlockPage(pageIndex).setBlock(block);
    }

    private void insertEntityPage(final PagedHologramLine pagedLine, final CommandContext<CommandSourceStack> context) {
        final var entity = context.getArgument("entity", EntityType.class);
        final var pageIndex = context.getArgument("page", int.class) - 1;
        pagedLine.insertEntityPage(pageIndex, entity);
    }

    private void insertItemPage(final PagedHologramLine pagedLine, final CommandContext<CommandSourceStack> context) {
        final var item = context.getArgument("item", ItemStack.class);
        final var pageIndex = context.getArgument("page", int.class) - 1;
        pagedLine.insertItemPage(pageIndex).setItemStack(item);
    }

    private void insertTextPage(final PagedHologramLine pagedLine, final CommandContext<CommandSourceStack> context) {
        final var text = context.getArgument("text", String.class);
        final var pageIndex = context.getArgument("page", int.class) - 1;
        pagedLine.insertTextPage(pageIndex).setUnparsedText(text);
    }
}
