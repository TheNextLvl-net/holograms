package net.thenextlvl.hologram.plugin.commands.brigadier;

import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.hologram.plugin.HologramPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public abstract class SimpleCommand extends BrigadierCommand implements Command<CommandSourceStack> {
    protected SimpleCommand(final HologramPlugin plugin, final String name, @Nullable final String permission) {
        super(plugin, name, permission);
    }
}
