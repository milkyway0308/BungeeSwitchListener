package skywolf46.bukkitswitchhandler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ByteCollectingDecoder extends ByteToMessageDecoder {
    private int lastBytesize = -1;
    private final Object LOCK = new Object();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (lastBytesize == -1) {
            if (byteBuf.readableBytes() < 4)
                return;
            lastBytesize = byteBuf.readInt();
//            System.out.println("Collecting " + lastBytesize + "; Current " + byteBuf.readableBytes());
            while (byteBuf.readableBytes() >= lastBytesize) {
                if (lastBytesize == -1)
                    break;
                list.add(readByteData(byteBuf));
                if (byteBuf.readableBytes() >= 4)
                    lastBytesize = byteBuf.readInt();
            }
        } else {
//            System.out.println("Collecting " + lastBytesize + "; Current " + byteBuf.readableBytes());
            while (byteBuf.readableBytes() >= lastBytesize) {
                if (lastBytesize == -1)
                    break;
                list.add(readByteData(byteBuf));
                if (byteBuf.readableBytes() >= 4)
                    lastBytesize = byteBuf.readInt();
            }
        }
    }

    private ByteBuf readByteData(ByteBuf byteBuf) {
        byte[] bs = new byte[lastBytesize];
        byteBuf.readBytes(bs);
        lastBytesize = -1;
        return Unpooled.wrappedBuffer(bs);
    }


}
