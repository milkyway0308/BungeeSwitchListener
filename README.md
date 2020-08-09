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
public class TestPlugin extends JavaPlugin implements Listener{
    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("BukkitSwitchHandler") != null){
            try{
                PreparedStatement stmt = BukkitSwitchHandler.getSQL().prepareStatement("create table if not exists testTable(user varchar(50), dataName varchar(20),data varchar(200), primary key (user)) CHARACTER SET utf8 COLLATE utf8_general_ci;");
                stmt.executeUpdate();
            }catch (Exception ex){
                ex.printStackTrace();
            }
           BukkitSwitchHandler.register("TestPlugin", (uid,di) -> {
               // You can use DataInput for read external data
               try {
                   PreparedStatement stmt = BukkitSwitchHandler.getSQL().prepareStatement("select * from EssQL where user = ?");
                   stmt.setString(1, uid.toString());
                   ResultSet rs = stmt.executeQuery();
                   if(rs.next()) {
                       // Print "data" column
                       System.out.println(rs.getString(3));
                   } else {
                       System.out.println("Data not exist: " + uid.toString());  
                   }
               } catch (Exception ex){
                   
               }
           }, (uid, di) -> {
               // Reload request here.
           });
        }
    }
    
    @EventHandler
    public void ev(PlayerQuitEvent e){
        try {
            // Save.
            PreparedStatement stmt = BukkitSwitchHandler.getSQL().prepareStatement("insert into testTable values(?, ?, ?)");
            stmt.setString(1, e.getPlayer().getUniqueID().toString());
            stmt.setString(2, "Test Data");
            stmt.setString(3, "Hello, World");
            stmt.executeUpdate();
            BukkitSwitchHandler.saveCompleteRequest("TestPlugin", e.getPlayer().getUniqueId());
        } catch (Exception ex){
            
        }
    }
}
```