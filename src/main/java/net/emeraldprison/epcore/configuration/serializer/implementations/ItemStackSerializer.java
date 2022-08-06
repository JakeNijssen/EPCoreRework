package net.emeraldprison.epcore.configuration.serializer.implementations;

import net.emeraldprison.epcore.configuration.Configuration;
import net.emeraldprison.epcore.configuration.serializer.Serializer;
import net.emeraldprison.epcore.configuration.serializer.Serializers;
import net.emeraldprison.epcore.utilities.Utilities;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ItemStackSerializer extends Serializer<ItemStack> {
    @Override
    public void saveObject(@NotNull String path, @NotNull ItemStack object, @NotNull Configuration configuration) {
        configuration.set(path + ".material", object.getType().toString());
        configuration.set(path + ".amount", object.getAmount());
        if ((object.getItemMeta() instanceof Damageable) && ((Damageable) object.getItemMeta()).getDamage() != 0) {
            configuration.set(path + ".durability", ((Damageable) object.getItemMeta()).getDamage());
        }

        for (Map.Entry<Enchantment, Integer> entry : object.getEnchantments().entrySet()) {
            configuration.set(path + ".enchantments." + entry.getKey().getKey().getKey(), entry.getValue());
        }

        ItemMeta itemMeta = object.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        itemMeta.getDisplayName();
        configuration.set(path + ".name", itemMeta.getDisplayName().replace("§", "&"));

        if (itemMeta.getLore() != null && itemMeta.getLore().size() != 0) {
            List<String> raw = new ArrayList<>();
            for (String line : itemMeta.getLore()) {
                raw.add(line.replace("§", "&"));
            }

            configuration.set(path + ".lore", raw);
        }

        object.setItemMeta(itemMeta);
    }

    @Override
    public ItemStack deserialize(@NotNull String path, @NotNull Configuration configuration) {
        ConfigurationSection section = configuration.getConfigurationSection(path);

        String rawMaterial = section.getString("material");
        if (rawMaterial == null) {
            throw new IllegalArgumentException("No material in ItemStack (path: " + section.getName() + ")");
        }

        Material material = Material.getMaterial(rawMaterial);
        if (material == null) {
            throw new IllegalArgumentException("Invalid material (" + rawMaterial + ") in ItemStack (path: " + section.getName() + ")");
        }

        int amount = section.contains("amount") ? section.getInt("amount") : 1;
        int durability = section.contains("durability") ? section.getInt("durability") : 0;
        String name = section.getString("name");

        boolean hasLore = section.getConfigurationSection("lore") != null &&
                section.getConfigurationSection("lore").getString("structure") != null;
        List<String> lore = hasLore ?
                Serializers.of(List.class).deserialize(path + ".lore", configuration) :
                new ArrayList<>();

        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (name != null) {
            itemMeta.setDisplayName(Utilities.translate(name));
        }

        if (lore != null && lore.size() != 0) {
            itemMeta.setLore(Utilities.translate(lore));
        }

        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(durability);
        }

        itemStack.setItemMeta(itemMeta);

        if (section.contains("enchantments")) {
            ConfigurationSection enchantments = section.getConfigurationSection("enchantments");
            for (String key : enchantments.getKeys(false)) {
                itemStack.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.fromString(key)), enchantments.getInt(key));
            }
        }

        return itemStack;
    }
}
