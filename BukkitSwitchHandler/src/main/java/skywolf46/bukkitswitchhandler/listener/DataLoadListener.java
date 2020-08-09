package skywolf46.bukkitswitchhandler.listener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;

import java.util.UUID;

public class DataLoadListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        BukkitSwitchHandler.load(UUID.fromString(new String(message)));
    }
}
