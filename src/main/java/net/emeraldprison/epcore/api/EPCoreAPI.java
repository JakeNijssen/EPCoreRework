package net.emeraldprison.epcore.api;

import net.emeraldprison.epcore.EPCore;
import net.emeraldprison.epcore.database.DatabaseManager;
import net.emeraldprison.epcore.economy.EconomyHandler;
import net.emeraldprison.epcore.utilities.logging.EPCoreLogger;
import org.jetbrains.annotations.NotNull;

public class EPCoreAPI {

    private static final EPCore core = EPCore.getPlugin();

    public static EconomyHandler getEconomyHandler() {
        return core.getEconomyHandler();
    }

    public static DatabaseManager getMainDatabaseManager() {
        return core.getDatabaseManager();
    }

    public static EPCoreLogger getCoreLogger() {
        return core.getCoreLogger();
    }
}

