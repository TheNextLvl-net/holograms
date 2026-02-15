package net.thenextlvl.hologram.service;

import net.thenextlvl.hologram.line.DisplayHologramLine;
import net.thenextlvl.service.api.hologram.HologramDisplay;
import net.thenextlvl.service.api.hologram.HologramLine;
import net.thenextlvl.service.api.hologram.LineType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
abstract class ServiceHologramLine<T, L extends net.thenextlvl.hologram.line.HologramLine> implements HologramLine<T> {
    protected final L line;

    protected ServiceHologramLine(final L line) {
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
    public Location getLocation() {
        return line.getHologram().getLocation();
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public Optional<HologramDisplay> getDisplay() {
        return line instanceof final DisplayHologramLine display ? Optional.of(new ServiceHologramDisplay(display)) : Optional.empty();
    }

    @Override
    public World getWorld() {
        return line.getWorld();
    }

    @Override
    public double getX() {
        return line.getHologram().getLocation().getX();
    }

    @Override
    public double getY() {
        return line.getHologram().getLocation().getY();
    }

    @Override
    public double getZ() {
        return line.getHologram().getLocation().getZ();
    }

    @Override
    public float getPitch() {
        return line.getHologram().getLocation().getPitch();
    }

    @Override
    public float getYaw() {
        return line.getHologram().getLocation().getYaw();
    }

    @Override
    public double getHeight() {
        return 0;
    }

    @Override
    public void setHeight(final double height) {
    }
}
