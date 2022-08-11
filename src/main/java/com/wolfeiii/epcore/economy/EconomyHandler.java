package com.wolfeiii.epcore.economy;

import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.api.events.player.PlayerPurchaseEvent;
import com.wolfeiii.epcore.utilities.logging.LogLevel;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EconomyHandler {

    private final EPCore core;
    private Economy economy;

    public EconomyHandler(@NotNull EPCore core) {
        this.core = core;
    }

    public boolean setup() {
        if (core.getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> economyProvider = core.getServer().getServicesManager().getRegistration(Economy.class);

           if (economyProvider == null) {
               core.getCoreLogger().log(LogLevel.WARN, "Could not find and setup Vault support.");
               core.getCoreLogger().log(LogLevel.WARN, "Do you have Vault installed?.");
               return false;
           }

           this.economy = economyProvider.getProvider();
           return true;
        }

        this.economy = null;
        return false;
    }

    public double getBalance(@NotNull UUID uuid) {
        Player player = getPlayer(uuid);
        if (player != null) {
            return economy.getBalance(player);
        }

        // Player doesn't exist or isn't online, and so we return -1;
        return -1D;
    }

    public EconomyResponse addToBalance(@NotNull UUID uuid, double amountToAdd) {
        Player player = getPlayer(uuid);
        if (player != null) {
            return economy.depositPlayer(player, Math.abs(amountToAdd));
        }

        // Player doesn't exist or isn't online, and we shouldn't do anything.
        return null;
    }

    public EconomyResponse removeFromBalance(@NotNull UUID uuid, double amountToRemove) {
        Player player = getPlayer(uuid);
        if (player != null) {
            return economy.withdrawPlayer(player, Math.abs(amountToRemove));
        }

        // Player doesn't exist or isn't online, and we shouldn't do anything.
        return null;
    }

    public boolean canPurchase(@NotNull UUID uuid, double purchaseAmount) {
        Player player = getPlayer(uuid);
        if (player == null)
            return false;

        PlayerPurchaseEvent event = new PlayerPurchaseEvent(player, purchaseAmount);
        return hasEnough(uuid, purchaseAmount) && !event.isCancelled();
    }

    public boolean hasEnough(@NotNull UUID uuid, double purchaseAmount) {
        return getBalance(uuid) >= purchaseAmount;
    }

    public Player getPlayer(@NotNull UUID uuid) {
        return core.getServer().getPlayer(uuid);
    }
}
