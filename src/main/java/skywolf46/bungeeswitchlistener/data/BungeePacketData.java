package skywolf46.bungeeswitchlistener.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Arrays;
import java.util.UUID;

public class BungeePacketData {
    private String category;
    private ByteBuf buf;
    private boolean isBroadcast;

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
        this.buf.readerIndex(0);
        writeString(buf, category);
        writeAdditional(buf);
        buf.writeBoolean(isBroadcast);
        int toWrite = this.buf.readableBytes();
        buf.writeInt(toWrite);
        buf.writeBytes(this.buf, toWrite);
        this.buf.release();
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
