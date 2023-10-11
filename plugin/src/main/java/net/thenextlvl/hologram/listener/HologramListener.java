package net.thenextlvl.hologram.listener;

import core.annotation.ParametersAreNotNullByDefault;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.hologram.api.HologramProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
@ParametersAreNotNullByDefault
public class HologramListener implements Listener {
    private final HologramProvider provider;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        loadHolograms(event.getPlayer());
    }

    @EventHandler
    public void onWorldChanged(PlayerChangedWorldEvent event) {
        unloadHolograms(event.getPlayer());
        loadHolograms(event.getPlayer());
    }

    private void loadHolograms(Player player) {
        var loader = provider.getHologramLoader();
        provider.getHologramRegistry().getHolograms().stream()
                .filter(hologram -> loader.canSee(player, hologram))
                .forEach(hologram -> loader.load(hologram, player));
    }

    private void unloadHolograms(Player player) {
        var loader = provider.getHologramLoader();
        loader.getHolograms(player).forEach(hologram ->
                loader.unload(hologram, player));
    }
}
