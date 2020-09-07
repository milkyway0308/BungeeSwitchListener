package skywolf46.bukkitswitchhandler.util.priorityloader;

import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.Connection;

public interface IPlayerDataLoader {
    void load(Player p, Connection con) throws IOException;
}
