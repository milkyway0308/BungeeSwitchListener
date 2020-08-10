package skywolf46.bukkitswitchhandler.Thread;

import skywolf46.bukkitswitchhandler.BukkitSwitchHandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class SQLConsumerThread extends Thread {
    private final List<Consumer<Connection>> writers = new ArrayList<>();
    private final Object LOCK = new Object();
    private static final AtomicBoolean disabled = new AtomicBoolean(false);

    public SQLConsumerThread() {
        setDaemon(false);
    }

    public void append(Consumer<Connection> c) {
        synchronized (LOCK) {
            if (disabled.get())
                c.accept(BukkitSwitchHandler.getSQL());
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
                lc.accept(BukkitSwitchHandler.getSQL());
            });
            lcs.clear();
            try {
                Thread.sleep(3L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        synchronized (LOCK) {
            for (Consumer<Connection> lc : writers)
                lc.accept(BukkitSwitchHandler.getSQL());
        }
    }

    public void stopThread() {
        disabled.set(true);
    }
}
