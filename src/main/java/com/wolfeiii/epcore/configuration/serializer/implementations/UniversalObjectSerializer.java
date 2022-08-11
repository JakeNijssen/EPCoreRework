package com.wolfeiii.epcore.configuration.serializer.implementations;

import com.wolfeiii.epcore.utilities.logging.LogLevel;
import com.wolfeiii.epcore.utilities.reflection.ReflectionUtilities;
import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.configuration.Configuration;
import com.wolfeiii.epcore.configuration.serializer.Serializer;
import com.wolfeiii.epcore.configuration.serializer.Serializers;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
        String classPath = configuration.getString(path + ".type");
        Class<?> clazz;

        try {
            clazz = Class.forName(classPath);
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException("An error occurred while deserializing class '" + classPath + "'", exception);
        }

        validateDefaultConstructor(clazz);
        if (!Serializable.class.isAssignableFrom(clazz)) {
            throw new RuntimeException("Class with classpath '" + classPath + "' does not implement Serializable");
        }

        Serializable instance;
        try {
            instance = (Serializable) clazz.getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException("An error occurred while deserializing class '" + classPath + "'", exception);
        }

        try {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);

                Class<?> type = field.getType();
                if (ReflectionUtilities.isSimpleType(type)) {
                    field.set(instance, configuration.get(path + "." + configuration.getNameStyle().format(field.getName())));
                } else {
                    Serializer<?> serializer = Serializers.of(field.getType());
                    if (serializer == null) {
                        throw new UnsupportedOperationException("Missing Serializer for type: " + field.getType());
                    }

                    field.set(instance, serializer.deserialize(path + "." + configuration.getNameStyle().format(field.getName()), configuration));
                }
            }
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }

        return instance;
    }

    private void validateDefaultConstructor(Class<?> clazz) {
        try {
            clazz.getConstructor(); // Get no-args constructor for test
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " does not have a default constructor");
        }
    }
}
