package net.thenextlvl.hologram;

import core.bukkit.item.ItemBuilder;
import core.bukkit.plugin.CorePlugin;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.thenextlvl.hologram.api.Hologram;
import net.thenextlvl.hologram.api.HologramProvider;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class HologramPlugin extends CorePlugin {
    private @Nullable RegisteredServiceProvider<HologramProvider> registration;

    @Override
    public void onEnable() {
        Bukkit.getServicesManager().register(HologramProvider.class, getHologramProvider(), this, ServicePriority.Normal);
        this.registration = Bukkit.getServicesManager().getRegistration(HologramProvider.class);
        // TODO: 10.04.23 load holograms from file
    }

    @SuppressWarnings("deprecation")
    private Hologram test(HologramProvider provider) {
        var factory = provider.getHologramFactory();
        var location = new Location(null, 0, 0, 0);
        return factory.createHologram(location, List.of(
                factory.createTextLine(display -> {
                    display.text(Component.text("TEST"));
                    display.setLineWidth(10);
                    display.setAlignment(TextDisplay.TextAligment.RIGHT);
                    display.setShadowed(true);
                    display.setSeeThrough(true);
                    display.setTextOpacity((byte) 7);
                    display.setBackgroundColor(Color.AQUA);
                    display.setBillboard(Display.Billboard.FIXED);
                }),
                factory.createItemLine(display -> {
                    display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GROUND);
                    display.setItemStack(new ItemBuilder(Material.IRON_SWORD).modify(meta ->
                            meta.addEnchant(Enchantment.DURABILITY, 1, false)));
                    display.setBillboard(Display.Billboard.HORIZONTAL);
                }),
                factory.createBlockLine(display -> {
                    display.setBlock(Material.CRIMSON_STAIRS.createBlockData());
                    display.setBillboard(Display.Billboard.VERTICAL);
                    display.setBrightness(new Display.Brightness(15, 15));
                    display.setGlowColorOverride(Color.GREEN);
                    display.setGlowing(true);
                })
        ));
    }

    private HologramProvider getHologramProvider() {
        var version = Bukkit.getBukkitVersion();
        if (version.contains("1.19.4"))
            return new net.thenextlvl.hologram.v1_19_R3.CraftHologramProvider(this);
        throw new IllegalStateException("Your server version is not supported: " + version);
    }

    @Override
    public void onDisable() {
        Bukkit.getServicesManager().unregisterAll(this);
    }
}
