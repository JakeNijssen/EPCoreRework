package net.emeraldprison.epcore.configuration.serializer.implementations;

import net.emeraldprison.epcore.configuration.Configuration;
import net.emeraldprison.epcore.configuration.serializer.Serializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class UniversalArraySerializer extends Serializer<Object[]> {

    @Override
    public void saveObject(@NotNull String path, Object @NotNull [] object, @NotNull Configuration configuration) {

    }

    @Override
    public Object[] deserialize(@NotNull String path, @NotNull Configuration configuration) {
        return new Object[0];
    }
}
