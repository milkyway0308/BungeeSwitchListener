package skywolf46.bungeeswitchlistener.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import skywolf46.bungeeswitchlistener.BungeeSwitchListener;
import skywolf46.bungeeswitchlistener.data.BungeePacketData;

import java.net.InetSocketAddress;

public class BungeePacketProcessor extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BungeePacketData bpd = (BungeePacketData) msg;
        if (bpd.isUserData()) {
            if (bpd.isBroadcast()) {
                BungeeSwitchListener.broadcast(ctx, bpd);
            } else {
                ProxiedPlayer pp = BungeeCord.getInstance().getPlayer(bpd.getUID());
                if (pp != null && pp.getServer() != null) {
                    Server sv = pp.getServer();
                    ChannelHandlerContext ct = BungeeSwitchListener.get(((InetSocketAddress) sv.getInfo().getSocketAddress()).getPort());
                    if (ct != null)
                        ct.writeAndFlush(bpd);
                }
            }
        } else {
            // Just broadcast...
            BungeeSwitchListener.broadcast(ctx, bpd);
        }
    }
}
