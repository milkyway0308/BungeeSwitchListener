package skywolf46.bungeeswitchlistener.data;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class UserPacketData extends BungeePacketData {
    private UUID uid;
    private int mode;

    public UserPacketData(String category, boolean isBroadcast, UUID uid, int mode) {
        super(category, isBroadcast);
        this.uid = uid;
        this.mode = mode;
    }

    public UserPacketData(ByteBuf buf) {
        super(buf);
    }

    @Override
    public byte getPacketID() {
        return 1;
    }

    public int getMode() {
        return mode;
    }

    @Override
    public boolean isUserData() {
        return true;
    }

    @Override
    public UUID getUID() {
        return uid;
    }

    @Override
    protected void writeAdditional(ByteBuf buf) {
        buf.writeLong(uid.getLeastSignificantBits());
        buf.writeLong(uid.getMostSignificantBits());
        buf.writeInt(mode);
    }

    @Override
    protected void readAdditional(ByteBuf buf) {
        this.uid = new UUID(buf.readLong(), buf.readLong());
        this.mode = buf.readInt();
    }
}
