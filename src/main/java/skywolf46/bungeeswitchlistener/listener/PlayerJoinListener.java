package skywolf46.bungeeswitchlistener.listener;

import io.netty.channel.Channel;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import skywolf46.bungeeswitchlistener.BungeeSwitchListener;
import skywolf46.bungeeswitchlistener.data.BungeePacketData;
import skywolf46.bungeeswitchlistener.data.UserPacketData;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class PlayerJoinListener implements Listener {
    private HashMap<ProxiedPlayer, Boolean> dat = new HashMap<>();

    @EventHandler
    public void ev(ServerConnectEvent e) {
        switch (e.getReason()) {
            case JOIN_PROXY:
            case LOBBY_FALLBACK:
//                e.getRequest().getTarget().sendData("MC|InitialLoad", e.getPlayer().getUniqueId().toString().getBytes());
            {
                dat.put(e.getPlayer(), true);
            }
            break;

//                    if (e.getPlayer().getServer() == null)
//                        return;
//                    port = ((InetSocketAddress) e.getPlayer().getServer().getInfo().getSocketAddress()).getPort();
//                    ctx = BungeeSwitchListener.get(port);
//                    if (ctx != null) {
//                        ctx.writeAndFlush(new UserPacketData("Initial", false, e.getPlayer().getUniqueId(), 1));
//                    }
        }
//                System.out.println("...Connect to port " + port);
    }
//        System.out.println("Connect " + e.getReason().name());

    @EventHandler
    public void ev(ServerConnectedEvent e) {
        if (e.getServer() != null) {
            if (dat.containsKey(e.getPlayer())) {
                dat.remove(e.getPlayer());
                int port = ((InetSocketAddress) e.getServer().getSocketAddress()).getPort();
                Channel ctx = BungeeSwitchListener.get(port);
                if (ctx != null) {
//                    ctx.writeAndFlush(new BungeePacketData("Nothing", false));
                    ctx.writeAndFlush(new UserPacketData("Initial", false, e.getPlayer().getUniqueId(), 1));
                }
                return;
            }
            int port = ((InetSocketAddress) e.getServer().getSocketAddress()).getPort();
            Channel ctx = BungeeSwitchListener.get(port);
            if (ctx != null) {
                ctx.writeAndFlush(new UserPacketData("ClearData", false, e.getPlayer().getUniqueId(), 1));
            }
        }
    }
}
