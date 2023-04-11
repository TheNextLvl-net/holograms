package net.thenextlvl.hologram.v1_19_R3.event;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.hologram.v1_19_R3.CraftHologramProvider;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class CraftHologramListener implements Listener {
    private final CraftHologramProvider provider;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        loadHolograms(event.getPlayer());
    }

    @EventHandler
    public void onWorldChanged(PlayerChangedWorldEvent event) {
        unloadHolograms(event.getPlayer(), event.getFrom());
        loadHolograms(event.getPlayer());
    }

    private void loadHolograms(Player player) {
        var loader = provider.getHologramLoader();
        provider.getHologramRegistry().getHolograms().stream()
                .filter(hologram -> loader.canSee(player, hologram))
                .forEach(hologram -> loader.load(hologram, player));
    }

    private void unloadHolograms(Player player, World world) {
        var loader = provider.getHologramLoader();
        loader.getHolograms(player, world).forEach(hologram ->
                loader.unload(hologram, player));
    }
}
