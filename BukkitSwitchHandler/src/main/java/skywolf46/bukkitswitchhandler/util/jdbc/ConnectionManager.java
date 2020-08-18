package skywolf46.bukkitswitchhandler.util.jdbc;

import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;

import java.sql.Connection;

public class ConnectionManager {


    public static Connection getConnection() {
        return BukkitSwitchHandler.getSQL();
    }
}
