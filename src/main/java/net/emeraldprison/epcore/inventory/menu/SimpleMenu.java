package net.emeraldprison.epcore.inventory.menu;

import lombok.Getter;
import lombok.Setter;
import net.emeraldprison.epcore.EPCore;
import net.emeraldprison.epcore.inventory.utilities.AnimationType;
import net.emeraldprison.epcore.inventory.utilities.InventorySize;
import net.emeraldprison.epcore.users.object.CoreUser;
import net.emeraldprison.epcore.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class SimpleMenu implements InventoryHolder {

    private final Map<Integer, Consumer<InventoryClickEvent>> itemHandlers = new HashMap<>();

    private final List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
    private final List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();
    private final List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();

    private Predicate<Player> closeFilter;

    @Getter
    private final Player player;
    private final Inventory inventory;

    @Setter
    private AnimationType snakeType;

    public SimpleMenu(@NotNull Player player, InventorySize size) {
        this(player, size, InventoryType.CHEST.getDefaultTitle());
    }

    public SimpleMenu(@NotNull Player player, InventorySize size, String title) {
        this(player, size.getSlots(), InventoryType.CHEST, title);
    }

    public SimpleMenu(@NotNull Player player, @NotNull InventoryType type) {
        this(player, type, type.getDefaultTitle());
    }

    public SimpleMenu(@NotNull Player player, @NotNull InventoryType type, @NotNull String title) {
        this(player, 0, type, title);
    }

    private SimpleMenu(@NotNull Player player, int size, InventoryType type, String title) {
        this.player = player;
        if (type == InventoryType.CHEST && size > 0) {
            this.inventory = Bukkit.createInventory(this, size, Utilities.translate(title));
        } else {
            this.inventory = Bukkit.createInventory(this, type, title);
        }

        if (inventory.getHolder() != this) {
            throw new IllegalStateException("Created Inventory but the holder doesn't belong to EPCore, instead found: " + inventory.getHolder());
        }

        populate();
    }

    public void populate() {
        this.inventory.clear();

        if (snakeType != null) {
            int[] slots = snakeType == AnimationType.BORDERS ? getBorders() : getCorners();
            int snakeLength = slots.length;
        }
    }

    protected void onOpen(InventoryOpenEvent event) {}

    protected void onClick(InventoryClickEvent event) {}

    protected void onClose(InventoryCloseEvent event) {}

    public void open() {
        player.openInventory(getInventory());
    }

    public CoreUser getCoreUser() {
        return EPCore.getPlugin().getUserHandler().getUser(player);
    }

    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    public void addItem(@NotNull ItemStack itemStack) {
        addItem(itemStack, null);
    }

    public void addItem(@NotNull ItemStack itemStack, @Nullable Consumer<InventoryClickEvent> clickHandler) {
        int slot = this.inventory.firstEmpty();
        if (slot != -1) {
            setItem(slot, itemStack, clickHandler);
        }
    }

    public void setItem(int slot, @NotNull ItemStack itemStack) {
        setItem(slot, itemStack, null);
    }

    public void setItem(int slot, @NotNull ItemStack itemStack, @Nullable Consumer<InventoryClickEvent> clickHandler) {
        this.inventory.setItem(slot, itemStack);

        if (clickHandler != null) {
            this.itemHandlers.put(slot, clickHandler);
        } else {
            this.itemHandlers.remove(slot);
        }
    }

    public void setItems(int slotFrom, int slotTo, ItemStack itemStack) {
        setItems(slotFrom, slotTo, itemStack, null);
    }

    public void setItems(int slotFrom, int slotTo, ItemStack itemStack, Consumer<InventoryClickEvent> clickHandler) {
        for (int index = slotFrom; index <= slotTo; index++) {
            setItem(index, itemStack, clickHandler);
        }
    }

    public void setItems(int[] slots, ItemStack item) {
        setItems(slots, item, null);
    }

    public void setItems(int[] slots, ItemStack itemStack, Consumer<InventoryClickEvent> handler) {
        for (int slot : slots) {
            setItem(slot, itemStack, handler);
        }
    }

    public void removeItem(int slot) {
        this.inventory.clear(slot);
        this.itemHandlers.remove(slot);
    }

    public void removeItems(int... slots) {
        for (int slot : slots) {
            removeItem(slot);
        }
    }

    public void fillBackground(@NotNull ItemStack itemStack) {
        for (int index = 0; index < inventory.getSize(); index++) {
            if (getItem(index) == null || getItem(index).getType() == Material.AIR) {
                setItem(index, itemStack);
            }
        }
    }

    public void setCloseFilter(Predicate<Player> closeFilter) {
        this.closeFilter = closeFilter;
    }

    public void addOpenHandler(Consumer<InventoryOpenEvent> openHandler) {
        this.openHandlers.add(openHandler);
    }

    public void addCloseHandler(Consumer<InventoryCloseEvent> closeHandler) {
        this.closeHandlers.add(closeHandler);
    }

    public void addClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        this.clickHandlers.add(clickHandler);
    }

    public int[] getBorders() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> size < 27 || i < 9 || i % 9 == 0 || (i - 8) % 9 == 0 || i > size - 9).toArray();
    }

    public int[] getCorners() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> i < 2 || (i > 6 && i < 10) || i == 17 || i == size - 18 || (i > size - 11 && i < size - 7) || i > size - 3).toArray();
    }

    @Override @NotNull
    public Inventory getInventory() {
        return inventory;
    }

    public void handleOpen(@NotNull InventoryOpenEvent event) {
        onOpen(event);

        this.openHandlers.forEach(handler -> handler.accept(event));
    }

    public boolean handleClose(@NotNull InventoryCloseEvent event) {
        onClose(event);

        this.closeHandlers.forEach(handler -> handler.accept(event));
        return this.closeFilter != null && this.closeFilter.test((Player) event.getPlayer());
    }

    public void handleClick(@NotNull InventoryClickEvent event) {
        onClick(event);

        this.clickHandlers.forEach(handler -> handler.accept(event));
        Consumer<InventoryClickEvent> clickConsumer = this.itemHandlers.get(event.getRawSlot());

        if (clickConsumer != null) {
            clickConsumer.accept(event);
        }
    }
}
