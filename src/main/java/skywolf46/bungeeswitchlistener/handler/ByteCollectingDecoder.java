package skywolf46.bungeeswitchlistener.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.md_5.bungee.BungeeCord;
import org.fusesource.jansi.Ansi;
import skywolf46.bungeeswitchlistener.BungeeSwitchListener;

import java.util.List;

public class ByteCollectingDecoder extends ChannelInboundHandlerAdapter {
    private int lastBytesize = -1;
    private int port;
    private ByteBuf buffer = Unpooled.directBuffer();

    public ByteCollectingDecoder(int port) {
        this.port = port;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bx = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bx);
        buffer.writeBytes(bx);
        if (lastBytesize == -1) {
            if (buffer.readableBytes() < 4)
                return;
            lastBytesize = buffer.readInt();
//            System.out.println("Byte start... " + lastBytesize);
//            System.out.println("Collecting " + lastBytesize + "; Current " + buffer.readableBytes());
            while (buffer.readableBytes() >= lastBytesize) {
                if (lastBytesize == -1)
                    break;
//                System.out.println("...Decode amount: " + lastBytesize);
                PacketDataDecoder.forceDecode(ctx, readByteData(buffer));
                if (buffer.readableBytes() >= 4)
                    lastBytesize = buffer.readInt();
                else
                    lastBytesize = -1;
            }
        } else {
//            System.out.println("Collecting " + lastBytesize + "; Current " + buffer.readableBytes());
//            System.out.println("Continue decoding..");
            while (buffer.readableBytes() >= lastBytesize) {
                if (lastBytesize == -1)
                    break;
//                System.out.println("Reading " + lastBytesize);
                PacketDataDecoder.forceDecode(ctx, readByteData(buffer));
                if (buffer.readableBytes() >= 4)
                    lastBytesize = buffer.readInt();
                else
                    lastBytesize = -1;
            }
        }
    }


    //    @Override
//    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
//        buffer.writeBytes(buffer, byteBuf.readableBytes());
//        if (lastBytesize == -1) {
//            if (buffer.readableBytes() < 4)
//                return;
//            lastBytesize = buffer.readInt();
////            System.out.println("Collecting " + lastBytesize + "; Current " + buffer.readableBytes());
//            while (buffer.readableBytes() >= lastBytesize) {
//                if (lastBytesize == -1)
//                    break;
//                PacketDataDecoder.forceDecode(channelHandlerContext, readByteData(buffer));
//                if (buffer.readableBytes() >= 4)
//                    lastBytesize = buffer.readInt();
//            }
//        } else {
////            System.out.println("Collecting " + lastBytesize + "; Current " + buffer.readableBytes());
//            while (buffer.readableBytes() >= lastBytesize) {
//                if (lastBytesize == -1)
//                    break;
//                PacketDataDecoder.forceDecode(channelHandlerContext, readByteData(buffer));
//                if (buffer.readableBytes() >= 4)
//                    lastBytesize = buffer.readInt();
//            }
//        }
//    }

    private ByteBuf readByteData(ByteBuf byteBuf) {
        byte[] bs = new byte[lastBytesize];
        byteBuf.readBytes(bs);
        lastBytesize = -1;
        return Unpooled.wrappedBuffer(bs);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        BungeeCord.getInstance().getConsole().sendMessage(Ansi.ansi().fg(Ansi.Color.YELLOW).a("BungeeSwitchListener").fg(Ansi.Color.WHITE).a(" | ").fg(Ansi.Color.RED).a("Server connection disconnected : Port " + port).toString());
        BungeeSwitchListener.unregister(port);
    }
}
