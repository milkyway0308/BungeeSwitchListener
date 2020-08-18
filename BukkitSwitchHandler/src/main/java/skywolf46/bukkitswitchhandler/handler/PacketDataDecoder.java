package skywolf46.bukkitswitchhandler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import skywolf46.bukkitswitchhandler.data.BungeePacketData;
import skywolf46.bukkitswitchhandler.data.UserPacketData;

import java.util.List;

public class PacketDataDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte type = byteBuf.readByte();
        switch (type) {
            case 0: {
                list.add(new BungeePacketData(byteBuf));
            }
            break;
            case 1: {
                list.add(new UserPacketData(byteBuf));
            }
            break;
            default:
                throw new IllegalStateException("Cannot read data: packet type " + type + " is not defined");
        }
    }
}
