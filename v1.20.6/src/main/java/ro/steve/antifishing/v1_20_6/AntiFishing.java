package ro.steve.antifishing.v1_20_6;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.Bukkit;

public class AntiFishing {

    public static StateFlag ANTI_FISHING;

    public AntiFishing() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("fishing", true);
            registry.register(flag);
            ANTI_FISHING = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("fishing");
            if (existing instanceof StateFlag) {
                ANTI_FISHING = (StateFlag) existing;
            } else {
                Bukkit.getLogger().info("Cannot register anti-fishing flag");
            }
        }
    }
}
