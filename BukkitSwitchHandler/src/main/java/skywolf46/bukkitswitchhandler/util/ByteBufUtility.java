package skywolf46.bukkitswitchhandler.util;

import io.netty.buffer.ByteBuf;

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
}
