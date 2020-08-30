package skywolf46.bukkitswitchhandler.data;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class BungeeTargetPacketData extends BungeePacketData {
    private UUID target = null;

    public BungeeTargetPacketData(ByteBuf data) {
        super(data);
    }

    public BungeeTargetPacketData(String task, UUID uid) {
        super(task, false);
        this.target = uid;
    }

    @Override
    public byte getPacketID() {
        return 2;
    }

    @Override
    public UUID getUID() {
        return target;
    }

    @Override
    protected void readAdditional(ByteBuf buf) {
        if (buf.readBoolean()) {
            this.target = new UUID(buf.readLong(), buf.readLong());
        }
    }

    @Override
    protected void writeAdditional(ByteBuf buf) {
        buf.writeBoolean(target != null);
        if (target != null) {
            buf.writeLong(target.getMostSignificantBits());
            buf.writeLong(target.getLeastSignificantBits());
        }
    }
}
