package skywolf46.bukkitswitchhandler.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.Bukkit;
import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;

public class BungeeErrorHandler extends ChannelHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Bukkit.getConsoleSender().sendMessage("§6BungeeSwitchHandler §7| §4Bungee disconnected! §cReconnecting...");
        BukkitSwitchHandler.startBungeeConnection();
        ctx.close();
    }
}
