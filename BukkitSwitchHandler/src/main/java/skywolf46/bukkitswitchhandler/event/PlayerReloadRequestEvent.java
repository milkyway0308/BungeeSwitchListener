package skywolf46.bukkitswitchhandler.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.io.DataOutputStream;
import java.util.UUID;

public class PlayerReloadRequestEvent extends Event {
    private static HandlerList handlerList = new HandlerList();

    private UUID uid;

    private String category;
    private DataOutputStream dos;

    public PlayerReloadRequestEvent(UUID uid, String category, DataOutputStream data) {
        this.uid = uid;
        this.category = category;
        this.dos = data;
    }

    public DataOutputStream getStream() {
        return dos;
    }

    public String getCategory() {
        return category;
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
