package net.thenextlvl.hologram.listeners;

import io.papermc.paper.event.entity.EntityKnockbackEvent;
import io.papermc.paper.event.player.PlayerPickEntityEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.models.PaperHologram;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class EntityListener implements Listener {
    private final HologramPlugin plugin;

    public EntityListener(final HologramPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityRemove(final EntityRemoveEvent event) {
        plugin.hologramProvider().getHologram(event.getEntity()).ifPresent(hologram -> {
            hologram.persist();
            ((PaperHologram) hologram).invalidate(event.getEntity());
            hologram.despawn();
        });
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityCombust(final EntityCombustEvent event) {
        event.setCancelled(plugin.hologramProvider().isHologramPart(event.getEntity()));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        event.setCancelled(plugin.hologramProvider().isHologramPart(event.getEntity()));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPrePlayerEntityAttack(final PrePlayerAttackEntityEvent event) {
        event.setCancelled(plugin.hologramProvider().isHologramPart(event.getAttacked()));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerPickEntity(final PlayerPickEntityEvent event) {
        event.setCancelled(plugin.hologramProvider().isHologramPart(event.getEntity()));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerArmorStandManipulate(final PlayerArmorStandManipulateEvent event) {
        event.setCancelled(plugin.hologramProvider().isHologramPart(event.getRightClicked()));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        event.setCancelled(plugin.hologramProvider().isHologramPart(event.getRightClicked()));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreeperPower(final CreeperPowerEvent event) {
        event.setCancelled(plugin.hologramProvider().isHologramPart(event.getEntity()));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onProjectileHit(final ProjectileHitEvent event) {
        event.setCancelled(event.getHitEntity() != null && plugin.hologramProvider().isHologramPart(event.getHitEntity()));
    }

    // waiting for https://github.com/PaperMC/Paper/pull/13252
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityKnockback(final EntityKnockbackEvent event) {
        event.setCancelled(plugin.hologramProvider().isHologramPart(event.getEntity()));
    }
}
