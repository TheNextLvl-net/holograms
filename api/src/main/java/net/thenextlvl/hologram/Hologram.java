package net.thenextlvl.hologram;

import core.annotation.MethodsReturnNonnullByDefault;
import net.thenextlvl.hologram.line.HologramLine;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface Hologram {

    Location getLocation();

    void setLocation(Location location);

    Collection<HologramLine> getLines();

    void setLines(Collection<HologramLine> lines);

    void load(Player player);

    void unload(Player player);
}
