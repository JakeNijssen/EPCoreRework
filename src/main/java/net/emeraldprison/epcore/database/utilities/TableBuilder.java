package net.emeraldprison.epcore.database.utilities;

import net.emeraldprison.epcore.EPCore;
import net.emeraldprison.epcore.database.DatabaseManager;
import net.emeraldprison.epcore.database.service.HikariSQLService;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashMap;

public class TableBuilder {

    private final DatabaseManager databaseManager;

    private String tableName, query, specs;
    private String queryUpdate = "";
    private int columns;

    public static TableBuilder newTable(@NotNull String tableName, @NotNull DatabaseManager databaseManager) {
        return new TableBuilder(tableName, databaseManager);
    }

    public TableBuilder(String tableName, DatabaseManager databaseManager) {
        this.tableName = tableName;
        this.databaseManager = databaseManager;

        this.query = "CREATE TABLE IF NOT EXISTS ep_" + tableName + " (";
    }

    public TableBuilder addColumn(String columnName, SQLDataType dataType,
                                  int amount, boolean allowNull,
                                  SQLDefaultType defaultType, boolean primary) {
        this.specs = "`" + columnName + "` " + dataType;

        // Amount
        if (amount > 0) this.specs += "(" + amount + ")";

        // If Column allows null
        if (!allowNull) {
            this.specs += " NOT NULL";
        }

        // Default Type
        if (defaultType == SQLDefaultType.CUSTOM) {
            Object defaultObject = defaultType.getDefaultObject()[0];
            if (defaultObject instanceof Enum<?> || defaultObject instanceof String) {
                this.specs += " DEFAULT '" + defaultObject + "'";
            } else {
                this.specs += " DEFAULT " + defaultObject;
            }
        } else if (defaultType == SQLDefaultType.AUTO_INCREMENT) {
            this.specs += " AUTO INCREMENT";
        } else if (defaultType == SQLDefaultType.NULL) {
            this.specs += " DEFAULT NULL";
        }

        // Primary key
        if (primary) {
            this.specs += "PRIMARY KEY";
        }

        // Support for multiple columns (could be useful ig)
        if (columns > 0) {
            this.query += ", ";
        }

        query += specs;
        columns++;

        // Execute SQL Query
        try {
            if (!this.databaseManager.getResults(null,
                    "information.schema.COLUMNS",
                    "COLUMN_NAME=? AND TABLE_NAME=? AND TABLE_SCHEMA=?",
                    new HashMap<>() {{
                        put(1, columnName);
                        put(2, "ep_" + tableName);
                        put(3, databaseManager.getSqlService().getDatabaseName());
                    }}
            ).next()) {
                this.queryUpdate += "ALTER TABLE ep_" + this.tableName + " ADD " + specs + ";";
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }


        return this;
    }

    public TableBuilder setConstraints(@NotNull String... columnName) {
        StringBuilder constraints = new StringBuilder(", UNIQUE(");
        for (int index = 0; index < columnName.length; index++) {
            if (index > 0) {
                constraints.append(",");
            }

            constraints.append(columnName[index]);
        }

        this.query = constraints.toString() + ")";
        return this;
    }

    public void build() {
        this.query += ")";
        this.databaseManager.execute(query);

        if (!queryUpdate.equals("")) {
            for (String updateQuery : queryUpdate.split(";")) {
                if (updateQuery == null || updateQuery.isEmpty()) {
                    continue;
                }

                this.databaseManager.executeQuery(updateQuery);
            }
        }
    }
}
