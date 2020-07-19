package skywolf46.bungeeswitchlistener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import skywolf46.bungeeswitchlistener.listener.ServerSwitchListener;

public final class BungeeSwitchListener extends Plugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        ProxyServer.getInstance().registerChannel("SWHandler|Prepared");

        ProxyServer.getInstance().getPluginManager().registerListener(this,new ServerSwitchListener());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
