package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.arguments.EnumArgumentType;
import net.thenextlvl.hologram.commands.brigadier.SimpleCommand;
import net.thenextlvl.hologram.line.DisplayHologramLine;
import org.bukkit.entity.Display.Billboard;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditBillboardCommand extends SimpleCommand {
    private HologramLineEditBillboardCommand(final HologramPlugin plugin) {
        super(plugin, "billboard", "holograms.command.line.edit.billboard");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final HologramPlugin plugin) {
        final var command = new HologramLineEditBillboardCommand(plugin);
        final var named = Commands.argument("billboard", new EnumArgumentType<>(Billboard.class));
        return command.create().then(named.executes(command));
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        final var hologram = context.getArgument("hologram", Hologram.class);
        final var line = context.getArgument("line", int.class);
        final var billboard = context.getArgument("billboard", Billboard.class);

        final var message = hologram.getLine(line - 1, DisplayHologramLine.class).map(textLine -> {
            if (textLine.getBillboard() == billboard) return "nothing.changed";
            textLine.setBillboard(billboard);
            return "hologram.billboard";
        }).orElse("hologram.type.display");

        final var billboardName = plugin.bundle().component(switch (billboard) {
            case FIXED -> "billboard.fixed";
            case VERTICAL -> "billboard.vertical";
            case HORIZONTAL -> "billboard.horizontal";
            case CENTER -> "billboard.center";
        }, sender);

        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("hologram", hologram.getName()),
                Placeholder.component("billboard", billboardName),
                Formatter.number("line", line));
        return SINGLE_SUCCESS;
    }
}
