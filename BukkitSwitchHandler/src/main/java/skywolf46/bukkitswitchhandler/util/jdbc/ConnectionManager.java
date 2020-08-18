package skywolf46.bukkitswitchhandler.util.jdbc;

import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager {


    public static Connection getConnection() {
        return BukkitSwitchHandler.getSQL();
    }
}
