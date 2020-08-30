package skywolf46.bukkitswitchhandler.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;
import skywolf46.bukkitswitchhandler.handler.BungeeInitialIdentityHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class BungeeClientSocket {
    private AtomicBoolean active = new AtomicBoolean(false);
    private AtomicBoolean failed = new AtomicBoolean(false);
    private Channel channel;
    private Bootstrap strap;
    private int port;

    public BungeeClientSocket(int port, Consumer<BungeeClientSocket> socket) {
        this.port = port;
        EventLoopGroup group = new NioEventLoopGroup();
        strap = new Bootstrap();
        strap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new BungeeInitialIdentityHandler(() -> {
                    socket.accept(BungeeClientSocket.this);
                }));
    }

    public void reconnect() {
        try {
            if (channel != null) {
                try {
                    channel.close();
                } catch (Exception ignored) {
                }
                channel = null;
            }
            channel = strap.connect(new InetSocketAddress("localhost", port)).sync().channel();
            failed.set(false);
        } catch (Exception ex) {
            failed.set(true);
        }
    }


    public void add(Consumer<BungeeClientSocket> soc) {
        BukkitSwitchHandler.getPacketThread().append(con -> {
            soc.accept(BungeeClientSocket.this);
        });
    }

    public Channel getSocketChannel() {
        return channel;
    }


    public boolean failed() {
        return failed.get();
    }

    public boolean isConnected() {
        return this.channel != null;
    }
}
