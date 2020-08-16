package skywolf46.bukkitswitchhandler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import skywolf46.bukkitswitchhandler.data.BungeePacketData;
import skywolf46.bukkitswitchhandler.data.UserPacketData;

public class PacketDataEncoderSub extends MessageToByteEncoder<UserPacketData> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, UserPacketData pac, ByteBuf byteBuf) throws Exception {
        System.out.println("Write and Flush");
        byteBuf.writeByte(pac.getPacketID());
        pac.write(byteBuf);
    }
}
