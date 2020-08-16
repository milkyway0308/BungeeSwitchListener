### BungeeSwitchListener
Best synchronization system for your SQL system

#### What is this?
BungeeSwitchListener is cross-server synchronization for SQL system.

#### How it works?
BungeeSwitchListener injecting handler to Bungeecord to receive custom packets.
If connection is localhost, and first 8 byte is equals to identify variable, connection will establish.

#### What do I need for this plugin?
You will need <b>Java 8</b>.<br>
Cause Java 9 not supported private reflection, It will need patch.<br>
I'll upload Java 9 bungeecord patch later.<br>

You need to add BungeeSwitchListener to your bungeecord, and BukkitSwitchHandler to bukkit.<br>

BungeeSwitchListener is bungee based repeater, and you need to add reciever to bukkit.<br>

#### How I can use it?
Add BukkitSwitchListener to plugin dependency.<br><br>

When server startup, use BukkitSwitchHandler#register(Consumer<UUID,DataInput> load,Consumer<UUID,DataInput> reload) to register reciever.<br><br>

When player quit, save to SQL.<br><br>

After save complete, use BukkitSwitchHandler#saveCompleteRequest(String task, UUID uid)<br><br>

<b>** Warning**</b> On player join, plugin will load with null parameter.

#### Is there an example?
Sure. Look next line.<br><br>

#### Deerializer / Reloader register
```java
public class TestPlugin extends JavaPlugin implements Listener {
    private static HashMap<String, String> name = new HashMap<>();
    private static final Object LOCK = new Object();
    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("BukkitSwitchHandler") != null){
            Bukkit.getPluginManager().registerEvents(this, this);
        }
    }
    
    @EventHandler
    public void ev(PlayerInitialLoadEvent e) {
        // Call when player join to bungeecord proxy.
        load(e.getUid());
    }
    
    @EventHandler
    public void ev(PlayerSaveEvent e){
        
    }
    
    private void load(UUID player){
        try (PreparedStatement stmt = BukkitSwitchHandler.getSQL().prepareStatement("select * from testTable where uid = ?")){
            stmt.setString(1, player.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                synchronized (LOCK){
                    name.put(player.toString(), rs.getString(1));
                }
            }
            Bukkit.getScheduler().scheduleDelayedTask(this, ()->{
                Player p = Bukkit.getPlayer(player);
                if(p != null)
                    p.setDisplayName(name);
            });
        } catch (Exception ignored){
            
        }
    }
    
    @EventHandler
    public void ev(PlayerQuitEvent e){
        try (PreparedStatement stmt = BukkitSwitchHandler.getSQL().prepareStatement("insert into testTable values(?, ?)")){
            // Save.
            
            stmt.setString(1, e.getPlayer().getUniqueID().toString());
            synchronized (LOCK){
                stmt.setString(2, name.getOrDefault(e.getPlayer().getUniqueID().toString(), e.getPlayer().getName()));
            }
            stmt.executeUpdate();
            BukkitSwitchHandler.saveCompleteRequest("TestPlugin", e.getPlayer().getUniqueId());
        } catch (Exception ex){
            
        }
    }
}
```