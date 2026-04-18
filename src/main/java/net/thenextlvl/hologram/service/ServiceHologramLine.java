package net.thenextlvl.hologram.service;

import net.thenextlvl.service.hologram.Hologram;
import net.thenextlvl.service.hologram.LineType;
import net.thenextlvl.service.hologram.line.HologramLine;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
abstract class ServiceHologramLine<L extends net.thenextlvl.hologram.line.HologramLine> implements HologramLine {
    protected final ServiceHologram hologram;
    protected final L line;

    protected ServiceHologramLine(final ServiceHologram hologram, final L line) {
        this.hologram = hologram;
        this.line = line;
    }

    @Override
    public LineType getType() {
        return switch (line.getType()) {
            case BLOCK -> LineType.BLOCK;
            case ITEM -> LineType.ITEM;
            case TEXT -> LineType.TEXT;
            default -> LineType.ENTITY;
        };
    }

    @Override
    public World getWorld() {
        return line.getWorld();
    }

    @Override
    public Hologram getHologram() {
        return hologram;
    }

    @Override
    public Optional<String> getViewPermission() {
        return line.getViewPermission();
    }

    @Override
    public boolean setViewPermission(@Nullable final String permission) {
        return line.setViewPermission(permission);
    }

    @Override
    public boolean canSee(final Player player) {
        return line.canSee(player);
    }
}
