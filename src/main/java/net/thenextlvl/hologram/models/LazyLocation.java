package net.thenextlvl.hologram.models;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public final class LazyLocation extends Location implements Keyed {
    private final Key key;

    public LazyLocation(final Location location) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.key = location.getWorld().key();
    }

    public LazyLocation(final Key key, final double x, final double y, final double z, final float yaw, final float pitch) {
        super(Bukkit.getWorld(key), x, y, z, yaw, pitch);
        this.key = key;
    }

    @Override
    public @Nullable World getWorld() {
        var world = super.getWorld();
        if (world != null) return world;

        world = Bukkit.getWorld(key);
        if (world != null) setWorld(world);
        return world;
    }

    @Override
    public boolean isWorldLoaded() {
        return getWorld() != null;
    }

    @Override
    public Key key() {
        return key;
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof final Location other)) return false;
        final var world = other instanceof final LazyLocation lazy ? lazy.key()
                : other.getWorld() != null ? other.getWorld().key() : null;
        return Objects.equals(key, world)
                && other.getX() == getX()
                && other.getY() == getY()
                && other.getZ() == getZ()
                && other.getYaw() == getYaw()
                && other.getPitch() == getPitch();
    }

    @Override
    public String toString() {
        return "LazyLocation{"
                + "key=" + key
                + ",x=" + getX()
                + ",y=" + getY()
                + ",z=" + getZ()
                + ",pitch=" + getPitch()
                + ",yaw=" + getYaw()
                + '}';
    }
}
