package com.wolfeiii.epcore.configuration.serializer.implementations;

import com.wolfeiii.epcore.configuration.Configuration;
import com.wolfeiii.epcore.configuration.serializer.Serializer;
import com.wolfeiii.epcore.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class LocationSerializer extends Serializer<Location> {
    @Override
    public void saveObject(@NotNull String path, @NotNull Location object, @NotNull Configuration configuration) {
        // World, X, Y, Z, Yaw, Pitch
        configuration.set(path + ".world", object.getWorld().getName());
        configuration.set(path + ".x", Utilities.round(object.getX()));
        configuration.set(path + ".y", Utilities.round(object.getY()));
        configuration.set(path + ".z", Utilities.round(object.getZ()));
        configuration.set(path + ".yaw", Utilities.round(object.getYaw()));
        configuration.set(path + ".pitch", Utilities.round(object.getPitch()));
    }

    @Override
    public Location deserialize(@NotNull String path, @NotNull Configuration configuration) {
        ConfigurationSection section = configuration.getConfigurationSection(path);
        World world = Bukkit.getWorld(section.getString("world"));
        if (world == null) {
            throw new IllegalArgumentException("Invalid Location with path:" + section.getName() + "), world "
                    + section.getString("world") + "doesn't exist.");
        }

        return new Location(
                world,
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                section.contains("yaw") ? (float) section.getDouble("yaw") : 0,
                section.contains("pitch") ? (float) section.getDouble("pitch") : 0);
    }
}
