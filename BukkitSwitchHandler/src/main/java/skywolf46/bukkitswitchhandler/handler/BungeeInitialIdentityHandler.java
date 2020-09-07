package skywolf46.bukkitswitchhandler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.bukkit.Bukkit;

public class BungeeInitialIdentityHandler extends ChannelInboundHandlerAdapter {
    private Runnable initializer;

    public BungeeInitialIdentityHandler(Runnable initializer) {
        this.initializer = initializer;
    }

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
        ByteBuf buf = (ByteBuf) msg;
        buf.markReaderIndex();
        if (buf.readInt() == 108487 && buf.readInt() == 498130) {
            ctx.pipeline().addLast("bungee-encode-filter", new ByteSendingEncoder());
            ctx.pipeline().addLast("bungee-encoder", new PacketDataEncoder());
            ctx.pipeline().addLast("bungee-decode-filter", new ByteCollectingDecoder());
//            ctx.pipeline().addLast("bungee-decoder", new PacketDataDecoder());
//            ctx.pipeline().addAfter("bungee-decoder", "bungee-processor", new BungeePacketProcessor());
//            ctx.pipeline().addLast("bungee-decoder", new PacketDataDecoder());
            ctx.pipeline().addLast("bungee-exception-handler", new BungeeErrorHandler());
            ctx.pipeline().remove(this);
            initializer.run();
        }
    }

}
