package skywolf46.bukkitswitchhandler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import skywolf46.bukkitswitchhandler.data.BungeePacketData;

public class PacketDataEncoder extends MessageToByteEncoder<BungeePacketData> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BungeePacketData pac, ByteBuf byteBuf) throws Exception {
        byteBuf.writeByte(pac.getPacketID());
        pac.write(byteBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
