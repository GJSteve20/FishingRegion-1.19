package ro.steve.antifishing.v1_20.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import ro.steve.antifishing.v1_20.AntiFishing;
import ro.steve.antifishing.v1_20.listeners.events.RegionEnterEvent;

import java.util.Objects;

public class PlayerFish implements Listener {

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.REEL_IN) {
            Listeners.getWGRegionEventsListener.getPlayerRegions().get(event.getPlayer()).forEach(p -> {
                StateFlag.State flagState = p.getFlag(AntiFishing.ANTI_FISHING);
                if (flagState != null) {
                    if (p.getOwners().contains(event.getPlayer().getUniqueId()) || p.getMembers().contains(event.getPlayer().getUniqueId())) {
                        return;
                    }
                    if (flagState != StateFlag.State.DENY) {
                        return;
                    }
                    event.setCancelled(true);
                }
            });
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getType() != EntityType.FISHING_HOOK) {
            return;
        }

        if (!(event.getEntity().getShooter() instanceof Player shooter)) {
            return;
        }

        Location hook = event.getEntity().getLocation();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(hook.getWorld())));

        if (regions != null) {
            ApplicableRegionSet regionSet = regions.getApplicableRegions(BukkitAdapter.adapt(hook).toVector().toBlockPoint());

            if (regionSet.size() > 0) {
                regionSet.forEach(r -> {
                    StateFlag.State flagState = r.getFlag(AntiFishing.ANTI_FISHING);
                    if (flagState == null) {
                        return;
                    }
                    if (flagState.toString().equalsIgnoreCase("DENY")) {
                        if (r.getOwners().contains(shooter.getUniqueId()) || r.getMembers().contains(shooter.getUniqueId())) {
                            return;
                        }
                        event.getEntity().remove();
                    }
                });
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getType() != EntityType.FISHING_HOOK) {
            return;
        }
        if (!(event.getEntity().getShooter() instanceof Player player)) {
            return;
        }
        Listeners.fish_bobber.put(player.getUniqueId(), event.getEntity());
    }

    @EventHandler
    public void onRegionEnter(RegionEnterEvent event) {
        ProtectedRegion region = event.getRegion();
        if (region.getOwners().contains(event.getPlayer().getUniqueId()) || region.getMembers().contains(event.getPlayer().getUniqueId())) {
            return;
        }
        if (Listeners.fish_bobber.containsKey(event.getPlayer().getUniqueId())) {
            Listeners.fish_bobber.get(event.getPlayer().getUniqueId()).remove();
            Listeners.fish_bobber.remove(event.getPlayer().getUniqueId());
        }
    }
}
