package skywolf46.bungeeswitchlistener.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import skywolf46.bungeeswitchlistener.data.BungeePacketData;
import skywolf46.bungeeswitchlistener.data.BungeeTargetPacketData;
import skywolf46.bungeeswitchlistener.data.UserPacketData;

import java.util.ArrayList;
import java.util.List;

public class PacketDataDecoder extends ByteToMessageDecoder {
    private static PacketDataDecoder decoder = new PacketDataDecoder();

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
            case 2: {
                list.add(new BungeeTargetPacketData(byteBuf));
            }
            break;
            default:
                throw new IllegalStateException("Cannot read data: packet type " + type + " is not defined");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        cause.printStackTrace();
    }

    public static void forceDecode(ChannelHandlerContext context, ByteBuf buf) {
        List<Object> li = new ArrayList<>();
        try {
            decoder.decode(null, buf, li);
            BungeePacketProcessor.forceProcess(context, (BungeePacketData) li.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
