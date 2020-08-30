package skywolf46.bukkitswitchhandler.util;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class ByteBufUtility {

    public static void writeString(ByteBuf bf, String str) {
        byte[] arr = str.getBytes();
        bf.writeShort(arr.length);
        bf.writeBytes(arr);
    }

    public static String readString(ByteBuf buf) {
        int len = buf.readShort();
        byte[] data = new byte[len];
        buf.readBytes(data);
        return new String(data);
    }

    public static void writeUUID(ByteBuf buf, UUID uid) {
        buf.writeLong(uid.getMostSignificantBits());
        buf.writeLong(uid.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }


}
