package ro.steve.antifishing;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ro.steve.antifishing.v1_21.AntiFishing;
import ro.steve.antifishing.v1_21.listeners.Listeners;

public class AntiFishingMain extends JavaPlugin {

    public void onLoad() {
        switchVersion();
    }

    public void onEnable() {
        switchVersionListener();
    }

    private void switchVersion() {
        String version = Bukkit.getBukkitVersion().split("-")[0];
        switch (version) {
            case "1.20": case "1.20.1": case "1.20.2": case "1.20.3": case "1.20.4": {
                new ro.steve.antifishing.v1_20.AntiFishing();
                return;
            }
            case "1.20.5": case "1.20.6": {
                new ro.steve.antifishing.v1_20_6.AntiFishing();
                return;
            }
            case "1.21": {
                new AntiFishing();
                return;
            }
            default: {
                this.getLogger().severe("This version isn't supported, please contact the dev");
                this.getServer().getPluginManager().disablePlugin(this);
            }
        }
    }

    private void switchVersionListener() {
        String version = Bukkit.getBukkitVersion().split("-")[0];
        switch (version) {
            case "1.20": case "1.20.1": case "1.20.2": case "1.20.3": case "1.20.4": {
                new ro.steve.antifishing.v1_20.listeners.Listeners(this);
                return;
            }
            case "1.20.5": case "1.20.6": {
                new ro.steve.antifishing.v1_20_6.listeners.Listeners(this);
                return;
            }
            case "1.21": {
                new Listeners(this);
                return;
            }
            default: {
                this.getLogger().severe("This version isn't supported, please contact the dev");
                this.getServer().getPluginManager().disablePlugin(this);
            }
        }
    }
}
