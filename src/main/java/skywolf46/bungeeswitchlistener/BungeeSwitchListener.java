package skywolf46.bungeeswitchlistener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.netty.PipelineUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import skywolf46.bungeeswitchlistener.data.BungeePacketData;
import skywolf46.bungeeswitchlistener.hyjacking.BungeeTrojanHandler;
import skywolf46.bungeeswitchlistener.listener.PlayerJoinListener;
import skywolf46.bungeeswitchlistener.util.FinalReleaser;

import java.lang.reflect.Field;
import java.util.HashMap;

public final class BungeeSwitchListener extends Plugin {
    private static final HashMap<Integer, Channel> context = new HashMap<>();

    public static void register(int port, Channel ctx) {
        context.put(port, ctx);
    }

    public static Channel get(int port) {
        return context.get(port);
    }

    public static void broadcast(Channel ctx, BungeePacketData bpd) {
        for (Channel cht : context.values()) {
            if (cht == ctx)
                continue;
            cht.writeAndFlush(bpd);
        }
        bpd.getBuffer().release();
    }

    public static void unregister(int port) {
        context.remove(port);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
//        ProxyServer.getInstance().registerChannel("MC|BungeeSwitcher");
//        ProxyServer.getInstance().registerChannel("MC|InitialLoad");
        BungeeCord.getInstance().getPluginManager().registerListener(this, new PlayerJoinListener());

        BungeeCord.getInstance().getConsole().sendMessage(Ansi.ansi().fg(Ansi.Color.RED).a("BungeeTrojan").fg(Ansi.Color.WHITE).a(" | ").a("Releasing...").toString());
        try {
            Field fl = PipelineUtils.class.getField("SERVER_CHILD");
            FinalReleaser.release(fl);
            BungeeCord.getInstance().getConsole().sendMessage(Ansi.ansi().fg(Ansi.Color.RED).a("BungeeTrojan").fg(Ansi.Color.WHITE).a(" | ").a("Replacing...").toString());
            fl.set(null, new BungeeTrojanHandler());
            BungeeCord.getInstance().getConsole().sendMessage(Ansi.ansi().fg(Ansi.Color.RED).a("BungeeTrojan").fg(Ansi.Color.WHITE).a(" | ").fg(Ansi.Color.GREEN).a("Listener is under control").toString());

//            System.out.println("§cBungeeTrojan §e| §aListener is under control");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        ServerConnection sc = (ServerConnection) ProxyServer.getInstance().getServerInfo("");
//        for(ServerInfo scc : ProxyServer.getInstance().getServers().values()){
//            System.out.println(scc.getName() + " - binding hijacker");
//            ServerConnection sc = (ServerConnection) scc;
//            sc.getCh().getHandle().pipeline().addAfter("packet-decoder","packet-hijacking",new PacketHijackingHandler());
//        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
