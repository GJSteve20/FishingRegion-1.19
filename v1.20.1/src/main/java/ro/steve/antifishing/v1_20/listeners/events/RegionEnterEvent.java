package ro.steve.antifishing.v1_20.listeners.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;
import ro.steve.antifishing.v1_20.listeners.events.utils.MovementWay;

public class RegionEnterEvent extends RegionEvent implements Cancellable {

    private boolean cancelled;

    private boolean cancellable;

    public RegionEnterEvent(ProtectedRegion region, Player player, MovementWay movement, PlayerEvent parent) {
        super(region, player, movement, parent);
        this.cancelled = false;
        this.cancellable = movement != MovementWay.SPAWN &&
                movement != MovementWay.DISCONNECT;
    }

    public void setCancelled(boolean cancelled) {
        if (!this.cancellable)
            return;
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public boolean isCancellable() {
        return this.cancellable;
    }

    protected void setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
        if (!this.cancellable)
            this.cancelled = false;
    }
}
