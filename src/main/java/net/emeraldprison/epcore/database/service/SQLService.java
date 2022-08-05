package net.emeraldprison.epcore.database.service;

import com.zaxxer.hikari.HikariConfig;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class SQLService {

    public abstract Connection getConnection() throws SQLException;

    public abstract HikariConfig createConfig();

    public abstract String getDatabaseName();
}
