package skywolf46.bukkitswitchhandler.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.bukkit.Bukkit;
import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;
import skywolf46.bukkitswitchhandler.handler.BungeePacketProcessor;
import skywolf46.bukkitswitchhandler.handler.PacketDataDecoder;
import skywolf46.bukkitswitchhandler.handler.PacketDataEncoder;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class InfiniReadingSocket {
    private AtomicBoolean active = new AtomicBoolean(false);
    private AtomicBoolean failed = new AtomicBoolean(false);
    private List<Consumer<InfiniReadingSocket>> obje = new ArrayList<>();
    private final Object LOCK = new Object();
    private Channel channel;

    public InfiniReadingSocket() {
        try {
            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            ByteBuf bb = Unpooled.buffer();
                            bb.writeInt(98012);
                            bb.writeInt(70031);
                            bb.writeInt(Bukkit.getPort());
                            ctx.writeAndFlush(bb);
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            if (!active.get()) {
                                ByteBuf buf = (ByteBuf) msg;
                                buf.markReaderIndex();
                                if (buf.readInt() == 108487 && buf.readInt() == 498130) {
                                    synchronized (LOCK) {
                                        active.set(true);
                                        for (Consumer<InfiniReadingSocket> infi : obje)
                                            infi.accept(InfiniReadingSocket.this);
                                        obje.clear();
                                    }
                                    ctx.pipeline().addFirst("bungee-decoder", new PacketDataDecoder());
                                    ctx.pipeline().addFirst("bungee-encoder", new PacketDataEncoder());
                                    ctx.pipeline().addAfter("bungee-decoder", "bungee-processor", new BungeePacketProcessor());
                                    ctx.pipeline().remove(this);
                                    BukkitSwitchHandler.initialize(InfiniReadingSocket.this);
                                }
                            } else {
                                // Listen broadcast
                            }
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            ctx.close();
                            BukkitSwitchHandler.retry();
                        }
                    });
            channel = b.connect(new InetSocketAddress("localhost", 25577)).sync().channel();


        } catch (Exception ex) {
            failed.set(true);
        }
    }


    public void add(Consumer<InfiniReadingSocket> soc) {
        synchronized (LOCK) {
            if (!active.get())
                obje.add(soc);
            else
                soc.accept(this);
        }
    }

    public Channel getSocketChannel() {
        return channel;
    }


    public boolean failed() {
        return failed.get();
    }
}
