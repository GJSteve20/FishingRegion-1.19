package ro.steve.antifishing.v1_20_6.listeners.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import ro.steve.antifishing.v1_20_6.listeners.events.utils.MovementWay;

public class RegionEnteredEvent extends RegionEvent {

    public RegionEnteredEvent(ProtectedRegion region, Player player, MovementWay movement, PlayerEvent parent) {
        super(region, player, movement, parent);
    }
}
