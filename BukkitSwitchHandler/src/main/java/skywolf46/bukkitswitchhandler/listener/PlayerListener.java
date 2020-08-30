package skywolf46.bukkitswitchhandler.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;

public class PlayerListener implements Listener {
    @EventHandler
    public void ev(PlayerQuitEvent e) {
        BukkitSwitchHandler.getProxy().sendProviderPacket(e.getPlayer().getUniqueId());
    }
}
