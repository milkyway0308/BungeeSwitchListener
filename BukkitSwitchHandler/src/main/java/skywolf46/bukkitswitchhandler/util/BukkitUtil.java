package skywolf46.bukkitswitchhandler.util;

import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;

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
        try {
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
            String data = Base64.getEncoder().encodeToString(baos.toByteArray());
            baos.close();
            return data;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ItemStack fromString(String str) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(str));
            DataInputStream dis = new DataInputStream(bais);
            Material mat = Material.getMaterial(dis.readUTF());
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

}
