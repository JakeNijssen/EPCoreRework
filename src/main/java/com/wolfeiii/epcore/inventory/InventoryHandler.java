package com.wolfeiii.epcore.inventory;

import com.wolfeiii.epcore.configuration.serializer.styling.NameStyle;
import lombok.Getter;
import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.configuration.Configuration;
import com.wolfeiii.epcore.inventory.menu.SimpleMenu;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InventoryHandler {

    private final EPCore core;
    private final List<SimpleMenu> tickInventories = new ArrayList<>();

    @Getter
    private final Configuration inventoryConfiguration;

    public InventoryHandler(@NotNull EPCore core) {
        this.core = core;
        this.inventoryConfiguration = new Configuration(core, new File(core.getDataFolder(), "inventory.yml"), NameStyle.HYPHEN, true);
    }

    public boolean setup() {
        core.getServer().getScheduler().runTaskTimer(
                core,
                () -> {
                    this.tickInventories.forEach(SimpleMenu::populate);
                },
                0L, 1L
        );

        return true;
    }

    public void addInventory(@NotNull SimpleMenu inventory) {
        this.tickInventories.add(inventory);
    }

    public void removeInventory(@NotNull SimpleMenu inventory) {
        this.tickInventories.remove(inventory);
    }

    public ItemStack getCloseItem() {
        return inventoryConfiguration.get("items.close", ItemStack.class);
    }
}
