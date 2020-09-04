package skywolf46.bungeeswitchlistener.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import org.fusesource.jansi.Ansi;
import skywolf46.bungeeswitchlistener.BungeeSwitchListener;
import skywolf46.bungeeswitchlistener.data.BungeePacketData;
import skywolf46.bungeeswitchlistener.data.BungeeServerDirectPacketData;
import skywolf46.bungeeswitchlistener.data.BungeeTargetPacketData;

import java.net.InetSocketAddress;

public class BungeePacketProcessor extends ChannelInboundHandlerAdapter {
    private int port;

    public BungeePacketProcessor(int port) {
        this.port = port;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BungeePacketData bpd = (BungeePacketData) msg;
        switch (bpd.getPacketID()) {
            case 0:
            case 1: {
                if (bpd.isUserData()) {
                    if (bpd.isBroadcast()) {
                        BungeeSwitchListener.broadcast(ctx.channel(), bpd);
                    } else {
                        ProxiedPlayer pp = BungeeCord.getInstance().getPlayer(bpd.getUID());
                        if (pp != null && pp.getServer() != null) {
                            Server sv = pp.getServer();
                            Channel ct = BungeeSwitchListener.get(((InetSocketAddress) sv.getInfo().getSocketAddress()).getPort());
                            if (ct != null) {
                                ct.writeAndFlush(bpd);
                            }
                        }
                    }
                } else {
                    // Just broadcast...
                    BungeeSwitchListener.broadcast(ctx.channel(), bpd);
                }
            }
            break;
            case 2: {
                BungeeSwitchListener.listen((BungeeTargetPacketData) bpd);
                bpd.getBuffer().release();
                break;
            }
            case 3: {
                BungeeServerDirectPacketData direct = (BungeeServerDirectPacketData) bpd;
                Channel sChan;
                if (direct.isPortMode()) {
                    sChan = BungeeSwitchListener.get(direct.getPort());
                } else {
                    ServerInfo chan = ProxyServer.getInstance().getServerInfo(direct.getTargetServer());
                    if (chan == null) {
                        ProxyServer.getInstance().getConsole().sendMessage("§6BungeeSwitchListener §7| §cPacket to server " + direct.getTargetServer() + " dropped; Server not connected");
                        return;
                    }
                    sChan = BungeeSwitchListener.get(((InetSocketAddress) chan.getSocketAddress()).getPort());
                }
                if (sChan == null) {
                    ProxyServer.getInstance().getConsole().sendMessage("§6BungeeSwitchListener §7| §cPacket to server " + (direct.isPortMode() ? "Port " + direct.getPort() : direct.getTargetServer()) + " dropped; Server not using BukkitSwitchHandler");
                    return;
                }
//                direct.getBuffer().retain();
                sChan.writeAndFlush(direct);
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        BungeeCord.getInstance().getConsole().sendMessage(Ansi.ansi().fg(Ansi.Color.YELLOW).a("BungeeSwitchListener").fg(Ansi.Color.WHITE).a(" | ").fg(Ansi.Color.RED).a("Server connection disconnected : Port " + port).toString());
        BungeeSwitchListener.unregister(port);
    }
}
