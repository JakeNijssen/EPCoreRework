package com.wolfeiii.epcore.configuration.serializer.implementations;

import com.wolfeiii.epcore.configuration.Configuration;
import com.wolfeiii.epcore.configuration.serializer.Serializer;
import org.jetbrains.annotations.NotNull;

public class UniversalArraySerializer extends Serializer<Object[]> {

    @Override
    public void saveObject(@NotNull String path, Object @NotNull [] object, @NotNull Configuration configuration) {

    }

    @Override
    public Object[] deserialize(@NotNull String path, @NotNull Configuration configuration) {
        return new Object[0];
    }
}
