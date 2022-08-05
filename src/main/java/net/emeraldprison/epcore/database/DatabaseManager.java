package net.emeraldprison.epcore.database;

import lombok.Getter;
import net.emeraldprison.epcore.EPCore;
import net.emeraldprison.epcore.database.service.HikariSQLService;
import net.emeraldprison.epcore.database.service.SQLService;
import net.emeraldprison.epcore.database.utilities.SQLDataType;
import net.emeraldprison.epcore.database.utilities.SQLDefaultType;
import net.emeraldprison.epcore.database.utilities.TableBuilder;
import net.emeraldprison.epcore.utilities.logging.LogLevel;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DatabaseManager {

    private final JavaPlugin owningPlugin;

    @Getter
    private SQLService sqlService;

    public DatabaseManager(@NotNull JavaPlugin owningPlugin) {
        this.owningPlugin = owningPlugin;
    }

    @SuppressWarnings("ConstantConditions") // will suppress the "bla bla bla may be null" warnings because we use a try catch
    public boolean setup() {
        File sqlFile = new File(owningPlugin.getDataFolder(), "sql.yml");

        if (!sqlFile.exists() || !sqlFile.isFile()) {
            EPCore.getPlugin().getCoreLogger().log(LogLevel.WARN, "Could not identify valid SQL file (" + sqlFile.getAbsolutePath() + ") for plugin " + owningPlugin.getName() + ". Invalid file?");
            return false;
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(sqlFile);
        StorageType storageType = StorageType.parse(configuration.getString("type"));

        if (storageType == null) {
            EPCore.getPlugin().getCoreLogger().log(LogLevel.WARN, "Could not identify Storage Type in file (" + sqlFile.getAbsolutePath() + "). Invalid file?");
            return false;
        }

        try {
            this.sqlService = switch (storageType) {
                case SQLITE -> {
                    String fileName = configuration.getString("settings.sqlite.file-name")
                            .replace("%PLUGIN%", owningPlugin.getName());
                    String username = configuration.getString("settings.sqlite.username");
                    String password = configuration.getString("settings.sqlite.password");
                    yield new HikariSQLService(owningPlugin, fileName, username, password, storageType);
                }

                case MYSQL -> {
                    String hostName = configuration.getString("settings.mysql.hostname");
                    String databaseName = configuration.getString("settings.mysql.database")
                            .replace("%PLUGIN%", owningPlugin.getName());
                    String username = configuration.getString("settings.mysql.username");
                    String password = configuration.getString("settings.mysql.password");
                    int port = configuration.getInt("settings.mysql.port");
                    yield new HikariSQLService(owningPlugin, hostName, databaseName, port, username, password, storageType);
                }
            };
        } catch (NullPointerException exception) {
            EPCore.getPlugin().getCoreLogger().log(LogLevel.WARN, "Could not identify value in file (" + sqlFile.getAbsolutePath() + ") for plugin " + owningPlugin.getName() + ". Invalid file?");
            exception.printStackTrace();
            return false;
        }

        return true;
    }

    public void loadDefaultTables() {
        // Generate Default Tables
        // This should not be executed by any other plugins than the core.
        TableBuilder.newTable("users", this)
                .addColumn("uuid", SQLDataType.VARCHAR, 32, false, SQLDefaultType.NO_DEFAULT, true)
                .addColumn("name", SQLDataType.VARCHAR, 100, false, SQLDefaultType.NO_DEFAULT, false)
                .build();

        TableBuilder.newTable("settings", this)
                .addColumn("uuid", SQLDataType.VARCHAR, 32, false, SQLDefaultType.NO_DEFAULT, true)
                .addColumn("setting", SQLDataType.VARCHAR, 100, false, SQLDefaultType.NO_DEFAULT, false)
                .addColumn("value", SQLDataType.VARCHAR, 100, true, SQLDefaultType.NO_DEFAULT, false)
                .build();
    }

    public boolean update(@NotNull String table, @NotNull Map<String, Object> data, @NotNull Map<String, Object> whereData) {
        try {
            StringBuilder query = new StringBuilder("UPDATE ep_" + table + " SET ");
            HashMap<Integer, Object> indexed = new HashMap<>();

            data.remove("uuid");
            data.remove("id");

            int currentIndex = 1;
            for (Map.Entry<String, Object> dataEntry : data.entrySet()) {
                if (currentIndex > 1) {
                    query.append(", ");
                }

                query.append("`").append(dataEntry.getKey()).append("`").append("=?");
                indexed.put(currentIndex, dataEntry.getValue());

                currentIndex++;
            }

            query.append(" WHERE ");

            List<Map.Entry<String, Object>> entries = new ArrayList<>(whereData.entrySet());
            for (int index = 0; index < entries.size(); index++) {
                Map.Entry<String, Object> entry = entries.get(index);
                if (index > 0) {
                    query.append(" AND ");
                }

                query.append("`").append(entry.getKey()).append("`").append("=?");
                indexed.put(index, entry.getValue());

                index++;
            }

            try (Connection connection = sqlService.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(query.toString());

                for (int index : indexed.keySet()) {
                    Object indexedValue = indexed.get(index);

                    if (indexedValue instanceof InputStream) {
                        statement.setBinaryStream(index, (InputStream) indexedValue);
                        continue;
                    }

                    statement.setObject(index, indexedValue);
                }

                statement.executeUpdate();
                statement.close();
            }

            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public ResultSet getResults(@NotNull String table, @NotNull String where, @NotNull Map<Integer, Object> data) throws SQLException {
        return getResults("ep", table, where, data);
    }

    public ResultSet getResults(@Nullable String tablePrefix, @NotNull String table, @NotNull String where, @NotNull Map<Integer, Object> data) throws SQLException {
        StringBuilder query = new StringBuilder(
                "SELECT * FROM " + (tablePrefix == null ? "" : tablePrefix + "_") + table + " WHERE " + where
        );

        try (Connection connection = sqlService.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    query.toString()
            );

            for (int dataInteger : data.keySet()) {
                Object object = data.get(dataInteger);

                if (object instanceof InputStream) {
                    statement.setBinaryStream(dataInteger, (InputStream) object);
                    continue;
                }
                statement.setObject(dataInteger, object);
            }

            ResultSet resultSet = statement.executeQuery();
            RowSetFactory factory = RowSetProvider.newFactory();
            CachedRowSet cachedRowSet = factory.createCachedRowSet();
            cachedRowSet.populate(resultSet);

            statement.close();
            resultSet.close();
            connection.close();

            return cachedRowSet;
        }
    }

    public ResultSet executeQuery(@NotNull String sqlQuery) {
        try (Connection connection = sqlService.getConnection()) {
            ResultSet resultSet = connection.prepareStatement(sqlQuery).executeQuery();

            RowSetFactory factory = RowSetProvider.newFactory();
            CachedRowSet cachedRowSet = factory.createCachedRowSet();
            cachedRowSet.populate(resultSet);

            resultSet.close();
            connection.close();

            return cachedRowSet;
        } catch (SQLException exception) {
            EPCore.getPlugin().getCoreLogger().log(LogLevel.WARN, "Error executing SQL query with following message: " + exception.getMessage());
            return null;
        }
    }

    public void execute(@NotNull String sqlQuery) {
        try (Connection connection = sqlService.getConnection()) {
            connection.prepareStatement(sqlQuery).execute();
        } catch (SQLException exception) {
            EPCore.getPlugin().getCoreLogger().log(LogLevel.WARN, "Error executing SQL statement with following message: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    public void executeUpdate(@NotNull String sqlQuery) {
        try (Connection connection = sqlService.getConnection()) {
            connection.prepareStatement(sqlQuery).executeUpdate();
        } catch (SQLException exception) {
            EPCore.getPlugin().getCoreLogger().log(LogLevel.WARN, "Error executing SQL update statement with following message: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    public boolean insert(String table, Map<String, Object> data){
        return execute("INSERT INTO", table, data, true);
    }

    public boolean remove(String table, Map<String, Object> whereData) {
        HashMap<Integer, Object> indexed = new HashMap<>();
        try {
            StringBuilder query = new StringBuilder("DELETE FROM xfun_"+table+" WHERE ");
            final int[] a = {1};

            List<Map.Entry<String, Object>> entries = new ArrayList<>(whereData.entrySet());
            for (int index = 0; index < entries.size(); index++) {
                Map.Entry<String, Object> entry = entries.get(index);
                if (index > 0) {
                    query.append(" AND ");
                }

                query.append("`").append(entry.getKey()).append("`").append("=?");
                indexed.put(index, entry.getValue());

                index++;
            }

            EPCore.getPlugin().getCoreLogger().log(LogLevel.DEBUG,"Making a remove query as follows: " + query);
            try(Connection connection = sqlService.getConnection()){
                PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

                for(Integer index : indexed.keySet()){
                    Object value = indexed.get(index);

                    if (value instanceof InputStream){
                        preparedStatement.setBinaryStream(index, (InputStream) value);
                        continue;
                    }
                    preparedStatement.setObject(index, value);
                }

                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
            return true;
        }catch (SQLException exception) {
            EPCore.getPlugin().getCoreLogger().log(LogLevel.WARN, "Error executing SQL update statement with following message: " + exception.getMessage());
            exception.printStackTrace();
            return false;
        }
    }

    public boolean execute(@NotNull String prefix, @NotNull String tableName, @NotNull Map<String, Object> data, boolean insert) {
        try {
            HashMap<Integer, Object> indexedValues = new HashMap<>();
            StringBuilder sqlQuery = new StringBuilder(prefix + " ep_" + tableName + " ("),
                    values = new StringBuilder(") VALUES(");

            List<String> dataKeys = new ArrayList<>(data.keySet());
            for (int keyIndex = 1; keyIndex < data.keySet().size() + 1; keyIndex++) {
                if (keyIndex > 1) {
                    sqlQuery.append(", ");
                    values.append(", ");
                }

                sqlQuery.append("`").append(dataKeys.get(keyIndex)).append("`");
                values.append("?");

                indexedValues.put(keyIndex, data.get(dataKeys.get(keyIndex)));
            }

            values.append(")");
            sqlQuery.append(values);

            try (Connection connection = sqlService.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(sqlQuery.toString());

                for (int index : indexedValues.keySet()) {
                    Object value = indexedValues.get(index);

                    if (value instanceof InputStream) {
                        statement.setBinaryStream(index, (InputStream) value);
                        continue;
                    }

                    statement.setObject(index, value);
                }

                statement.executeUpdate();
                statement.close();
                return true;
            }
        } catch (SQLException exception) {
            EPCore.getPlugin().getCoreLogger().log(LogLevel.WARN, "Error executing SQL statement with following message: " + exception.getMessage());
            exception.printStackTrace();
            return false;
        }
    }
}
