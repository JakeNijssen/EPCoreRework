package net.emeraldprison.epcore;

import lombok.Getter;
import net.emeraldprison.epcore.api.EPCoreAPI;
import net.emeraldprison.epcore.configuration.Configuration;
import net.emeraldprison.epcore.configuration.serializer.styling.NameStyle;
import net.emeraldprison.epcore.database.DatabaseManager;
import net.emeraldprison.epcore.economy.EconomyHandler;
import net.emeraldprison.epcore.inventory.InventoryHandler;
import net.emeraldprison.epcore.inventory.listener.InventoryListener;
import net.emeraldprison.epcore.inventory.menu.SimpleMenu;
import net.emeraldprison.epcore.inventory.utilities.InventorySize;
import net.emeraldprison.epcore.settings.SettingsHandler;
import net.emeraldprison.epcore.settings.inventory.SettingsGUI;
import net.emeraldprison.epcore.statistics.StatisticsHandler;
import net.emeraldprison.epcore.users.UserHandler;
import net.emeraldprison.epcore.users.listeners.UserJoinListener;
import net.emeraldprison.epcore.users.listeners.UserQuitListener;
import net.emeraldprison.epcore.utilities.builder.ItemBuilder;
import net.emeraldprison.epcore.utilities.logging.EPCoreLogger;
import net.emeraldprison.epcore.utilities.logging.LogLevel;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;

@Getter
public final class EPCore extends JavaPlugin {

    private static EPCore instance;

    // Handlers
    private final EPCoreLogger coreLogger = new EPCoreLogger(ChatColor.GRAY, LogLevel.INFO);
    private final DatabaseManager databaseManager = new DatabaseManager(this);
    private final EconomyHandler economyHandler = new EconomyHandler(this);
    private final SettingsHandler settingsHandler = new SettingsHandler(this);
    private final StatisticsHandler statisticsHandler = new StatisticsHandler(this);
    private final InventoryHandler inventoryHandler = new InventoryHandler(this);
    private final UserHandler userHandler = new UserHandler(this);

    // Configuration
    private final Configuration configuration = new Configuration(this, new File(getDataFolder(), "config.yml"), NameStyle.HYPHEN, true);
    private final Configuration sqlConfig = new Configuration(this, new File(getDataFolder(), "sql.yml"), NameStyle.HYPHEN, false);

    public EPCore() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Set up Database handler...
        if (!databaseManager.setup()) {
            // Database Manager failed to set up, no databases for this plugin... :(
            getCoreLogger().log(LogLevel.FATAL, "Database Manager failed to set up for " + getPlugin().getName() + ", disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Set up Economy handler...
        if (!economyHandler.setup()) {
            getCoreLogger().log(LogLevel.FATAL, "Economy Manager failed to set up for EPCore, disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Set up Settings handler...
        if (!settingsHandler.setup()) {
            getCoreLogger().log(LogLevel.FATAL, "Settings Manager failed to set up for EPCore, disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!inventoryHandler.setup()) {
            getCoreLogger().log(LogLevel.FATAL, "Inventory Manager failed to set up for EPCore, disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!statisticsHandler.setup()) {
            getCoreLogger().log(LogLevel.FATAL, "Statistics Manager failed to set up for EPCore, disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Load all the default tables.
        databaseManager.loadDefaultTables();

        registerListeners(
                new InventoryListener(this),
                new UserJoinListener(),
                new UserQuitListener()
        );
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static EPCore getPlugin() {
        return instance;
    }

    public void registerListeners(@NotNull Listener... listeners) {
        Arrays.stream(listeners)
                .forEach(listener ->
                        getServer().getPluginManager().registerEvents(listener, this));
    }
}
