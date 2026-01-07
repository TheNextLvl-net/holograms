package net.thenextlvl.hologram.commands.line.edit;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.commands.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HologramLineEditReplaceCommand extends BrigadierCommand {
    private HologramLineEditReplaceCommand(HologramPlugin plugin) {
        super(plugin, "replace", "holograms.command.line.edit.replace");
    }

    // todo: hologram line edit <hologram> <index> replace <match> <text>
    public static LiteralArgumentBuilder<CommandSourceStack> create(HologramPlugin plugin) {
        var command = new HologramLineEditReplaceCommand(plugin);
        return command.create();
    }
}
