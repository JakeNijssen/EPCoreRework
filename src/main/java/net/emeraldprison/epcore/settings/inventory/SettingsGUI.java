package net.emeraldprison.epcore.settings.inventory;

import net.emeraldprison.epcore.EPCore;
import net.emeraldprison.epcore.inventory.menu.SimpleMenu;
import net.emeraldprison.epcore.inventory.utilities.InventorySize;
import net.emeraldprison.epcore.settings.Setting;
import net.emeraldprison.epcore.utilities.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingsGUI extends SimpleMenu {

    public SettingsGUI(@NotNull Player player) {
        super(player, InventorySize.FOUR_ROWS, "Settings");
    }

    @Override
    public void populate() {
        super.populate();

        Map<Setting, Boolean> settingMap = getCoreUser().getSettings();
        int[] slots = new int[] {
                11, 12, 13, 14, 15,
                20, 21, 22, 23, 24};
        for (int index = 0; index < slots.length; index++) {
            List<Setting> settingList = new ArrayList<>(settingMap.keySet());
            if (index >= settingList.size()) {
                break;
            }

            Setting setting = settingList.get(index);
            boolean enabled = settingMap.get(settingList.get(index));

            ItemStack settingItemStack = ItemBuilder.item(enabled ? Material.LIME_DYE : Material.GRAY_DYE)
                    .name(setting.getDisplayName())
                    .setLore(setting.getDescription())
                    .addLore(" ")
                    .addLore((enabled ? "&c" : "&a") + "Click to " + (enabled ? "disable" : "enable") + " this option.")
                    .build();

            setItem(slots[index], settingItemStack, (event) -> {
                EPCore.getPlugin().getSettingsHandler().setValue(getCoreUser(), setting, !enabled);
            });
        }

        setItem(getInventory().getSize() - 5, EPCore.getPlugin().getInventoryHandler().getCloseItem(), (event) -> {
            getPlayer().closeInventory();
        });

        ItemStack backgroundItemStack = ItemBuilder.item(Material.GRAY_STAINED_GLASS_PANE)
                .name(" ")
                .build();
        fillBackground(backgroundItemStack);
    }
}
