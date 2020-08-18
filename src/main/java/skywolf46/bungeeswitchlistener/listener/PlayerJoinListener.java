package skywolf46.bungeeswitchlistener.listener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import skywolf46.bungeeswitchlistener.BungeeSwitchListener;
import skywolf46.bungeeswitchlistener.data.BungeePacketData;
import skywolf46.bungeeswitchlistener.data.UserPacketData;

import java.net.InetSocketAddress;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void ev(ServerConnectEvent e) {
        System.out.println("What?");
        switch (e.getReason()) {
            case JOIN_PROXY:
            case LOBBY_FALLBACK:
//                e.getRequest().getTarget().sendData("MC|InitialLoad", e.getPlayer().getUniqueId().toString().getBytes());
            {

                System.out.println("Initial");
                int port = ((InetSocketAddress) e.getRequest().getTarget().getSocketAddress()).getPort();
                System.out.println(port);
                Channel ctx = BungeeSwitchListener.get(port);
                System.out.println(ctx);
                if (ctx != null) {
                    System.out.println("write and flush");
                    ctx.writeAndFlush(new BungeePacketData("Nothing", false));

                    ctx.writeAndFlush(new UserPacketData("Initial", false, e.getPlayer().getUniqueId(), 1));
                }
            }
            break;
            default:
                System.out.println("Hello World");
                if (e.getTarget() != null) {
                    int port = ((InetSocketAddress) e.getRequest().getTarget().getSocketAddress()).getPort();
                    System.out.println(port);
                    Channel ctx = BungeeSwitchListener.get(port);
                    if (ctx != null) {
                        ctx.writeAndFlush(new UserPacketData("ClearData", false, e.getPlayer().getUniqueId(), 1));
                    }
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
    }

    @EventHandler
    public void ev(ServerConnectedEvent e) {

    }
}
