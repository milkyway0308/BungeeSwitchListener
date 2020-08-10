package skywolf46.bukkitswitchhandler.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;

import java.util.UUID;
import java.util.function.Consumer;

public class Request {

    public static void saveComplete(String task, UUID uid) {
        BukkitSwitchHandler.getSocket().add(tar -> {
            ByteBuf bf = tar.getSocketChannel().alloc().buffer();
            bf.writeByte(0);
            writeString(bf, task);
            writeString(bf, uid.toString());
            bf.writeInt(0);
            tar.getSocketChannel().writeAndFlush(bf);
        });
    }

    public static void reload(String task, UUID uid) {
        BukkitSwitchHandler.getSocket().add(tar -> {
            ByteBuf bf = tar.getSocketChannel().alloc().buffer();
            bf.writeByte(1);
            writeString(bf, task);
            writeString(bf, uid.toString());
            bf.writeInt(0);
            tar.getSocketChannel().writeAndFlush(bf);
        });
    }

    private static void writeString(ByteBuf bf, String str) {
        byte[] arr = str.getBytes();
        bf.writeShort(arr.length);
        bf.writeBytes(arr);
    }

    public static void saveComplete(String task, UUID player, Consumer<ByteBuf> buf) {
        BukkitSwitchHandler.getSocket().add(tar -> {
            ByteBuf bf = tar.getSocketChannel().alloc().buffer();
            bf.writeByte(0);
            writeString(bf, task);
            writeString(bf, player.toString());
            writeBuffer(buf, tar, bf);
        });
    }

    public static void broadcast(String task, UUID player, Consumer<ByteBuf> buf) {
        BukkitSwitchHandler.getSocket().add(tar -> {
            ByteBuf bf = tar.getSocketChannel().alloc().buffer();
            bf.writeByte(2);
            writeString(bf, task);
            writeString(bf, player.toString());
            writeBuffer(buf, tar, bf);
        });
    }

    private static void writeBuffer(Consumer<ByteBuf> buf, InfiniReadingSocket tar, ByteBuf bf) {
        ByteBuf buffer = Unpooled.buffer();
        buf.accept(buffer);
        byte[] bufArr = buffer.array();
        buffer.release();
        bf.writeInt(bufArr.length);
        buffer.writeBytes(bufArr);
        tar.getSocketChannel().writeAndFlush(bf);
    }

    public static void reload(String task, UUID player, Consumer<ByteBuf> buf) {
        BukkitSwitchHandler.getSocket().add(tar -> {
            ByteBuf bf = tar.getSocketChannel().alloc().buffer();
            bf.writeByte(1);
            writeString(bf, task);
            writeString(bf, player.toString());
            writeBuffer(buf, tar, bf);
        });
    }
}
