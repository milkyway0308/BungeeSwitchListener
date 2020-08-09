package skywolf46.bungeeswitchlistener.listener;

import net.md_5.bungee.api.event.ClientConnectEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void ev(ServerConnectEvent e) {
        switch (e.getReason()) {
            case JOIN_PROXY:
            case LOBBY_FALLBACK:
                e.getRequest().getTarget().sendData("MC|InitialLoad", e.getPlayer().getUniqueId().toString().getBytes());
        }
//        System.out.println("Connect " + e.getReason().name());
    }

    @EventHandler
    public void ev(ServerConnectedEvent e) {
        System.out.println("Connect2 " + e.getServer());
    }
}
