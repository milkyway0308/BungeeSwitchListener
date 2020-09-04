package skywolf46.bungeeswitchlistener.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import skywolf46.bungeeswitchlistener.util.ByteBufUtility;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class BungeePacketData {
    private String category;
    private ByteBuf buf;
    private boolean isBroadcast;
    private byte[] writerByte = null;
    private final Object LOCK = new Object();


    public BungeePacketData(ByteBuf data) {
        buf = Unpooled.buffer();
        read(data);
    }

    public BungeePacketData(String task, boolean isBroadcast) {
        buf = Unpooled.buffer();
        this.category = task;
        this.isBroadcast = isBroadcast;
    }

    public boolean isBroadcast() {
        return isBroadcast;
    }

    public byte getPacketID() {
        return 0;
    }

    public String getCategory() {
        return category;
    }


    public boolean isUserData() {
        return false;
    }

    public UUID getUID() {
        return null;
    }

    public void read(ByteBuf inBuf) {
        int length = inBuf.readableBytes();
        this.category = readString(inBuf);
        readAdditional(inBuf);
        this.isBroadcast = inBuf.readBoolean();
        int len = inBuf.readInt();
        byte[] b = new byte[len];
        inBuf.readBytes(b);
        this.buf.writeBytes(b);
    }

    public void write(ByteBuf buf) {
        synchronized (LOCK) {
            if (writerByte == null) {
                writerByte = ByteBufUtility.readAllBytes(this.buf);
                this.buf.release();
            }
        }
        writeString(buf, category);
        writeAdditional(buf);
        buf.writeBoolean(isBroadcast);
        buf.writeInt(writerByte.length);
        buf.writeBytes(writerByte);
//        System.out.println("Write " + toWrite);

    }

    protected void writeAdditional(ByteBuf buf) {

    }

    protected void readAdditional(ByteBuf buf) {

    }

    protected void writeString(ByteBuf bf, String str) {
        byte[] arr = str.getBytes();
        bf.writeShort(arr.length);
        bf.writeBytes(arr);
    }

    protected String readString(ByteBuf buf) {
        byte[] arr = new byte[buf.readShort()];
        buf.readBytes(arr);
        return new String(arr);
    }

    public ByteBuf getBuffer() {
        return buf;
    }


}
