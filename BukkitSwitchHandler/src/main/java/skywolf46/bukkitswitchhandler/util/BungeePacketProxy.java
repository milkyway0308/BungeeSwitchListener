package skywolf46.bukkitswitchhandler.util;

import io.netty.buffer.ByteBuf;
import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;
import skywolf46.bukkitswitchhandler.data.BungeePacketData;
import skywolf46.bukkitswitchhandler.data.BungeeServerDirectPacketData;
import skywolf46.bukkitswitchhandler.data.BungeeTargetPacketData;
import skywolf46.bukkitswitchhandler.data.UserPacketData;
import skywolf46.bukkitswitchhandler.exception.BungeeNotInitializedException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class BungeePacketProxy {
    private BungeeClientSocket socket;
    private int port;

    public BungeePacketProxy(int bungeePort) {
        this.socket = new BungeeClientSocket(port = bungeePort, soc -> {
        });
    }

    public void reconnect() {
        this.socket.reconnect();
    }


    public void sendDirectPacket(String server, String task, UUID uid, Consumer<ByteBuf> buf) {
        if (socket == null)
            throw new BungeeNotInitializedException();
        socket.add(tar -> {
            BungeeServerDirectPacketData direct = new BungeeServerDirectPacketData(task, uid, server);
            if (buf != null)
                buf.accept(direct.getBuffer());
            tar.getSocketChannel().writeAndFlush(direct);
        });
    }

    public void sendDirectPacket(int serverPort, String task, UUID uid, Consumer<ByteBuf> buf) {
        if (socket == null)
            throw new BungeeNotInitializedException();
        socket.add(tar -> {
            BungeeServerDirectPacketData direct = new BungeeServerDirectPacketData(task, uid, serverPort);
            if (buf != null)
                buf.accept(direct.getBuffer());
            tar.getSocketChannel().writeAndFlush(direct);
        });
    }

    public void sendDirectPacket(String server, String task, Consumer<ByteBuf> buf) {
        sendDirectPacket(server, task, null, buf);
    }


    public void sendDirectPacket(int serverPort, String task, Consumer<ByteBuf> buf) {
        sendDirectPacket(serverPort, task, null, buf);
    }

    public void sendPacket(String task, UUID player, Consumer<ByteBuf> buf) {
        if (socket == null)
            throw new BungeeNotInitializedException();
        socket.add(tar -> {
            BungeeTargetPacketData upd = new BungeeTargetPacketData(task, player);
            if (buf != null)
                buf.accept(upd.getBuffer());
            tar.getSocketChannel().writeAndFlush(upd);
        });
    }


    public void sendPacket(String task, Consumer<ByteBuf> buf) {
        sendPacket(task, null, buf);
    }

    public void sendPacket(String task, UUID uid) {
        sendPacket(task, uid, null);
    }

    public void sendPacket(String task) {
        sendPacket(task, null, null);
    }

    public void broadcastDataPacket(String task, UUID player, Consumer<ByteBuf> buffer) {
        if (socket == null)
            throw new BungeeNotInitializedException();
        socket.add(tar -> {
            UserPacketData upd = new UserPacketData(task, true, player, 2);
            if (buffer != null)
                buffer.accept(upd.getBuffer());
            tar.getSocketChannel().writeAndFlush(upd);
        });
    }


    public void sendDataPacket(String task, UUID player, Consumer<ByteBuf> buffer) {
        if (socket == null)
            throw new BungeeNotInitializedException();
        socket.add(tar -> {
            UserPacketData upd = new UserPacketData(task, false, player, 2);
            if (buffer != null)
                buffer.accept(upd.getBuffer());
            tar.getSocketChannel().writeAndFlush(upd);
        });
    }

    public void sendDataPacket(String task, UUID player) {
        sendDataPacket(task, player, null);
    }

    public void sendBroadcastPacket(String task, Consumer<ByteBuf> buf) {
        if (socket == null)
            throw new BungeeNotInitializedException();
        socket.add(tar -> {
            BungeePacketData upd = new BungeePacketData(task, true);
            buf.accept(upd.getBuffer());
            tar.getSocketChannel().writeAndFlush(upd);
        });
    }


    public void sendLoadRequest(String task, UUID player, Consumer<ByteBuf> buf) {
        if (socket == null)
            throw new BungeeNotInitializedException();
        socket.add(tar -> {
            UserPacketData upd = new UserPacketData(task, false, player, 0);
//            upd.getBuffer().writeInt(BukkitSwitchHandler.getRegisteredProvider().size());
//            for (String n : BukkitSwitchHandler.getRegisteredProvider())
//                ByteBufUtility.writeString(upd.getBuffer(), n);
            if (buf != null)
                buf.accept(upd.getBuffer());
            tar.getSocketChannel().writeAndFlush(upd);
        });
    }

    public void sendLoadRequest(String task, UUID player) {
        sendLoadRequest(task, player, null);
    }


    public boolean isConnected() {
        return this.socket.isConnected();
    }

    public boolean isConnectionFailed() {
        return this.socket.failed();
    }

    public void sendProviderPacket(UUID player) {
        if (socket == null)
            throw new BungeeNotInitializedException();
        socket.add(tar -> {
//            System.out.println("Send provider packet");
//            System.out.println(BukkitSwitchHandler.getRegisteredProvider());
            UserPacketData upd = new UserPacketData("", false, player, 3);
            List<String> target = new ArrayList<>(BukkitSwitchHandler.getRegisteredProvider());
            upd.getBuffer().writeInt(target.size());
            for (String str : target)
                ByteBufUtility.writeString(upd.getBuffer(), str);
            tar.getSocketChannel().write(upd);
        });
    }
}
