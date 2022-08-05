package net.emeraldprison.epcore.database.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.emeraldprison.epcore.EPCore;
import net.emeraldprison.epcore.database.StorageType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class HikariSQLService extends SQLService {

    private final JavaPlugin owningPlugin;

    public final String DATABASE_NAME;
    private final String USERNAME;
    private final String PASSWORD;
    private final StorageType TYPE;
    private Integer PORT;
    private String HOST;

    private HikariDataSource dataSourceOne, dataSourceTwo;

    public HikariSQLService(JavaPlugin owningPlugin, String host, String databaseName, int port,
                            String username, String password, StorageType type) {
        this.owningPlugin = owningPlugin;
        HOST = host;
        DATABASE_NAME = databaseName;
        PORT = port;
        USERNAME = username;
        PASSWORD = password;
        TYPE = type;
    }

    public HikariSQLService(JavaPlugin owningPlugin, String databaseName, String username,
                            String password, StorageType type) {
        this.owningPlugin = owningPlugin;
        DATABASE_NAME = databaseName;
        USERNAME = username;
        PASSWORD = password;
        TYPE = type;
    }

    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public HikariDataSource getDataSource() {
        if (dataSourceOne == null)
            generateNewDataSource(true);

        if (dataSourceTwo == null)
            generateNewDataSource(false);

        // TODO: Return the one with no connections.
        return dataSourceOne;
    }

    private void generateNewDataSource(boolean sourceOne) {
        HikariConfig hikariConfig = createConfig();

        if (sourceOne) {
            dataSourceOne = new HikariDataSource(hikariConfig);
        } else {
            dataSourceTwo = new HikariDataSource(hikariConfig);
        }
    }

    @Override
    public HikariConfig createConfig() {
        if (TYPE.equals(StorageType.SQLITE)) {
            // Create File
            try {
                File file = new File(owningPlugin.getDataFolder(), cleanDatabaseName(DATABASE_NAME));
                if (!file.exists() || !file.isFile()) {
                    file.createNewFile();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        String jdbcUrl = TYPE.equals(StorageType.MYSQL)
                ? "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE_NAME
                : "jdbc:sqlite:" + owningPlugin.getDataFolder() + "/" + cleanDatabaseName(DATABASE_NAME);
        HikariConfig config = new HikariConfig();

        // Settings
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(25);
        config.setMinimumIdle(0);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(60000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return config;
    }

    public String cleanDatabaseName(@NotNull String databaseName) {
        if (databaseName.endsWith(".db")) {
            return databaseName;
        }

        return databaseName + ".db";
    }

    @Override
    public String getDatabaseName() {
        return DATABASE_NAME;
    }
}
