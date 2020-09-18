package skywolf46.bukkitswitchhandler.util.sqlpool;

import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;
import skywolf46.bukkitswitchhandler.Thread.SQLConsumerThread;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SQLThreadPool {
    private List<SQLConsumerThread> threads = new ArrayList<>();
    private List<Consumer<Connection>> cons = new ArrayList<>();
    private final Object LOCK = new Object();

    public SQLThreadPool(int initial) throws SQLException {
        for (int i = 0; i < initial; i++) {
            threads.add(BukkitSwitchHandler.createIndividualThread());
        }
    }

    public void close() {
        for (SQLConsumerThread scon : threads)
            scon.stopThread();
    }

    public void addTask(Consumer<Connection> cons) {
        synchronized (LOCK) {
            if (threads.size() > 0) {
                SQLConsumerThread t = threads.remove(0);
                t.append(con -> {
                    cons.accept(con);
                    retunTask(t);
                });
            } else {
                this.cons.add(cons);
            }
        }
    }

    private void retunTask(SQLConsumerThread thd) {
        synchronized (LOCK) {
            if (cons.size() > 0) {

            }
        }
    }
}
