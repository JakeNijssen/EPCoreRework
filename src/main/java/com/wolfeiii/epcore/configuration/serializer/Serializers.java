package com.wolfeiii.epcore.configuration.serializer;

import com.wolfeiii.epcore.configuration.serializer.implementations.*;
import com.wolfeiii.epcore.settings.Setting;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class Serializers {

    public static final Map<Class<?>, Serializer<?>> SERIALIZERS = new LinkedHashMap<>();
    private static final Serializer UNIVERSAL_ARRAY_SERIALIZER = new UniversalArraySerializer();
    private static final Serializer UNIVERSAL_OBJECT_SERIALIZER = new UniversalObjectSerializer();

    static {
        register(Location.class, new LocationSerializer());
        register(ItemStack.class, new ItemStackSerializer());
        register(Material.class, new MaterialSerializer());
        register(Setting.class, new SettingSerializer());
    }

    public static <T> Serializer<T> of(Class<T> clazz) {
        if (SERIALIZERS.containsKey(clazz)) {
            return (Serializer<T>) SERIALIZERS.get(clazz);
        }

        for (Map.Entry<Class<?>, Serializer<?>> entry : SERIALIZERS.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                return (Serializer<T>) entry.getValue();
            }
        }

        if (clazz.isArray())
            return UNIVERSAL_ARRAY_SERIALIZER;

        if (Serializable.class.isAssignableFrom(clazz))
            return UNIVERSAL_OBJECT_SERIALIZER;

        return null;
    }

    public static <T> Serializer<T> of(T type) {
        if (SERIALIZERS.containsKey(type.getClass())) {
            return (Serializer<T>) SERIALIZERS.get(type.getClass());
        }

        for (Map.Entry<Class<?>, Serializer<?>> entry : SERIALIZERS.entrySet()) {
            if (entry.getKey().isAssignableFrom(type.getClass())) {
                return (Serializer<T>) entry.getValue();
            }
        }

        if (type.getClass().isArray())
            return UNIVERSAL_ARRAY_SERIALIZER;

        if (type instanceof Serializable)
            return UNIVERSAL_OBJECT_SERIALIZER;

        return null;
    }

    public static Serializer<?> of(String classPath) {
        try {
            Class<?> clazz = Class.forName(classPath);
            return of(clazz);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Missing Serializer for classpath: " + classPath);
        }
    }

    public static void register(Class<?> clazz, Serializer<?> serializer) {
        if (!clazz.equals(serializer.getSerializerType())) {
            throw new IllegalStateException("Can't register serializer " + serializer.getClass().getName());
        }

        SERIALIZERS.put(clazz, serializer);
    }

    public static void unregister(Class<?> clazz) {
        SERIALIZERS.remove(clazz);
    }
}
