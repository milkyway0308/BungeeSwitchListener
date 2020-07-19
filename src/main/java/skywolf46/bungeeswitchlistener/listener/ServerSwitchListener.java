package skywolf46.bungeeswitchlistener.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class ServerSwitchListener implements Listener {
    private HashSet<String> moved = new HashSet<>();

    @EventHandler
    public void ev(ServerSwitchEvent e) {
        e.getPlayer().getServer().sendData("SWHandler|Prepare", e.getPlayer().getUniqueId().toString().getBytes());
//        for (ServerInfo si : ProxyServer.getInstance().getServers().values()) {
//            si.sendData("bungeeHandler|Disconnect", e.getPlayer().getUniqueId().toString().getBytes());
//        }
    }

    @EventHandler
    public void ev(PluginMessageEvent e) {
        switch (e.getTag()) {
            case "SWHandler|Reload": {
                ByteArrayInputStream baos = new ByteArrayInputStream(e.getData());
                DataInputStream dis = new DataInputStream(baos);
                try {
                    String uid = dis.readUTF();
                    UUID u = UUID.fromString(uid);
                    ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(u);
                    if (pp != null) {
                        pp.getServer().sendData("SWHandler|Refresh", e.getData());
                    }
                    // If not, ignore update request
                } catch (IOException exx) {
                    exx.printStackTrace();
                }
            }
            break;
            case "SWHandler|Prepared": {
                String uid = new String(e.getData());
                UUID next = UUID.fromString(uid);
                ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(next);
                if (pp != null) {
                    pp.getServer().sendData("SWHandler|Request", e.getData());
                }
            }
            break;
        }
    }
}
