package skywolf46.bukkitswitchhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import javafx.fxml.FXMLLoader;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import skywolf46.bukkitswitchhandler.listener.DataLoadListener;
import skywolf46.bukkitswitchhandler.listener.EventListener;
import skywolf46.bukkitswitchhandler.util.InfiniReadingSocket;
import skywolf46.bukkitswitchhandler.util.PlayerReloadRequest;
import skywolf46.bukkitswitchhandler.util.Request;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class BukkitSwitchHandler extends JavaPlugin {

    private static BukkitSwitchHandler inst;

    private static Connection SQL;

    private static HashMap<String, BiConsumer<UUID, DataInput>> listener = new HashMap<>();

    private static InfiniReadingSocket socket;

    private static HashMap<String, List<UUID>> sendReady = new HashMap<>();

    private static HashMap<String, List<UUID>> reloadReady = new HashMap<>();
    private static HashMap<String, BiConsumer<UUID, DataInput>> rellistener = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        inst = this;
        register("Test", (user, stream) -> {
            try {
                System.out.println(stream.readUTF());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, (user, stream) -> {
            try {
                System.out.println(stream.readUTF());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        EventListener el = new EventListener();
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "MC|BungeeSwitcher", el);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "MC|InitialLoad", new DataLoadListener());
        saveCompleteRequest("Tester", UUID.randomUUID());

        File fl = new File(getDataFolder(), "config.yml");
        if (!fl.exists()) {
            fl.getParentFile().mkdirs();
            saveResource("config.yml", true);
//            return;
        }
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(fl);
        String url = conf.getString("SQL.Address");
        try {
            SQL = DriverManager.getConnection(url, conf.getString("SQL.User"), conf.getString("SQL.Password"));
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        retry();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static BukkitSwitchHandler inst() {
        return inst;
    }

    public static void load(String name, UUID user, DataInput bios) {
        if (listener.containsKey(name))
            listener.get(name).accept(user, bios);
        else
            throw new IllegalStateException("Listener for \"" + name + "\" is not registered");
    }

    public static void register(String name, BiConsumer<UUID, DataInput> load, BiConsumer<UUID, DataInput> reload) {
        listener.put(name, load);
        rellistener.put(name, reload);
    }

    public static void saveCompleteRequest(String task, UUID player) {
        synchronized (BukkitSwitchHandler.class) {
            if (socket == null) {
                sendReady.computeIfAbsent(task, a -> new ArrayList<>()).add(player);
                return;
            }
        }
        Request.saveComplete(task, player);
    }

    public static void saveCompleteRequest(String task, UUID player, Consumer<ByteBuf> buf) {
        synchronized (BukkitSwitchHandler.class) {
            if (socket == null) {
                sendReady.computeIfAbsent(task, a -> new ArrayList<>()).add(player);
                return;
            }
        }
        Request.saveComplete(task, player, buf);
    }

    public static void reloadRequest(String task, UUID player) {
        synchronized (BukkitSwitchHandler.class) {
            if (socket == null) {
                reloadReady.computeIfAbsent(task, a -> new ArrayList<>()).add(player);
                return;
            }
        }
        Request.reload(task, player);
    }

    public static void reloadRequest(String task, UUID player, Consumer<ByteBuf> buffer) {
        synchronized (BukkitSwitchHandler.class) {
            if (socket == null) {
                reloadReady.computeIfAbsent(task, a -> new ArrayList<>()).add(player);
                return;
            }
        }
        Request.reload(task, player, buffer);
    }

    public static InfiniReadingSocket getSocket() {
        return socket;
    }

    public static void initialize(InfiniReadingSocket soc) {
        System.out.println("Init.");
        socket = soc;
        synchronized (BukkitSwitchHandler.class) {
            for (String uid : sendReady.keySet()) {
                for (UUID x : sendReady.get(uid)) {
                    saveCompleteRequest(uid, x);
                }
            }

            for (String uid : reloadReady.keySet()) {
                for (UUID x : reloadReady.get(uid)) {
                    reloadRequest(uid, x);
                }
            }
            sendReady.clear();
            reloadReady.clear();
        }
    }

    public static void retry() {
        socket = null;
        new Thread(() -> {
            InfiniReadingSocket waiter = null;
            while (socket == null) {
                if (waiter != null) {
                    try {
                        Thread.sleep(100);
                        continue;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    waiter = new InfiniReadingSocket();
                } catch (Exception ex) {
//                        socket = null;
                    Bukkit.getConsoleSender().sendMessage("§cBukkitTrojan §7| §cBungee connection failed. Trying after 4 seconds...");
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void load(UUID uniqueId) {
        for (BiConsumer<UUID, DataInput> bi : listener.values())
            bi.accept(uniqueId, null);
    }


    public static Connection getSQL() {
        return SQL;
    }
}
