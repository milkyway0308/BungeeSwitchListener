package skywolf46.bukkitswitchhandler.Thread;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class SQLConsumerThread extends Thread {
    private final List<Consumer<Connection>> writers = new ArrayList<>();
    private final Object LOCK = new Object();
    private static final AtomicBoolean disabled = new AtomicBoolean(false);
    private Connection con;
    private long delay = 100;

    public SQLConsumerThread(Connection con) {
        setDaemon(false);
        this.con = con;
    }

    public void append(Consumer<Connection> c) {
        synchronized (LOCK) {
            if (disabled.get())
                c.accept(this.con);
            else
                writers.add(c);
        }
    }

    @Override
    public void run() {
        while (!disabled.get()) {
            List<Consumer<Connection>> lcs;
            synchronized (LOCK) {
                lcs = new ArrayList<>(writers);
                writers.clear();
            }
            lcs.forEach(lc -> {
                try {
                    lc.accept(this.con);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            lcs.clear();
            try {
                Thread.sleep(3L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (delay-- > 0) {
            synchronized (LOCK) {
                if (writers.size() > 0)
                    delay += 10;
                for (Consumer<Connection> lc : writers)
                    lc.accept(this.con);
                writers.clear();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            if (this.con != null)
                this.con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void stopThread() {
        disabled.set(true);
    }
}
