package com.wolfeiii.epcore.settings.inventory;

import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.inventory.utilities.InventorySize;
import com.wolfeiii.epcore.utilities.builder.ItemBuilder;
import com.wolfeiii.epcore.inventory.menu.SimpleMenu;
import com.wolfeiii.epcore.settings.Setting;
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

            ItemStack settingItemStack = ItemBuilder.item(setting.getType())
                    .name(setting.getDisplayName())
                    .addLore(" ")
                    .addLore(setting.getDescription().toArray(new String[0]))
                    .addLore(" ")
                    .addLore("&f&nClick to toggle " + setting.getToggleMessage())
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
