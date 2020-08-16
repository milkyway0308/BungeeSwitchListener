package skywolf46.bungeeswitchlistener.thread;

import java.util.ArrayList;
import java.util.List;

public class SafeSendThread extends Thread {
    private List<Runnable> lists = new ArrayList<>();
    private final Object LOCK = new Object();

    @Override
    public void run() {
        super.run();
    }
}
