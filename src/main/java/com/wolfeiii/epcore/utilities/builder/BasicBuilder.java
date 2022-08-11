package com.wolfeiii.epcore.utilities.builder;

import com.wolfeiii.epcore.utilities.Utilities;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BasicBuilder<B extends BasicBuilder<B>> {

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public BasicBuilder(@NotNull ItemStack itemStack, int amount) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
        this.itemStack.setAmount(amount);
    }

    public B setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this.getThis();
    }

    public B setItemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
        return this.getThis();
    }

    public B setType(Material material) {
        this.itemStack.setType(material);
        return this.getThis();
    }

    public B setName(String name) {
        if (itemMeta == null) return this.getThis();
        itemMeta.setDisplayName(name);
        return this.getThis();
    }

    public B name(String name) {
        if (itemMeta == null) return this.getThis();
        try {
            itemMeta.setDisplayName(Utilities.translate(name));
        }catch (Exception exception) {
            exception.printStackTrace();
        }
        return this.getThis();
    }

    public B amount(int amount) {
        itemStack.setAmount(amount);
        return this.getThis();
    }

    public B addLore(String line) {
        if (itemMeta == null) return this.getThis();
        List<String> currentLore = itemMeta.getLore() != null ? itemMeta.getLore() : new ArrayList<>();
        currentLore.add(Utilities.translate(line));
        return setLore(currentLore);
    }

    public B addLore(String... lines) {
        if (itemMeta == null) return this.getThis();
        List<String> currentLore = itemMeta.getLore();
        currentLore.addAll(Utilities.translate(Arrays.asList(lines)));
        return setLore(currentLore);
    }

    public B setLore(List<String> lore) {
        if (itemMeta == null) return this.getThis();
        itemMeta.setLore(Utilities.translate(lore));
        return this.getThis();
    }

    public B setLore(String... lore) {
        if (itemMeta == null) return this.getThis();
        itemMeta.setLore(Utilities.translate(Arrays.asList(lore)));
        return this.getThis();
    }

    public B enchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        if (itemMeta == null) return this.getThis();
        itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction);
        return this.getThis();
    }

    public B enchant(Enchantment enchantment, int level) {
        if (itemMeta == null) return this.getThis();
        itemMeta.addEnchant(enchantment, level, true);
        return this.getThis();
    }

    public B disenchant(Enchantment enchantment) {
        if (itemMeta == null) return this.getThis();
        itemMeta.removeEnchant(enchantment);
        return this.getThis();
    }

    public B unbreakable(boolean unbreakable) {
        if (itemMeta == null) return this.getThis();
        itemMeta.setUnbreakable(unbreakable);
        return this.getThis();
    }

    public B addFlags(ItemFlag... itemFlags) {
        if (itemMeta == null) return this.getThis();
        itemMeta.addItemFlags(itemFlags);
        return this.getThis();
    }

    public B removeFlags(ItemFlag... itemFlags) {
        if (itemMeta == null) return this.getThis();
        itemMeta.removeItemFlags(itemFlags);
        return this.getThis();
    }

    public B setFlags(ItemFlag... itemFlags) {
        if (itemMeta == null) return this.getThis();
        for (ItemFlag itemFlag : itemMeta.getItemFlags()) {
            itemMeta.removeItemFlags(itemFlag);
        }
        itemMeta.addItemFlags(itemFlags);
        return this.getThis();
    }

    public B modelData(int data) {
        if (itemMeta == null) return this.getThis();
        itemMeta.setCustomModelData(data);
        return this.getThis();
    }

    public B damage(int damage) {
        if (itemMeta == null) return this.getThis();
        ((Damageable) itemMeta).setDamage(damage);
        return this.getThis();
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return itemStack;
    }

    protected abstract B getThis();
}