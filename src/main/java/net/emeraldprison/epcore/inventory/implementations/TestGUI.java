package net.emeraldprison.epcore.inventory.implementations;

import net.emeraldprison.epcore.inventory.menu.SimpleMenu;
import net.emeraldprison.epcore.inventory.utilities.InventorySize;
import net.emeraldprison.epcore.utilities.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TestGUI extends SimpleMenu {

    public TestGUI(@NotNull Player player) {
        super(player, InventorySize.THREE_ROWS, "Title");

        setCloseFilter((closingPlayer) -> {
            EntityEquipment equipment = closingPlayer.getEquipment();

            // The player will not be able to close if he/she wears a golden chest-plate.
            return equipment != null
                    && equipment.getChestplate() != null
                    && equipment.getChestplate().getType() == Material.GOLDEN_CHESTPLATE;
        });
    }

    @Override
    public void populate() {
        super.populate();

        ItemStack itemStack = ItemBuilder.item(Material.GRASS_BLOCK)
                .name("&cCool Grass")
                .setLore("&2First Line", "&aSecond Line")
                .amount(32)
                .build();

        addItem(itemStack, (event) -> {
            // This code will be executed when the player clicks the item.
            getPlayer().sendMessage("You clicked on grass.");
        });

        setItem(5, itemStack, (event) -> {
            // This code will be executed when the player clicks the item.
            getPlayer().sendMessage("You clicked on another grass.");
        });
    }

    @Override
    protected void onOpen(InventoryOpenEvent event) {
        event.getPlayer().sendMessage("You opened this inventory, congratulations!");
    }
}
