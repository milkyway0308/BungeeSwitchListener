package skywolf46.bukkitswitchhandler.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_12_R1.PacketDataSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayInCustomPayload;
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.plugin.messaging.StandardMessenger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PlayerReloadRequest {
    private ByteArrayOutputStream baos;
    private DataOutputStream dos;
    private String category;
//    private static InfiniReadingSocket socket = new InfiniReadingSocket();


    public PlayerReloadRequest(String category) {
        this.category = category;
        baos = new ByteArrayOutputStream();
        dos = new DataOutputStream(baos);
    }

    public String getCategory() {
        return category;
    }

    public void send(UUID uid) {
        byte[] b1 = uid.toString().getBytes();
        byte[] b2 = new byte[b1.length + baos.size()];
        System.arraycopy(b1, 0, b2, 0, b1.length);
        System.arraycopy(baos.toByteArray(), 0, b2, b1.length, baos.size());
//        PacketPlayOutCustomPayload p = new PacketPlayOutCustomPayload(category, new PacketDataSerializer(Unpooled.wrappedBuffer(b2)));
        ByteBuf bf = Unpooled.buffer();
        bf.writeBytes(b2);
//        socket.add(a -> {
//            a.getSocketChannel().writeAndFlush(bf);
//        });
    }

    public void write(String str) {
        try {
            dos.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void write(char str) {
        try {
            dos.writeChar(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void write(byte[] by) {
        try {
            dos.write(by);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(boolean b) {
        try {
            dos.writeBoolean(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(long l) {
        try {
            dos.writeLong(l);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void write(double d) {
        try {
            dos.writeDouble(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(float f) {
        try {
            dos.writeFloat(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(int i) {
        try {
            dos.writeInt(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void write(short s) {
        try {
            dos.writeByte(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void write(byte b) {
        try {
            dos.writeByte(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
