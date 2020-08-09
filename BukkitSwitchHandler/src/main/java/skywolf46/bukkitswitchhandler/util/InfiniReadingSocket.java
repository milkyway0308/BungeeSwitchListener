package skywolf46.bukkitswitchhandler.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class InfiniReadingSocket extends Thread {
    private AtomicBoolean enabled = new AtomicBoolean(false);
    private AtomicBoolean active = new AtomicBoolean(false);
    private List<Consumer<InfiniReadingSocket>> obje = new ArrayList<>();
    private final Object LOCK = new Object();
    private Channel channel;

    public InfiniReadingSocket() {
        System.out.println("Create.");
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
                            ctx.writeAndFlush(bb);
                            System.out.println("Active");
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf buf = (ByteBuf) msg;
                            if (!active.get()) {
                                if (buf.readInt() == 108487 && buf.readInt() == 498130) {
                                    synchronized (LOCK) {
                                        active.set(true);
                                        for (Consumer<InfiniReadingSocket> infi : obje)
                                            infi.accept(InfiniReadingSocket.this);
                                        System.out.println(obje.size());
                                        obje.clear();
                                    }
                                    System.out.println("Connected");
                                    BukkitSwitchHandler.initialize(InfiniReadingSocket.this);
                                }
                            } else {
//                                String name = readString(buf);
//                                byte[] data = new byte[buf.readInt()];
//                                buf.readBytes(data);
//                                ByteArrayInputStream bios = new ByteArrayInputStream(data);
//                                DataInputStream di = new DataInputStream(bios);
//                                BukkitSwitchHandler.load(name, di);
//                                di.close();
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
            ex.printStackTrace();
        }
    }

    private String readString(ByteBuf buf) {
        int len = buf.readShort();
        byte[] data = new byte[len];
        buf.readBytes(data);
        return new String(data);
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

    @Override
    public void run() {
    }


}
