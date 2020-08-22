package skywolf46.bungeeswitchlistener.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import skywolf46.bungeeswitchlistener.data.BungeePacketData;

import java.util.Map;

public class PacketDataEncoder extends MessageToByteEncoder<BungeePacketData> {
//    @Override
//    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        if(msg instanceof BungeePacketData){
//        }
//        super.write(ctx, msg, promise);
//    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BungeePacketData pac, ByteBuf byteBuf) throws Exception {
        byteBuf.writeByte(pac.getPacketID());
        pac.write(byteBuf);
//        System.out.println("Writing " + pac.getCategory());
    }


}
