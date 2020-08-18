package skywolf46.bungeeswitchlistener.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import org.fusesource.jansi.Ansi;
import skywolf46.bungeeswitchlistener.BungeeSwitchListener;
import skywolf46.bungeeswitchlistener.data.BungeePacketData;

import java.net.InetSocketAddress;

public class BungeePacketProcessor extends ChannelInboundHandlerAdapter {
    private int port;

    public BungeePacketProcessor(int port) {
        this.port = port;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BungeePacketData bpd = (BungeePacketData) msg;
        if (bpd.isUserData()) {
            if (bpd.isBroadcast()) {
                BungeeSwitchListener.broadcast(ctx.channel(), bpd);
            } else {
                ProxiedPlayer pp = BungeeCord.getInstance().getPlayer(bpd.getUID());
                if (pp != null && pp.getServer() != null) {
                    Server sv = pp.getServer();
                    Channel ct = BungeeSwitchListener.get(((InetSocketAddress) sv.getInfo().getSocketAddress()).getPort());
                    if (ct != null)
                        ct.writeAndFlush(bpd);
                    bpd.getBuffer().release();
                }
            }
        } else {
            // Just broadcast...
            BungeeSwitchListener.broadcast(ctx.channel(), bpd);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        BungeeCord.getInstance().getConsole().sendMessage(Ansi.ansi().fg(Ansi.Color.YELLOW).a("BungeeSwitchListener").fg(Ansi.Color.WHITE).a(" | ").fg(Ansi.Color.RED).a("Server connection disconnected : Port " + port).toString());
        BungeeSwitchListener.unregister(port);
    }
}
