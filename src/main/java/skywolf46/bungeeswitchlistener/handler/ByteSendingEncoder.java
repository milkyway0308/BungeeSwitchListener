package skywolf46.bungeeswitchlistener.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;

public class ByteSendingEncoder extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        ByteBuf next = ctx.alloc().ioBuffer();
//        System.out.println("...Writing " + byteBuf.readableBytes() + " bytes");
        next.writeInt(byteBuf.readableBytes());
        next.writeBytes(byteBuf);
        byteBuf.release();
        ctx.writeAndFlush(next);
    }

}
