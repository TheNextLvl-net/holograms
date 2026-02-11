package net.thenextlvl.hologram.listeners;

import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.economy.ServiceEconomyProvider;
import net.thenextlvl.hologram.economy.VaultEconomyProvider;
import net.thenextlvl.hologram.locale.MiniPlaceholdersFormatter;
import net.thenextlvl.hologram.locale.PlaceholderAPIFormatter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PluginListener implements Listener {
    private final HologramPlugin plugin;

    public PluginListener(final HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(final PluginEnableEvent event) {
        if (isPlugin(event.getPlugin(), "PlaceholderAPI")) {
            plugin.papiFormatter = new PlaceholderAPIFormatter();
            plugin.getComponentLogger().info("PlaceholderAPI detected, using for placeholders");
        }
        if (isPlugin(event.getPlugin(), "MiniPlaceholders")) {
            plugin.miniFormatter = new MiniPlaceholdersFormatter();
            plugin.getComponentLogger().info("MiniPlaceholders detected, using for placeholders");
        }
        
        if (isPlugin(event.getPlugin(), "ServiceIO")) {
            plugin.economyProvider = new ServiceEconomyProvider(event.getPlugin());
        } else if (isPlugin(event.getPlugin(), "Vault")) {
            plugin.economyProvider = new VaultEconomyProvider(event.getPlugin());
        }
    }
    
    private boolean isPlugin(final Plugin plugin, final String name) {
        return plugin.getName().equals(name) || plugin.getPluginMeta().getProvidedPlugins().contains(name);
    }
}
