package ro.steve.antifishing.v1_21.listeners.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import ro.steve.antifishing.v1_21.listeners.events.utils.MovementWay;

public class RegionLeftEvent extends RegionEvent {

    public RegionLeftEvent(ProtectedRegion region, Player player, MovementWay movement, PlayerEvent parent) {
        super(region, player, movement, parent);
    }
}
