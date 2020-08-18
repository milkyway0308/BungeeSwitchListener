package skywolf46.bukkitswitchhandler.util;

import io.netty.buffer.ByteBuf;
import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;
import skywolf46.bukkitswitchhandler.data.UserPacketData;

import java.util.UUID;
import java.util.function.Consumer;

public class Request {

    public static void saveComplete(String task, UUID uid) {
        BukkitSwitchHandler.getSocket().add(tar -> {
            tar.getSocketChannel().writeAndFlush(new UserPacketData(task, false, uid, 0));
        });
    }

    public static void reload(String task, UUID uid) {
        BukkitSwitchHandler.getSocket().add(tar -> {
//            ByteBuf bf = tar.getSocketChannel().alloc().buffer();
//            bf.writeByte(1);
//            writeString(bf, task);
//            writeString(bf, uid.toString());
//            bf.writeInt(0);
//            tar.getSocketChannel().writeAndFlush(bf);
            tar.getSocketChannel().writeAndFlush(new UserPacketData(task, false, uid, 2));
        });
    }

    public static void saveComplete(String task, UUID uid, Consumer<ByteBuf> buf) {
        BukkitSwitchHandler.getSocket().add(tar -> {
            UserPacketData upd = new UserPacketData(task, false, uid, 1);
            buf.accept(upd.getBuffer());
            tar.getSocketChannel().writeAndFlush(upd);
        });
    }


    public static void broadcast(String task, UUID player, Consumer<ByteBuf> buf) {
        BukkitSwitchHandler.getSocket().add(tar -> {
//            ByteBuf bf = tar.getSocketChannel().alloc().buffer();
//            bf.writeByte(2);
//            bf.writeLong(timestamp);
//            writeString(bf, task);
//            writeString(bf, player.toString());
//            writeBuffer(buf, tar, bf);
            UserPacketData upd = new UserPacketData(task, true, player, 2);
            buf.accept(upd.getBuffer());
            tar.getSocketChannel().writeAndFlush(upd);
        });
    }

//    private static void writeBuffer(Consumer<ByteBuf> buf, InfiniReadingSocket tar, ByteBuf bf) {
//        ByteBuf buffer = Unpooled.buffer();
//        buf.accept(buffer);
//        byte[] bufArr = buffer.array();
//        buffer.release();
//        bf.writeInt(bufArr.length);
//        buffer.writeBytes(bufArr);
//        tar.getSocketChannel().writeAndFlush(bf);
//    }

    public static void reload(String task, UUID player, Consumer<ByteBuf> buf) {
        BukkitSwitchHandler.getSocket().add(tar -> {
//            ByteBuf bf = tar.getSocketChannel().alloc().buffer();
//            bf.writeByte(1);
//            writeString(bf, task);
//            writeString(bf, player.toString());
//            writeBuffer(buf, tar, bf);
            UserPacketData upd = new UserPacketData(task, true, player, 2);
            buf.accept(upd.getBuffer());
            tar.getSocketChannel().writeAndFlush(upd);
        });
    }
}
