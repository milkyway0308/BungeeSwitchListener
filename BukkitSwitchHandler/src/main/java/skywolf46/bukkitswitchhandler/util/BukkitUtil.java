package skywolf46.bukkitswitchhandler.util;

import com.github.luben.zstd.Zstd;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;
import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class BukkitUtil {
    private static Method EXTRACT;
    private static Method IMPORT;
    private static Method CRAFTITEM_COPY;
    private static Method BUKKIT_COPY;
    private static Method NET_ITEMSTACK_NBT_EXTRACT;
    private static Method NET_ITEMSTACK_NBT_IMPORT;

    static {
        try {
            Class c = BukkitVersionUtil.getOBCClass("inventory.CraftItemStack");
            CRAFTITEM_COPY = c.getMethod("asNMSCopy", Class.forName("org.bukkit.inventory.ItemStack"));
            BUKKIT_COPY = c.getMethod("asBukkitCopy", BukkitVersionUtil.getNMSClass("ItemStack"));
            c = BukkitVersionUtil.getNMSClass("ItemStack");
            NET_ITEMSTACK_NBT_EXTRACT = c.getMethod("getTag");
            NET_ITEMSTACK_NBT_IMPORT = c.getMethod("setTag", BukkitVersionUtil.getNMSClass("NBTTagCompound"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    static {
        Class cl = null;
        try {
            cl = BukkitVersionUtil.getNMSClass("NBTCompressedStreamTools");
            EXTRACT = cl.getMethod("a", BukkitVersionUtil.getNMSClass("NBTTagCompound"), DataOutput.class);
            IMPORT = cl.getMethod("a", DataInputStream.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static String toString(ItemStack item) {
        return toString(item, false);
    }

    public static byte[] toByte(ItemStack item, boolean compress) {
        try {
//            long start = System.currentTimeMillis();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF(item.getType().name());
            dos.writeInt(item.getAmount());
            dos.writeShort(item.getDurability());
            Object ex = extractNBT(item);
            if (ex == null)
                dos.writeBoolean(false);
            else {
                dos.writeBoolean(true);
                EXTRACT.invoke(null, ex, dos);
            }
            dos.flush();
            dos.close();
            byte[] target;

            if (compress) {
                byte[] arr = baos.toByteArray();
                baos.close();
                if (arr.length <= 40) {
                    target = new byte[arr.length + 1];
                    target[0] = 0;
                } else {
                    arr = Zstd.compress(arr,0);
                    target = new byte[arr.length + 1];
                    target[0] = 1;
                }
                System.arraycopy(arr, 0, target, 1, arr.length);
            } else {
                target = baos.toByteArray();
                baos.close();
            }
            return target;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String toString(ItemStack item, boolean compress) {
        return Base64.getEncoder().encodeToString(toByte(item, compress));
    }

    public static ItemStack fromString(String str) {
        return fromString(str, false);
    }

    public static ItemStack fromString(String str, boolean compress) {
        return fromByte(Base64.getDecoder().decode(str), compress);
    }

    public static ItemStack fromByte(byte[] arr, boolean compress) {
        try {
            if (compress) {
                boolean decompressFlag = arr[0] == 1;
                arr = Arrays.copyOfRange(arr, 1, arr.length);
                if (decompressFlag) {
                    long size = Zstd.decompressedSize(arr);
                    arr = Zstd.decompress(arr, (int) size);
                }
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(arr);
            DataInputStream dis = new DataInputStream(bais);
            String matter = dis.readUTF();
            Material mat = Material.getMaterial(matter);
//            System.out.println("Material: " + matter);
            if (mat == null)
                return null;
            ItemStack item = new ItemStack(mat, dis.readInt(), dis.readShort());
            if (dis.readBoolean()) {
                Object nbt = IMPORT.invoke(null, dis);
                return importNBT(item, nbt);
            }
            return item;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ItemStack importNBT(ItemStack item, Object compound) {
        try {
            Object next = CRAFTITEM_COPY.invoke(null, item);
            NET_ITEMSTACK_NBT_IMPORT.invoke(next, compound);
            return (ItemStack) BUKKIT_COPY.invoke(null, next);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object extractNBT(ItemStack item) {
        try {
            Object next = CRAFTITEM_COPY.invoke(null, item);
            Object extract = NET_ITEMSTACK_NBT_EXTRACT.invoke(next);
            if (extract == null)
                return null;
            return extract;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void decompress(String target, Consumer<ItemStack> consumer) {
        BukkitSwitchHandler.getExecutor().execute(() -> consumer.accept(fromString(target, true)));
    }

    public static void decompress(String[] item, Consumer<ItemStack[]> consumer) {
        BukkitSwitchHandler.getExecutor().execute(() -> {
            ItemStack[] data = new ItemStack[item.length];
            for (int i = 0; i < item.length; i++)
                data[i] = fromString(item[i], true);
            consumer.accept(data);
        });
    }


    public static void decompress(List<String> item, Consumer<List<ItemStack>> consumer) {
        BukkitSwitchHandler.getExecutor().execute(() -> {
            List<ItemStack> data = new ArrayList<>();
            for (String s : item)
                data.add(fromString(s, true));
            consumer.accept(data);
        });
    }

    public static void compress(ItemStack item, Consumer<String> consumer) {
        BukkitSwitchHandler.getExecutor().execute(() -> {
            consumer.accept(toString(item, true));
        });
    }

    public static void compress(ItemStack[] item, Consumer<String[]> consumer) {
        BukkitSwitchHandler.getExecutor().execute(() -> {
            String[] data = new String[item.length];
            for (int i = 0; i < item.length; i++)
                data[i] = toString(item[i], true);
            consumer.accept(data);
        });
    }

    public static void compress(List<ItemStack> item, Consumer<List<String>> consumer) {
        BukkitSwitchHandler.getExecutor().execute(() -> {
            List<String> data = new ArrayList<>();
            for (ItemStack itemStack : item)
                data.add(toString(itemStack, true));
            consumer.accept(data);
        });
    }

    public static void compress(byte[] buf, Consumer<ByteBuf> consumer) {
        BukkitSwitchHandler.getExecutor().execute(() -> {
            ByteBuf buffer = Unpooled.wrappedBuffer(buf);
            byte[] arr = new byte[buffer.readableBytes()];
            buffer.readBytes(arr);
            arr = Zstd.compress(arr,0);
            buffer.release();
            buffer = Unpooled.wrappedBuffer(arr);
            consumer.accept(buffer);
            buffer.release();
        });
    }

    public static void decompress(byte[] buf, Consumer<ByteBuf> consumer) {
        BukkitSwitchHandler.getExecutor().execute(() -> {
            byte[] arr = Zstd.decompress(buf, (int) Zstd.decompressedSize(buf));
            ByteBuf buffer = Unpooled.wrappedBuffer(arr);
            consumer.accept(buffer);
            buffer.release();
        });
    }
}
