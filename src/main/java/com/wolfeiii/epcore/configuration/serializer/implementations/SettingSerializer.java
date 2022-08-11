package com.wolfeiii.epcore.configuration.serializer.implementations;

import com.wolfeiii.epcore.configuration.Configuration;
import com.wolfeiii.epcore.configuration.serializer.Serializer;
import com.wolfeiii.epcore.utilities.Utilities;
import com.wolfeiii.epcore.settings.Setting;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettingSerializer extends Serializer<Setting> {
    @Override
    public void saveObject(@NotNull String path, @NotNull Setting object, @NotNull Configuration configuration) {
        configuration.set(path + ".material.type", object.getType());
        configuration.set(path + ".material.color", object.getColor());
        configuration.set(path + ".material.display-name", object.getDisplayName());
        configuration.set(path + ".material.description", object.getDescription());

        configuration.set(path + ".options.commands", object.getCommands());
        configuration.set(path + ".options.message", object.isMessage());
        configuration.set(path + ".options.toggle-message", object.getToggleMessage());
        configuration.set(path + ".options.confirmation", object.isConfirmation());
        configuration.set(path + ".options.defaultEnabled", object.isDefaultEnabled());
    }

    @Override
    public Setting deserialize(@NotNull String path, @NotNull Configuration configuration) {
        String name = path.split("\\.")[path.split("\\.").length - 1];
        Material material = Material.getMaterial(configuration.getString(path + ".material.type"));
        ChatColor chatColor = ChatColor.valueOf(configuration.getString(path + ".material.color").toUpperCase());
        String displayName = Utilities.translate(configuration.getString(path + ".material.display-name"));
        List<String> description = Utilities.translate(configuration.getStringList(path + ".material.description"));

        List<String> commands = configuration.getStringList(path + ".options.commands");
        boolean message = configuration.getBoolean(path + ".options.message");
        String toggleMessage = configuration.getString(path + ".options.toggle-message");
        boolean confirmation = configuration.getBoolean(path + ".options.confirmation");
        boolean defaultEnabled = configuration.getBoolean(path + ".options.defaultEnabled");

        return new Setting(name, material, chatColor, toggleMessage, displayName, description,
                commands, message, confirmation, defaultEnabled);
    }
}
