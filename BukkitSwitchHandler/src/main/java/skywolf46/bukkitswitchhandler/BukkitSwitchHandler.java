package skywolf46.bukkitswitchhandler;

import io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import skywolf46.bukkitswitchhandler.Thread.SQLConsumerThread;
import skywolf46.bukkitswitchhandler.listener.BroadcastListener;
import skywolf46.bukkitswitchhandler.listener.DataLoadListener;
import skywolf46.bukkitswitchhandler.listener.EventListener;
import skywolf46.bukkitswitchhandler.util.InfiniReadingSocket;
import skywolf46.bukkitswitchhandler.util.Request;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
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


    private static SQLConsumerThread thd;

    private static final AtomicLong RELOAD_TIMESTAMP = new AtomicLong(0);

    private static final HashMap<Long, Consumer<List<DataInput>>> WAITING_TIMESTAMP = new HashMap<>();

    private static final Object LOCK = new Object();

    @Override

    public void onEnable() {
        // Plugin startup logic
        inst = this;
        (thd = new SQLConsumerThread()).start();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        EventListener el = new EventListener();
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "MC|BungeeSwitcher", el);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "MC|InitialLoad", new DataLoadListener());
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "MC|BSLBroadcast", new BroadcastListener());
//        saveCompleteRequest("Tester", UUID.randomUUID());

        File fl = new File(getDataFolder(), "config.yml");
        if (!fl.exists()) {
            fl.getParentFile().mkdirs();
            saveResource("config.yml", true);
        }
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(fl);
        String url = conf.getString("SQL.Address");
        try {
            SQL = DriverManager.getConnection(url, conf.getString("SQL.User"), conf.getString("SQL.Password"));
            PreparedStatement stmt = BukkitSwitchHandler.getSQL().prepareStatement("show databases like 'BungeecordBridge'");
            ResultSet rs = stmt.executeQuery();
            if (!rs.next())
                BukkitSwitchHandler.getSQL().prepareStatement("create database BungeecordBridge CHARACTER SET utf8 COLLATE utf8_general_ci;").execute();
            BukkitSwitchHandler.getSQL().prepareStatement("use BungeecordBridge").execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        retry();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        thd.stopThread();
    }

    public static SQLConsumerThread getAsyncThread() {
        return thd;
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

    public static void reload(String name, UUID user, DataInput bios) {
        if (rellistener.containsKey(name))
            rellistener.get(name).accept(user, bios);
        else
            throw new IllegalStateException("Listener for \"" + name + "\" is not registered");
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
//
//    public static void broadcastRequest(String task, UUID player, Consumer<ByteBuf> buffer, Consumer<List<DataInput>> inputs) {
//        synchronized (BukkitSwitchHandler.class) {
//            if (socket == null) {
//                reloadReady.computeIfAbsent(task, a -> new ArrayList<>()).add(player);
//                return;
//            }
//        }
//        long timestamp = RELOAD_TIMESTAMP.incrementAndGet();
//        synchronized (LOCK) {
//            WAITING_TIMESTAMP.put(timestamp, inputs);
//        }
//        Request.broadcast(task, player, buffer);
//    }

    public static InfiniReadingSocket getSocket() {
        return socket;
    }

    public static void initialize(InfiniReadingSocket soc) {
//        System.out.println("Init.");
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
                    if (waiter.failed()) {
                        Bukkit.getConsoleSender().sendMessage("§6BukkitSwitchHandler §7| §cBungee connection failed. Retry after 1 seconds...");
                        waiter = null;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Thread.sleep(100);
                            continue;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    waiter = new InfiniReadingSocket();
                } catch (Exception ex) {
//                        socket = null;
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
