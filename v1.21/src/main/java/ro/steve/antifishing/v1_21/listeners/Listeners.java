package ro.steve.antifishing.v1_21.listeners;

import com.sk89q.worldguard.WorldGuard;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Listeners {

    public static WGRegionEventsListener getWGRegionEventsListener;
    public static Map<UUID, Entity> fish_bobber;

    public Listeners(Plugin plugin) {
        PluginManager m = plugin.getServer().getPluginManager();
        getWGRegionEventsListener = new WGRegionEventsListener(plugin, WorldGuard.getInstance());
        fish_bobber = new HashMap<>();
        m.registerEvents(new PlayerFish(), plugin);
    }
}
