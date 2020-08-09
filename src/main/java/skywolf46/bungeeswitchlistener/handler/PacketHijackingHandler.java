package skywolf46.bungeeswitchlistener.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.LegacyDecoder;
import net.md_5.bungee.protocol.Varint21FrameDecoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.UUID;

public class PacketHijackingHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf bb = (ByteBuf) msg;
        bb.markReaderIndex();
//        System.out.println(bb.readableBytes());
        if (bb.readableBytes() == 8) {
            int x1 = bb.readInt();
            int x2 = bb.readInt();
            if (x1 == 98012 && x2 == 70031) {

                try {
                    while (ctx.pipeline().removeFirst() != null) {
                    }
                } catch (Exception ex) {

                }
                ctx.pipeline().addFirst("fake-connection-listener", new ByteReadHandler());
                ByteBuf bf = Unpooled.buffer();
                bf.writeInt(108487);
                bf.writeInt(498130);
                ctx.channel().writeAndFlush(bf);
            }
        } else {
            ctx.pipeline().remove(this);
            super.channelRead(ctx, msg);
        }
    }


}
