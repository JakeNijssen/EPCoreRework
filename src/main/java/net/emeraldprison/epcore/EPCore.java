package net.emeraldprison.epcore;

import lombok.Getter;
import net.emeraldprison.epcore.database.DatabaseManager;
import net.emeraldprison.epcore.economy.EconomyHandler;
import net.emeraldprison.epcore.inventory.listener.InventoryListener;
import net.emeraldprison.epcore.inventory.menu.SimpleMenu;
import net.emeraldprison.epcore.inventory.utilities.InventorySize;
import net.emeraldprison.epcore.users.UserHandler;
import net.emeraldprison.epcore.utilities.logging.EPCoreLogger;
import net.emeraldprison.epcore.utilities.logging.LogLevel;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
public final class EPCore extends JavaPlugin {

    private static EPCore instance;

    private final EPCoreLogger coreLogger = new EPCoreLogger(ChatColor.GRAY, LogLevel.INFO);
    private final DatabaseManager databaseManager = new DatabaseManager(this);
    private final EconomyHandler economyHandler = new EconomyHandler(this);
    private final UserHandler userHandler = new UserHandler(this);

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

        databaseManager.loadDefaultTables();

        registerListeners(
                new InventoryListener(this)
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
