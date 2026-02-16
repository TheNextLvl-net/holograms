package net.thenextlvl.hologram.listeners;

import io.papermc.paper.event.entity.EntityKnockbackEvent;
import io.papermc.paper.event.player.PlayerPickEntityEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.thenextlvl.hologram.Hologram;
import net.thenextlvl.hologram.HologramPlugin;
import net.thenextlvl.hologram.action.ClickAction;
import net.thenextlvl.hologram.action.ClickType;
import net.thenextlvl.hologram.event.PlayerHologramInteractEvent;
import net.thenextlvl.hologram.line.PagedHologramLine;
import net.thenextlvl.hologram.models.line.PaperEntityHologramLine;
import net.thenextlvl.hologram.models.line.PaperHologramLine;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiConsumer;

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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDeath(final EntityDeathEvent event) {
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityRemove(final EntityRemoveEvent event) {
        plugin.hologramProvider().getHologramLine(event.getEntity())
                .map(hologram -> (PaperHologramLine) hologram)
                .ifPresent(hologram -> hologram.invalidate(event.getEntity()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        plugin.hologramProvider().getHolograms(event.getPlayer())
                .flatMap(Hologram::getLines)
                .filter(PaperEntityHologramLine.class::isInstance)
                .map(PaperEntityHologramLine.class::cast)
                .forEach(line -> {
                    line.applyBillboard(event.getPlayer());
                });
    }

    private void handleInteraction(final Player player, final Entity entity, final Cancellable cancellable, final boolean isRight) {
        plugin.hologramProvider().getHologramLine(entity).ifPresent(line -> {
            cancellable.setCancelled(true);

            if (!entity.getType().equals(EntityType.INTERACTION)) return;

            final var type = player.isSneaking()
                    ? (isRight ? ClickType.SHIFT_RIGHT : ClickType.SHIFT_LEFT)
                    : (isRight ? ClickType.RIGHT : ClickType.LEFT);

            if (!new PlayerHologramInteractEvent(line, player, type).callEvent()) return;

            final var consumer = (BiConsumer<String, ClickAction<?>>) (name, action) -> {
                if (action.isSupportedClickType(type)) action.invoke(line, player);
            };
            if (line instanceof final PagedHologramLine paged)
                paged.forEachPage(page -> page.forEachAction(consumer));
            line.forEachAction(consumer);
        });
    }
}
