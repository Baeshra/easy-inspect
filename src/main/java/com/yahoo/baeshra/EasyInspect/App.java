package com.yahoo.baeshra.EasyInspect;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

public class App extends JavaPlugin {

    public static App plugin;
    CoreProtectAPI coreProtectApi = null;

    @Override
    public void onEnable() {
        App.plugin = this;
        coreProtectApi = getCoreProtect();
        if(coreProtectApi == null) {
            getLogger().warning("Easy Inspect requires Core Protect. Please install core protect and restart.");
        } else {
            this.getCommand("ei").setExecutor(new InspectCommand());
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("See you again, SpigotMC!");
    }

    private CoreProtectAPI getCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");
     
        // Check that CoreProtect is loaded
        if (plugin == null || !(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (CoreProtect.isEnabled() == false) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 6) {
            return null;
        }

        return CoreProtect;
    }
}