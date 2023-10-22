package net.thenextlvl.hologram.v1_19_R3.display;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.thenextlvl.hologram.api.display.EntityBlockDisplay;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
public class CraftEntityBlockDisplay extends CraftEntityDisplay implements EntityBlockDisplay {
    private @NotNull BlockData block;
}
