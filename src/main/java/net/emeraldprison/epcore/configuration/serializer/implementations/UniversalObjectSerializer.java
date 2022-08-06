package net.emeraldprison.epcore.configuration.serializer.implementations;

import net.emeraldprison.epcore.EPCore;
import net.emeraldprison.epcore.configuration.Configuration;
import net.emeraldprison.epcore.configuration.serializer.Serializer;
import net.emeraldprison.epcore.configuration.serializer.Serializers;
import net.emeraldprison.epcore.utilities.logging.LogLevel;
import net.emeraldprison.epcore.utilities.reflection.ReflectionUtilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class UniversalObjectSerializer extends Serializer<Serializable> {
    @Override
    public void saveObject(@NotNull String path, @NotNull Serializable object, @NotNull Configuration configuration) {
        validateDefaultConstructor(object.getClass());

        try {
            for (Field field : object.getClass().getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);
                Object value = field.get(object);

                try {
                    if (ReflectionUtilities.isSimpleType(field.getType())) {
                        configuration.set(path + "." + configuration.getNameStyle().format(field.getName()), value);
                    } else {
                        Serializer<?> serializer = Serializers.of(field.getType());
                        if (serializer == null) {
                            throw new UnsupportedOperationException("Missing Serializer for type: " + field.getType());
                        }

                        serializer.serialize(path + "." + configuration.getNameStyle().format(field.getName()), value, configuration);
                    }
                } catch (Exception exception) {
                    EPCore.getPlugin().getCoreLogger().log(LogLevel.ERROR, "An error occurred whilst serializing field '" + field.getName()
                            + "' from class '" + object.getClass().getName() + "'. Read stacktrace below.");
                    exception.printStackTrace();
                }
            }

            configuration.set(path + "." + configuration.getNameStyle().format("type"), object.getClass().getName());
        } catch (IllegalAccessException exception) {
            EPCore.getPlugin().getCoreLogger().log(LogLevel.ERROR, "An error occurred whilst serializing " +
                    "class '" + object.getClass().getName() + "'. Read stacktrace below.");
            exception.printStackTrace();
        }
    }

    @Override
    public Serializable deserialize(@NotNull String path, @NotNull Configuration configuration) {
        return null;
    }

    private void validateDefaultConstructor(Class<?> clazz) {
        try {
            clazz.getConstructor(); // Get no-args constructor for test
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " does not have a default constructor");
        }
    }
}
