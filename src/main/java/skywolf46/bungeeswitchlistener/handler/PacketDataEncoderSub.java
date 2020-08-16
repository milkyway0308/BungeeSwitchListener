package skywolf46.bungeeswitchlistener.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import skywolf46.bungeeswitchlistener.data.UserPacketData;

public class PacketDataEncoderSub extends MessageToByteEncoder<UserPacketData> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, UserPacketData pac, ByteBuf byteBuf) throws Exception {
        byteBuf.writeByte(pac.getPacketID());
        pac.write(byteBuf);
    }
}
