package skywolf46.bukkitswitchhandler.event;

import io.netty.buffer.ByteBuf;
import org.bukkit.event.HandlerList;
import skywolf46.bukkitswitchhandler.util.ByteBufUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PreviousProviderAnnounceEvent extends BukkitSwitchHandlerPlayerEvent {
    private static HandlerList handlerList = new HandlerList();
    private List<String> prev = new ArrayList<>();
    private UUID uid;

    public PreviousProviderAnnounceEvent(ByteBuf buffer, int initialIndex, String category, UUID uid) {
        super(buffer, initialIndex, category);
        this.uid = uid;
        int amount = buffer.readInt();
        for (int i = 0; i < amount; i++)
            prev.add(ByteBufUtility.readString(buffer));
    }

    public List<String> getPreviousProvider() {
        return prev;
    }

    @Override
    public UUID getUID() {
        return uid;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
