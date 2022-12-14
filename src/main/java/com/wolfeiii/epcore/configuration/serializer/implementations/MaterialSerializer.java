package com.wolfeiii.epcore.configuration.serializer.implementations;

import com.wolfeiii.epcore.configuration.Configuration;
import com.wolfeiii.epcore.configuration.serializer.Serializer;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class MaterialSerializer extends Serializer<Material> {
    @Override
    public void saveObject(@NotNull String path, @NotNull Material object, @NotNull Configuration configuration) {
        configuration.set(path, object.toString());
    }

    @Override
    public Material deserialize(@NotNull String path, @NotNull Configuration configuration) {
        return Material.getMaterial(configuration.getString(path));
    }
}
