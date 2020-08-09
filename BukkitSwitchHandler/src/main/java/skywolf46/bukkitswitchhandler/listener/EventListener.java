package skywolf46.bukkitswitchhandler.listener;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
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
        ByteArrayInputStream bais = new ByteArrayInputStream(message);
        DataInputStream dis = new DataInputStream(bais);
        try {
            byte type = dis.readByte();
            String task = dis.readUTF();
            UUID uid = UUID.fromString(dis.readUTF());
            if (type == 0) {
                BukkitSwitchHandler.load(task, uid, dis);
            }
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
