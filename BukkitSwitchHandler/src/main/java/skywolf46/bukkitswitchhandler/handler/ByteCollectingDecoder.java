package skywolf46.bukkitswitchhandler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ByteCollectingDecoder extends ChannelInboundHandlerAdapter {
    private int lastBytesize = -1;

    private ByteBuf buffer = Unpooled.directBuffer();

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
                PacketDataDecoder.forceDecode(readByteData(buffer));
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
                PacketDataDecoder.forceDecode(readByteData(buffer));
                if (buffer.readableBytes() >= 4)
                    lastBytesize = buffer.readInt();
                else
                    lastBytesize = -1;
            }
        }
    }
//    @Override
//    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
//
//    }

    private ByteBuf readByteData(ByteBuf byteBuf) {
        byte[] bs = new byte[lastBytesize];
        byteBuf.readBytes(bs);
        lastBytesize = -1;
        return Unpooled.wrappedBuffer(bs);
    }


}
