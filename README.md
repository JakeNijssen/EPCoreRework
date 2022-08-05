# EPCore - Reworked

## Developer Information

EPCore (Rework) is a core library that helps Developers efficiently and faster write their code for plugins to the prison server EmeraldPrison. This core will be continuously updated in order to keep up with the latest features and demand by players on the server.

## Command System
EPCore gives the Developer the ability to create commands in an easier and more straight forward way than previous systems. Here is an example that shows how to teleport a player:

```java
public class TeleportCommand extends Command {
    
}
```

## Database System
EPCore has an advanced Database system that allows Developers to easily execute SQL statements, without having to edit your code because you missed a comma or something. Here is an example for creating a table with EPCore's SQL system. In order to execute database queries to your plugins own database, you will need to initialize the Database Manager in your plugin, along with an `SQL.yml` file.

#### Setup

⋅⋅⋅You will need to create an `sql.yml` file in your plugins data folder, and then the  Database Manager should automatically be able to read it when initializing it.
```yaml
# Two options here, either MySQL or SQLite (not case-sensitive)
type: "MySQL"

settings:
  sqlite:
    # The file created for the database. Adding the .db in the end is optional and doesn't really matter.
    file-name: "database"
    username: "username"
    password: "password"
  mysql:
    # The database name for this plugin.
    database: "database"
    hostname: "127.0.0.1"
    username: "username"
    password: "password"
    port: 3306
```

After creating the file, you can initialize the Database Manager like this:

```java
public class Main extends JavaPlugin {
    
    private final DatabaseManager databaseManager = new DatabaseManager(this); // this = your plugin main class
    
    @Override
    public void onEnable() {
        if (!databaseManager.setup()) {
            // We couldn't set up the Database Manager for this plugin.
            getLogger().log(LogLevel.FATAL, "Database Manager failed to set up for " + getPlugin().getName() + ", disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Now your Database Manager has been set up, and you can start executing queries using it.
    }
}
```