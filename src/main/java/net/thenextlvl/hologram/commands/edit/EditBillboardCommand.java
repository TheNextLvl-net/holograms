package net.thenextlvl.hologram.commands.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.arguments.EnumArgumentType;
import net.thenextlvl.hologram.commands.edit.LineTargetResolver.LineType;
import org.bukkit.entity.Display;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class EditBillboardCommand extends EditCommand {
    private EditBillboardCommand(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        super(plugin, "billboard", resolver);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin, final LineTargetResolver.Builder resolver) {
        final var command = new EditBillboardCommand(plugin, resolver);
        final var named = Commands.argument("billboard", new EnumArgumentType<>(Display.Billboard.class));
        return command.create().then(named.executes(command)).executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context, final LineTargetResolver resolver) throws CommandSyntaxException {
        return resolver.resolve((hologram, line, lineIndex, pageIndex, placeholders) -> {
            final var billboard = tryGetArgument(context, "billboard", Display.Billboard.class);

            final var billboardName = plugin.bundle().component(switch (billboard.orElseGet(line::getBillboard)) {
                case FIXED -> "billboard.fixed";
                case VERTICAL -> "billboard.vertical";
                case HORIZONTAL -> "billboard.horizontal";
                case CENTER -> "billboard.center";
            }, context.getSource().getSender());

            final var message = billboard.map(value -> {
                return set(line.getBillboard(), value, line::setBillboard, "hologram.billboard");
            }).orElse("hologram.billboard.query");

            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    TagResolver.resolver(placeholders),
                    Placeholder.component("billboard", billboardName));
            return SINGLE_SUCCESS;
        }, LineType.STATIC);
    }
}
