package skywolf46.bungeeswitchlistener.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.md_5.bungee.BungeeCord;
import org.fusesource.jansi.Ansi;
import skywolf46.bungeeswitchlistener.BungeeSwitchListener;

public class PacketHijackingHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf bb = (ByteBuf) msg;
        bb.markReaderIndex();
        if (bb.readableBytes() == 12) {
            int x1 = bb.readInt();
            int x2 = bb.readInt();
            if (x1 == 98012 && x2 == 70031) {
                try {
                    while (ctx.pipeline().removeFirst() != null) {
                    }
                } catch (Exception ex) {

                }
                int port = bb.readInt();
//                ctx.pipeline().addFirst("fake-connection-listener", new ByteReadHandler());
                ctx.pipeline().addFirst("packet-data-encoder", new PacketDataEncoder());
                ctx.pipeline().addLast("packet-data-decoder", new PacketDataDecoder());
//                ctx.pipeline().addFirst("packet-encoder-sub", new PacketDataEncoderSub());
                ctx.pipeline().addAfter("packet-data-decoder", "packet-data-processor", new BungeePacketProcessor(port));
                BungeeCord.getInstance().getConsole().sendMessage(Ansi.ansi().fg(Ansi.Color.YELLOW).a("BungeeSwitchListener").fg(Ansi.Color.WHITE).a(" | ").fg(Ansi.Color.GREEN).a("Incoming server connection : Port " + port).toString());
                ByteBuf bf = Unpooled.buffer();
                bf.writeInt(108487);
                bf.writeInt(498130);
                ctx.channel().writeAndFlush(bf);
                BungeeSwitchListener.register(port, ctx.channel());
            }
        } else {
            ctx.pipeline().remove(this);
            super.channelRead(ctx, msg);
        }
    }


}
