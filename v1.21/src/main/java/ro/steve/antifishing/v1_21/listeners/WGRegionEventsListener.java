package ro.steve.antifishing.v1_21.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import ro.steve.antifishing.v1_21.listeners.events.RegionEnterEvent;
import ro.steve.antifishing.v1_21.listeners.events.RegionEnteredEvent;
import ro.steve.antifishing.v1_21.listeners.events.RegionLeaveEvent;
import ro.steve.antifishing.v1_21.listeners.events.RegionLeftEvent;
import ro.steve.antifishing.v1_21.listeners.events.utils.MovementWay;

import java.util.*;

public class WGRegionEventsListener implements Listener {

    private final WorldGuard wgPlugin;

    private final Plugin plugin;

    private final Map<Player, Set<ProtectedRegion>> playerRegions;

    public WGRegionEventsListener(Plugin plugin, WorldGuard wgPlugin) {
        this.plugin = plugin;
        this.wgPlugin = wgPlugin;
        this.playerRegions = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Map<Player, Set<ProtectedRegion>> getPlayerRegions() {
        return playerRegions;
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        Set<ProtectedRegion> regions = this.playerRegions.remove(e.getPlayer());
        if (regions != null)
            for (ProtectedRegion region : regions) {
                RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                RegionLeftEvent leftEvent = new RegionLeftEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                this.plugin.getServer().getPluginManager().callEvent(leaveEvent);
                this.plugin.getServer().getPluginManager().callEvent(leftEvent);
            }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Set<ProtectedRegion> regions = this.playerRegions.remove(e.getPlayer());
        if (regions != null)
            for (ProtectedRegion region : regions) {
                RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                RegionLeftEvent leftEvent = new RegionLeftEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                this.plugin.getServer().getPluginManager().callEvent(leaveEvent);
                this.plugin.getServer().getPluginManager().callEvent(leftEvent);
            }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        e.setCancelled(updateRegions(e.getPlayer(), MovementWay.MOVE, e.getTo(), e));
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        e.setCancelled(updateRegions(e.getPlayer(), MovementWay.TELEPORT, e.getTo(), e));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        updateRegions(e.getPlayer(), MovementWay.SPAWN, e.getPlayer().getLocation(), e);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        updateRegions(e.getPlayer(), MovementWay.SPAWN, e.getRespawnLocation(), e);
    }

    private synchronized boolean updateRegions(final Player player, final MovementWay movement, Location to, final PlayerEvent event) {
        Set<ProtectedRegion> regions;
        if (this.playerRegions.get(player) == null) {
            regions = new HashSet<>();
        } else {
            regions = new HashSet<>(this.playerRegions.get(player));
        }
        Set<ProtectedRegion> oldRegions = new HashSet<>(regions);
        RegionContainer container = wgPlugin.getPlatform().getRegionContainer();
        RegionManager rm = container.get(BukkitAdapter.adapt(Objects.requireNonNull(to.getWorld())));
        if (rm == null)
            return false;
        HashSet<ProtectedRegion> appRegions = new HashSet<>(
                rm.getApplicableRegions(BukkitAdapter.adapt(to).toVector().toBlockPoint()).getRegions());
        ProtectedRegion globalRegion = rm.getRegion("__global__");
        if (globalRegion != null)
            appRegions.add(globalRegion);
        for (ProtectedRegion region : appRegions) {
            if (!regions.contains(region)) {
                RegionEnterEvent e = new RegionEnterEvent(region, player, movement, event);
                this.plugin.getServer().getPluginManager().callEvent(e);
                if (e.isCancelled()) {
                    regions.clear();
                    regions.addAll(oldRegions);
                    return true;
                }
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    RegionEnteredEvent e1 = new RegionEnteredEvent(region, player, movement, event);
                    WGRegionEventsListener.this.plugin.getServer().getPluginManager().callEvent(e1);
                }, 1L);
                regions.add(region);
            }
        }
        Iterator<ProtectedRegion> itr = regions.iterator();
        while (itr.hasNext()) {
            final ProtectedRegion region = itr.next();
            if (!appRegions.contains(region)) {
                if (rm.getRegion(region.getId()) != region) {
                    itr.remove();
                    continue;
                }
                RegionLeaveEvent e = new RegionLeaveEvent(region, player, movement, event);
                this.plugin.getServer().getPluginManager().callEvent(e);
                if (e.isCancelled()) {
                    regions.clear();
                    regions.addAll(oldRegions);
                    return true;
                }
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    RegionLeftEvent e1 = new RegionLeftEvent(region, player, movement, event);
                    plugin.getServer().getPluginManager().callEvent(e1);
                }, 1L);
                itr.remove();
            }
        }
        this.playerRegions.put(player, regions);
        return false;
    }
}