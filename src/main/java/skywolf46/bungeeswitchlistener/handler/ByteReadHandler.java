package skywolf46.bungeeswitchlistener.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.UUID;

public class ByteReadHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf bb = (ByteBuf) msg;
        byte type = bb.readByte();
        String task = readString(bb);

        UUID uid = UUID.fromString(readString(bb));
        byte[] dat = new byte[bb.readInt()];
        bb.readBytes(dat);
        switch (type) {
            case 0:
            case 1: {
                ProxiedPlayer pp = BungeeCord.getInstance().getPlayer(uid);
                if (pp != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(baos);
                    dos.writeByte(type);
                    dos.writeUTF(task);
                    dos.writeUTF(uid.toString());
                    dos.writeInt(dat.length);
                    dos.write(dat);
                    pp.getServer().sendData("MC|BungeeSwitcher", baos.toByteArray());
                    dos.close();
                }
            }// Save/Reload
            break;
            case 2: {
                bb.readerIndex(1);
                byte[] data = new byte[bb.readableBytes()];
                bb.readBytes(data);
                for (ServerInfo sv : ProxyServer.getInstance().getServers().values()) {
                    sv.sendData("MC|BSLBroadcast", data);
                }
            }// Broadcast
            break;
        }

    }

    private String readString(ByteBuf buf) {
        int len = buf.readShort();
        byte[] data = new byte[len];
        buf.readBytes(data);
        return new String(data);
    }
}
