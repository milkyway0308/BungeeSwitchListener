package skywolf46.bungeeswitchlistener.data;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class BungeeTargetPacketData extends BungeePacketData {
    private UUID playerUID;

    public BungeeTargetPacketData(ByteBuf data) {
        super(data);
    }

    @Override
    public byte getPacketID() {
        return 2;
    }

    @Override
    public UUID getUID() {
        return this.playerUID;
    }

    @Override
    protected void readAdditional(ByteBuf buf) {
        if (buf.readBoolean()) {
            this.playerUID = new UUID(buf.readLong(), buf.readLong());
        } else {
            this.playerUID = null;
        }
    }

    @Override
    protected void writeAdditional(ByteBuf buf) {
        buf.writeBoolean(playerUID != null);
        if (playerUID != null) {
            buf.writeLong(playerUID.getMostSignificantBits());
            buf.writeLong(playerUID.getLeastSignificantBits());
        }
    }
}
