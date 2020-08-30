package skywolf46.bukkitswitchhandler.Thread;

import com.mysql.jdbc.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class TaskWaiterThread extends Thread {
    private List<Consumer<Connection>> consumers = new ArrayList<>();
    private final Object LOCK = new Object();
    private static final AtomicBoolean disable = new AtomicBoolean(false);
    private int maxAmount;
    private long wait;
    private long reset;
    private long lastReset = -1;
    private int processable = 0;

    public TaskWaiterThread(int amount, long waitDelay, long resetDelay) {
        this.maxAmount = this.processable = amount;
        this.wait = waitDelay;
        this.reset = resetDelay;
    }

    @Override
    public void run() {
        while (!disable.get()) {
            try {
                Thread.sleep(wait);
                if (System.currentTimeMillis() - lastReset > reset) {
                    lastReset = System.currentTimeMillis();
                    this.processable = maxAmount;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
