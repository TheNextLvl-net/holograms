package net.thenextlvl.hologram;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.hologram.line.HologramLine;
import org.bukkit.Location;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class CraftHologram implements Hologram {
    private Location location;
    private Collection<HologramLine> lines;
}
