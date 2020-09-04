package skywolf46.bukkitswitchhandler;

import io.netty.buffer.PooledByteBufAllocator;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import skywolf46.bukkitswitchhandler.Thread.SQLConsumerThread;
import skywolf46.bukkitswitchhandler.listener.PlayerListener;
import skywolf46.bukkitswitchhandler.util.BungeePacketProxy;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class BukkitSwitchHandler extends JavaPlugin {

    private static List<String> registeredProvider = new ArrayList<>();

    private static BukkitSwitchHandler inst;

    private static Connection SQL;

    private static SQLConsumerThread packetThread;

    private static BungeePacketProxy proxy;

    private static List<SQLConsumerThread> sqlThreads = new ArrayList<>();

    private static PooledByteBufAllocator alloc = new PooledByteBufAllocator(false);

    private static ExecutorService asyncExecutor = Executors.newCachedThreadPool();

    private static final Random r = new Random();

    private static String ip = "localhost";

    private static int port = 25577;


    @Override
    public void onEnable() {
        // Plugin startup logic
        inst = this;
        (packetThread = new SQLConsumerThread(null)).start();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        sendLoadRequest("Tester", UUID.randomUUID());

        File fl = new File(getDataFolder(), "config.yml");
        if (!fl.exists()) {
            fl.getParentFile().mkdirs();
            saveResource("config.yml", true);
        }
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(fl);
        String url = conf.getString("SQL.Address");
        port = conf.getInt("Bungeecord.Port", port);
        ip = conf.getString("Bungeecord.IP", ip);
        int sqlThreadAmount = Math.max(1, conf.getInt("SQL.SQL Connection Threads", 1));
        try {
            Properties p = new Properties();
            p.setProperty("autoReconnect", "true");
            p.setProperty("user", conf.getString("SQL.User"));
            p.setProperty("password", conf.getString("SQL.Password"));
            SQL = DriverManager.getConnection(url, p);
            PreparedStatement stmt = BukkitSwitchHandler.getSQL().prepareStatement("show databases like 'BungeecordBridge'");
            ResultSet rs = stmt.executeQuery();
            if (!rs.next())
                BukkitSwitchHandler.getSQL().prepareStatement("create database BungeecordBridge CHARACTER SET utf8 COLLATE utf8_general_ci;").execute();
            try (PreparedStatement xt = BukkitSwitchHandler.getSQL().prepareStatement("use BungeecordBridge")) {
                xt.executeUpdate();
            }
            for (int i = 0; i < sqlThreadAmount; i++) {
                Connection con = DriverManager.getConnection(url, conf.getString("SQL.User"), conf.getString("SQL.Password"));
                try (PreparedStatement xt = con.prepareStatement("use BungeecordBridge")) {
                    xt.executeUpdate();
                }
                SQLConsumerThread thd = new SQLConsumerThread(con);
                thd.start();
                sqlThreads.add(thd);
            }
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                try (PreparedStatement st = getSQL().prepareStatement("select 1 = 1")) {
                    st.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                for (SQLConsumerThread thd : sqlThreads) {
                    thd.append(con -> {
                        try (PreparedStatement st = con.prepareStatement("select 1 = 1")) {
                            st.executeQuery();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }, 72000L, 72000L);
        } catch (SQLException e) {
//            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("§6BungeeSwitchHandler §7| §cFailed to connect on MySQL server. If you need to use BukkitSwitchHandler, restart server.");
            return;
        }
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        proxy = new BungeePacketProxy(port);
        startBungeeConnection();
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (SQL != null) {
            try {
                SQL.close();
            } catch (SQLException ignored) {

            }
        }
        for (SQLConsumerThread t : sqlThreads) {
            t.stopThread();
        }
        packetThread.stopThread();
        asyncExecutor.shutdown();
    }

    public static List<String> getRegisteredProvider() {
        return registeredProvider;
    }

    public static void registerProvider(String prov) {
        registeredProvider.add(prov);
    }

    public static String getBungeecordIP() {
        return ip;
    }

    public static int getBungeecordPort() {
        return port;
    }

    public static SQLConsumerThread getPacketThread() {
        return packetThread;
    }

    public static SQLConsumerThread getRandomSQLThread() {
        return sqlThreads.get(r.nextInt(sqlThreads.size()));
    }

    public static List<SQLConsumerThread> getThreads() {
        return new ArrayList<>(sqlThreads);
    }

    public static BukkitSwitchHandler inst() {
        return inst;
    }


    public static void startBungeeConnection() {
        new Thread(() -> {
            proxy.reconnect();
            while (proxy.isConnectionFailed()) {
                Bukkit.getConsoleSender().sendMessage("§6BungeeSwitchHandler §7| §4Bungee connect failed! §cRetry after 1 second.");
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                proxy.reconnect();
            }

            Bukkit.getConsoleSender().sendMessage("§6BungeeSwitchHandler §7| §aBungee connection established.");
        }).start();
    }


    public static Connection getSQL() {
        return SQL;
    }

    public static PooledByteBufAllocator getAllocator() {
        return alloc;
    }

    public static BungeePacketProxy getProxy() {
        return proxy;
    }

    public static Executor getExecutor() {
        return asyncExecutor;
    }
}
