package com.wolfeiii.epcore.api;

import com.wolfeiii.epcore.database.DatabaseManager;
import com.wolfeiii.epcore.economy.EconomyHandler;
import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.utilities.logging.EPCoreLogger;

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

