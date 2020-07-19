package skywolf46.bukkitswitchhandler.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;
import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;
import skywolf46.bukkitswitchhandler.event.PlayerLoadRequestEvent;
import skywolf46.bukkitswitchhandler.event.PlayerSaveRequestEvent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class EventListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        switch (channel) {
            case "SWHandler|Request": {
                Bukkit.getPluginManager().callEvent(new PlayerLoadRequestEvent(UUID.fromString(new String(message))));

            }
            break;
            case "SWHandler|Prepare": {
                System.out.println("Ready to prepare");
                Bukkit.getPluginManager().callEvent(new PlayerSaveRequestEvent(UUID.fromString(new String(message))));
                player.sendPluginMessage(BukkitSwitchHandler.inst(), "SWHandler|Prepared", message);
            }
            break;

        }
    }
}
