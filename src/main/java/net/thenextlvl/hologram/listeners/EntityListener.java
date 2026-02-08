package net.thenextlvl.hologram.listeners;

import io.papermc.paper.event.entity.EntityKnockbackEvent;
import io.papermc.paper.event.player.PlayerPickEntityEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ClickType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityCombust(final EntityCombustEvent event) {
        event.setCancelled(plugin.hologramProvider().isHologramPart(event.getEntity()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event.getDamageSource().getCausingEntity() instanceof final Player player)
            handleInteraction(player, event.getEntity(), event, false);
        else if (event.getDamageSource().getDirectEntity() instanceof final Player player)
            handleInteraction(player, event.getEntity(), event, false);
        else event.setCancelled(plugin.hologramProvider().isHologramPart(event.getEntity()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof final Player player)
            handleInteraction(player, event.getEntity(), event, false);
        else event.setCancelled(plugin.hologramProvider().isHologramPart(event.getEntity()));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPrePlayerEntityAttack(final PrePlayerAttackEntityEvent event) {
        handleInteraction(event.getPlayer(), event.getAttacked(), event, false);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerPickEntity(final PlayerPickEntityEvent event) {
        event.setCancelled(plugin.hologramProvider().isHologramPart(event.getEntity()));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerArmorStandManipulate(final PlayerArmorStandManipulateEvent event) {
        event.setCancelled(plugin.hologramProvider().isHologramPart(event.getRightClicked()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        handleInteraction(event.getPlayer(), event.getRightClicked(), event, true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCreeperPower(final CreeperPowerEvent event) {
        event.setCancelled(plugin.hologramProvider().isHologramPart(event.getEntity()));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onProjectileHit(final ProjectileHitEvent event) {
        event.setCancelled(event.getHitEntity() != null && plugin.hologramProvider().isHologramPart(event.getHitEntity()));
    }

    // waiting for https://github.com/PaperMC/Paper/pull/13252
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityKnockback(final EntityKnockbackEvent event) {
        event.setCancelled(plugin.hologramProvider().isHologramPart(event.getEntity()));
    }

    private void handleInteraction(final Player player, final Entity entity, final Cancellable cancellable, final boolean isRight) {
        plugin.hologramProvider().getHologramLine(entity).ifPresent(hologramLine -> {
            // todo: add custom hologram interact event
            final var type = player.isSneaking()
                    ? (isRight ? ClickType.SHIFT_RIGHT : ClickType.SHIFT_LEFT)
                    : (isRight ? ClickType.RIGHT : ClickType.LEFT);
            hologramLine.getActions().values().stream()
                    .filter(action -> action.isSupportedClickType(type))
                    .forEach(action -> action.invoke(hologramLine, player));
            cancellable.setCancelled(true);
        });
    }
}
