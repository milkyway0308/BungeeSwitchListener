package skywolf46.bukkitswitchhandler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class ByteSendingEncoder extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        ByteBuf next = ctx.alloc().ioBuffer();
        next.writeInt(byteBuf.readableBytes());
        next.writeBytes(byteBuf);
        byteBuf.release();
        ctx.writeAndFlush(next);
    }

}
