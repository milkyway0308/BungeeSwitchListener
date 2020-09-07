package skywolf46.bukkitswitchhandler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.bukkit.Bukkit;
import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;
import skywolf46.bukkitswitchhandler.data.BungeePacketData;
import skywolf46.bukkitswitchhandler.data.UserPacketData;
import skywolf46.bukkitswitchhandler.event.*;

public class BungeePacketProcessor extends ChannelInboundHandlerAdapter {
    private static BungeePacketProcessor proc = new BungeePacketProcessor();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BungeePacketData bpd = (BungeePacketData) msg;
        switch (bpd.getPacketID()) {
            case 0: {
//                System.out.println("Recv bungee packet");
                Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitSwitchHandler.inst(), () -> {
                    Bukkit.getPluginManager().callEvent(new BroadcastListeningEvent(bpd.getBuffer(), 0, bpd.getCategory()));
                    bpd.getBuffer().release();
                });
            }
            break;
            case 1: {
//                System.out.println("Recv user packet");
                UserPacketData upd = (UserPacketData) bpd;
                switch (upd.getMode()) {
                    case 0: {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitSwitchHandler.inst(), () -> {
                            Bukkit.getPluginManager().callEvent(new PlayerLoadEvent(upd.getBuffer(), 0, upd.getCategory(), upd.getUID()));
                            upd.getBuffer().release();
                        });
                    }
                    break;
                    case 1: {
                        if (upd.getCategory().equals("ClearData")) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitSwitchHandler.inst(), () -> {
                                Bukkit.getPluginManager().callEvent(new PlayerClearDataEvent(upd.getBuffer(), 0, upd.getCategory(), upd.getUID()));
                                upd.getBuffer().release();
                            });
                        } else if (upd.getCategory().equals("Initial")) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitSwitchHandler.inst(), () -> {
                                Bukkit.getPluginManager().callEvent(new PlayerInitialLoadEvent(upd.getBuffer(), 0, upd.getCategory(), upd.getUID()));
                                upd.getBuffer().release();
                            });
                        } else {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitSwitchHandler.inst(), () -> {
                                Bukkit.getPluginManager().callEvent(new PlayerSaveEvent(upd.getBuffer(), 0, upd.getCategory(), upd.getUID()));
                                upd.getBuffer().release();
                            });
                        }
                    }
                    break;
                    case 2: {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitSwitchHandler.inst(), () -> {
                            Bukkit.getPluginManager().callEvent(new PlayerReloadEvent(upd.getBuffer(), 0, upd.getCategory(), upd.getUID()));
                            upd.getBuffer().release();
                        });
                    }
                    break;
                    case 3: {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitSwitchHandler.inst(), () -> {
                            PreviousProviderAnnounceEvent prov = new PreviousProviderAnnounceEvent(upd.getBuffer(), 0, upd.getCategory(), upd.getUID());
                            Bukkit.getPluginManager().callEvent(prov);
                            upd.getBuffer().release();
                            for (String n : BukkitSwitchHandler.getRegisteredProvider()) {
                                if (!prov.getPreviousProvider().contains(n))
                                    Bukkit.getPluginManager().callEvent(new PlayerForceLoadEvent(Unpooled.EMPTY_BUFFER, 0, n, upd.getUID()));
                            }
                        });
                    }
                }
            }
            break;
            case 2: {
                // Cannot accept; Bungeecord only packet
            }
            break;
            case 3: {
                Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitSwitchHandler.inst(), () -> {
                    Bukkit.getPluginManager().callEvent(new ServerTargetPacketListeningEvent(bpd.getBuffer(), 0, bpd.getCategory(), bpd.getUID()));
                    bpd.getBuffer().release();
                });
            }
        }
    }

    public static void forceProcess(BungeePacketData data) {
        try {
            proc.channelRead(null, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
