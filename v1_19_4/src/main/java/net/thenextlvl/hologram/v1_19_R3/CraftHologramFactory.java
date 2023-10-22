package net.thenextlvl.hologram.v1_19_R3;

import net.kyori.adventure.text.Component;
import net.thenextlvl.hologram.api.HologramFactory;
import net.thenextlvl.hologram.v1_19_R3.hologram.CraftBlockHologram;
import net.thenextlvl.hologram.v1_19_R3.hologram.CraftItemHologram;
import net.thenextlvl.hologram.v1_19_R3.hologram.CraftTextHologram;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

public class CraftHologramFactory implements HologramFactory {
    @Override
    public CraftTextHologram createTextHologram(Location location, Component text) {
        return new CraftTextHologram(location, text);
    }

    @Override
    public CraftBlockHologram createBlockHologram(Location location, BlockData block) {
        return new CraftBlockHologram(location, block);
    }

    @Override
    public CraftItemHologram createItemHologram(Location location, ItemStack itemStack) {
        return new CraftItemHologram(location, itemStack);
    }
}
