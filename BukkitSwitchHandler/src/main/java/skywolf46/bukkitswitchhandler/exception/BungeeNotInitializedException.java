package skywolf46.bukkitswitchhandler.exception;

public class BungeeNotInitializedException extends RuntimeException {
    public BungeeNotInitializedException() {
        super("Bungeecord connection closed or not connected");
    }
}
