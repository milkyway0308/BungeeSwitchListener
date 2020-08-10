package skywolf46.bukkitswitchhandler.listener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class BroadcastListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        ByteArrayInputStream bais = new ByteArrayInputStream(message);
        DataInputStream dis = new DataInputStream(bais);
        try {
            String task = dis.readUTF();
            UUID uid = UUID.fromString(dis.readUTF());
            BukkitSwitchHandler.reload(task, uid, dis);
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
