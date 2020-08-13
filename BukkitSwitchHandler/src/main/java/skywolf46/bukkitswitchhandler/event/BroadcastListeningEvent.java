package skywolf46.bukkitswitchhandler.event;

import io.netty.buffer.ByteBuf;
import org.bukkit.event.HandlerList;

public class BroadcastListeningEvent extends BukkitSwitchHandlerEvent {
    private static HandlerList handlerList = new HandlerList();

    public BroadcastListeningEvent(ByteBuf buffer, int initialIndex, String category) {
        super(buffer, initialIndex, category);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
