package skywolf46.bukkitswitchhandler.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PlayerLoadRequestEvent extends Event {
    private static HandlerList handlerList = new HandlerList();

    private UUID uid;

    public PlayerLoadRequestEvent(UUID uid) {
        this.uid = uid;
    }

    public UUID getUid() {
        return uid;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
