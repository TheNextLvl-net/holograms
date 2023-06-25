package net.thenextlvl.hologram.v1_20_R1;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.hologram.api.Hologram;
import net.thenextlvl.hologram.api.line.HologramLine;
import net.thenextlvl.hologram.v1_20_R1.line.CraftHologramLine;
import org.bukkit.Location;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class CraftHologram implements Hologram {
    private Location location;
    private Collection<CraftHologramLine> lines;

    @Override
    @SuppressWarnings("unchecked")
    public void setLines(Collection<? extends HologramLine> lines) {
        this.lines = (Collection<CraftHologramLine>) lines;
    }

    @Override
    public CraftHologram clone() {
        try {
            CraftHologram clone = (CraftHologram) super.clone();
            clone.location = location.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
