package com.wolfeiii.epcore.inventory.listener;

import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.inventory.menu.SimpleMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

public record InventoryListener(@NotNull EPCore core) implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof SimpleMenu menu && event.getClickedInventory() != null) {
            boolean wasCancelled = event.isCancelled();
            event.setCancelled(true);

            menu.handleClick(event);

            if (!wasCancelled && !event.isCancelled()) {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof SimpleMenu simpleMenu) {
            simpleMenu.handleOpen(event);

            core.getInventoryHandler().addInventory(simpleMenu);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof SimpleMenu simpleMenu) {
            core.getInventoryHandler().removeInventory(simpleMenu);
            if (simpleMenu.handleClose(event)) {
                core.getServer().getScheduler().runTask(
                        core,
                        simpleMenu::open);
            }
        }
    }
}
