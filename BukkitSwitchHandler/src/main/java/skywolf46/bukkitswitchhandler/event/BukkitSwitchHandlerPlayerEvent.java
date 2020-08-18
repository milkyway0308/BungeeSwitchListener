package skywolf46.bukkitswitchhandler.event;

import io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class BukkitSwitchHandlerPlayerEvent extends BukkitSwitchHandlerEvent {

    public BukkitSwitchHandlerPlayerEvent(ByteBuf buffer, int initialIndex, String category) {
        super(buffer, initialIndex, category);
    }

    public abstract UUID getUID();

    public void syncPlayerTask(Consumer<Player> sp) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitSwitchHandler.inst(), () -> {
            checkPlayer(sp);
        });
    }

    private void checkPlayer(Consumer<Player> sp) {
        Player pl = Bukkit.getPlayer(getUID());
        if (pl == null)
            Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitSwitchHandler.inst(), () -> {
                checkPlayer(sp);
            }, 1L);
        else
            sp.accept(pl);
    }

}
