package skywolf46.bukkitswitchhandler.event;

import io.netty.buffer.ByteBuf;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PlayerReloadEvent extends BukkitSwitchHandlerPlayerEvent {
    private static HandlerList handlerList = new HandlerList();
    private UUID uid;

    public PlayerReloadEvent(ByteBuf buffer, int initialIndex, String category, UUID uid) {
        super(buffer, initialIndex, category);
        this.uid = uid;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public UUID getUID() {
        return uid;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
