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
    private HologramLineEditBillboardCommand(HologramPlugin plugin) {
        super(plugin, "billboard", "holograms.command.line.edit.billboard");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditBillboardCommand(plugin);
        var named = Commands.argument("billboard", new EnumArgumentType<>(Billboard.class));
        return command.create().then(named.executes(command));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var hologram = context.getArgument("hologram", Hologram.class);
        var line = context.getArgument("line", int.class);
        var billboard = context.getArgument("billboard", Billboard.class);

        var message = hologram.getLine(line - 1, DisplayHologramLine.class).map(textLine -> {
            if (textLine.getBillboard() == billboard) return "nothing.changed";
            textLine.setBillboard(billboard);
            return "hologram.billboard";
        }).orElse("hologram.type.display");

        var billboardName = plugin.bundle().component(switch (billboard) {
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
