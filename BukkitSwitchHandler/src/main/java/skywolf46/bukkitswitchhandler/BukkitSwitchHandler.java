package skywolf46.bukkitswitchhandler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import skywolf46.bukkitswitchhandler.listener.EventListener;

public final class BukkitSwitchHandler extends JavaPlugin {

    private static BukkitSwitchHandler inst;

    public static BukkitSwitchHandler inst() {
        return inst;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        inst = this;
        EventListener el = new EventListener();
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "SWHandler|Request", el);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "SWHandler|Prepare", el);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "SWHandler|Prepared");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
