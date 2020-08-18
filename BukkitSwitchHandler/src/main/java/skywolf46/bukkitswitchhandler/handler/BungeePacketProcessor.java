package skywolf46.bukkitswitchhandler.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.bukkit.Bukkit;
import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;
import skywolf46.bukkitswitchhandler.data.BungeePacketData;
import skywolf46.bukkitswitchhandler.data.UserPacketData;
import skywolf46.bukkitswitchhandler.event.*;

public class BungeePacketProcessor extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BungeePacketData bpd = (BungeePacketData) msg;
        System.out.println(bpd.isUserData());
        System.out.println(bpd.getCategory());
        if (bpd.isUserData()) {
            UserPacketData upd = (UserPacketData) bpd;
            switch (upd.getMode()) {
                case 0: {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitSwitchHandler.inst(), () -> {
                        Bukkit.getPluginManager().callEvent(new PlayerLoadEvent(upd.getBuffer(), 0, upd.getCategory(), upd.getUID()));
                    });
                }
                break;
                case 1: {
                    if (upd.getCategory().equals("ClearData")) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitSwitchHandler.inst(), () -> {
                            Bukkit.getPluginManager().callEvent(new PlayerClearDataEvent(upd.getBuffer(), 0, upd.getCategory(), upd.getUID()));
                        });
                    } else if (upd.getCategory().equals("Initial")) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitSwitchHandler.inst(), () -> {
                            Bukkit.getPluginManager().callEvent(new PlayerInitialLoadEvent(upd.getBuffer(), 0, upd.getCategory(), upd.getUID()));
                        });
                    } else {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitSwitchHandler.inst(), () -> {
                            Bukkit.getPluginManager().callEvent(new PlayerSaveEvent(upd.getBuffer(), 0, upd.getCategory(), upd.getUID()));
                        });
                    }
                }
                break;
                case 2: {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitSwitchHandler.inst(), () -> {
                        Bukkit.getPluginManager().callEvent(new PlayerReloadEvent(upd.getBuffer(), 0, upd.getCategory(), upd.getUID()));
                    });
                }
                break;
            }

        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitSwitchHandler.inst(), () -> {
                Bukkit.getPluginManager().callEvent(new BroadcastListeningEvent(bpd.getBuffer(), 0, bpd.getCategory()));
            });
        }
    }
}