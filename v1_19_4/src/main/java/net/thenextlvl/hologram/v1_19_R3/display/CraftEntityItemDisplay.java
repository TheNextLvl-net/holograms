package net.thenextlvl.hologram.v1_19_R3.display;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.thenextlvl.hologram.api.display.EntityItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
public class CraftEntityItemDisplay extends CraftEntityDisplay implements EntityItemDisplay {
    private @NotNull ItemStack itemStack;
    private ItemDisplayTransform itemDisplayTransform;
}
