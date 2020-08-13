package skywolf46.bukkitswitchhandler.event;

import io.netty.buffer.ByteBuf;
import org.bukkit.event.server.ServerEvent;

public abstract class BukkitSwitchHandlerEvent extends ServerEvent {
    private int initialIndex = 0;
    private ByteBuf buffer;
    private String category;

    public BukkitSwitchHandlerEvent(ByteBuf buffer, int initialIndex, String category) {
        this.buffer = buffer;
        this.initialIndex = initialIndex;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public ByteBuf getBuffer() {
        return buffer;
    }

    public void resetBuffer() {
        buffer.readerIndex(initialIndex);
    }
}
