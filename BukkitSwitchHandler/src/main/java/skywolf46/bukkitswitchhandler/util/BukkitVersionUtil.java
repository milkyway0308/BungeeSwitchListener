package skywolf46.bukkitswitchhandler.util;

import org.bukkit.Bukkit;
import org.bukkit.Server;

public class BukkitVersionUtil {
    public static String getVersion(Server server) {
        final String packageName = server.getClass().getPackage().getName();

        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    public static Class getNMSClass(String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + getVersion(Bukkit.getServer()) + "." + className);
    }


    public static Class getOBCClass(String className) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + getVersion(Bukkit.getServer()) + "." + className);
    }
}