package skywolf46.bukkitswitchhandler.data;

import io.netty.buffer.ByteBuf;
import skywolf46.bukkitswitchhandler.util.ByteBufUtility;

import java.util.UUID;

public class BungeeServerDirectPacketData extends BungeePacketData {
    private UUID target = null;
    private String targetServer;
    private int port;

    public BungeeServerDirectPacketData(ByteBuf data) {
        super(data);
    }

    public BungeeServerDirectPacketData(String task, UUID uid, String targetServer) {
        super(task, false);
        this.target = uid;
        this.targetServer = targetServer;
    }

    public BungeeServerDirectPacketData(String task, UUID uid, int port) {
        super(task, false);
        this.target = uid;
        this.port = port;
    }

    public boolean isPortMode() {
        return targetServer == null;
    }

    @Override
    public byte getPacketID() {
        return 3;
    }

    @Override
    public UUID getUID() {
        return target;
    }

    @Override
    protected void readAdditional(ByteBuf buf) {
        if (!buf.readBoolean())
            this.targetServer = ByteBufUtility.readString(buf);
        else
            this.port = buf.readInt();
        if (buf.readBoolean()) {
            this.target = new UUID(buf.readLong(), buf.readLong());
        }
    }

    @Override
    protected void writeAdditional(ByteBuf buf) {
        buf.writeBoolean(isPortMode());
        if (isPortMode()) {
            buf.writeInt(port);
        } else {
            ByteBufUtility.writeString(buf, targetServer);
        }
        buf.writeBoolean(target != null);
        if (target != null) {
            buf.writeLong(target.getMostSignificantBits());
            buf.writeLong(target.getLeastSignificantBits());
        }
    }

    public String getTargetServer() {
        return targetServer;
    }
}
