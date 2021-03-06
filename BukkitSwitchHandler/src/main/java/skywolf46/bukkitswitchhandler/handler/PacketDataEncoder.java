package skywolf46.bukkitswitchhandler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import skywolf46.bukkitswitchhandler.data.BungeePacketData;

import java.util.List;

public class PacketDataEncoder extends MessageToMessageEncoder<BungeePacketData> {
//    @Override
//    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        if(msg instanceof BungeePacketData){
//        }
//        super.write(ctx, msg, promise);
//    }


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BungeePacketData pac, List<Object> list) throws Exception {
        ByteBuf byteBuf = channelHandlerContext.alloc().buffer();
        byteBuf.writeByte(pac.getPacketID());
        pac.write(byteBuf);
        list.add(byteBuf);
    }

}
