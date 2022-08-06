package net.emeraldprison.epcore.utilities.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtilities {

    public static final Map<Class<?>, Class<?>> WRAPPERS = new HashMap<>();

    static {
        WRAPPERS.put(boolean.class, Boolean.class);
        WRAPPERS.put(int.class, Integer.class);
        WRAPPERS.put(char.class, Character.class);
        WRAPPERS.put(byte.class, Byte.class);
        WRAPPERS.put(short.class, Short.class);
        WRAPPERS.put(double.class, Double.class);
        WRAPPERS.put(long.class, Long.class);
        WRAPPERS.put(float.class, Float.class);
    }

    public static boolean isSimpleType(Object object) {
        return isSimpleType(object.getClass()) ||
                (object instanceof Collection && isSimpleType(getCollectionGeneric((Collection<?>) object)));
    }

    public static boolean isSimpleType(Class<?> type) {
        return isPrimitiveOrWrapper(type) || type.equals(String.class);
    }

    public static boolean isSimpleType(Method method) {
        Class<?> type = method.getReturnType();
        if (isSimpleType(type)) {
            return true;
        }

        if (!type.isAssignableFrom(Collection.class)) {
            return false;
        }

        if (!(method.getGenericReturnType() instanceof ParameterizedType returnTypes)) {
            throw new IllegalArgumentException("Could not get generic type of " + method.getName());
        }

        Type generic = returnTypes.getActualTypeArguments()[0];
        if (!(generic instanceof Class)) {
            throw new IllegalArgumentException("Could not get generic type of " + method.getName() + ". Config's method generic type can't be wildcard");
        }

        return isSimpleType((Class<?>) generic);
    }

    public static Class<?> getCollectionGeneric(Collection<?> collection) {
        for (Object element : collection) {
            if (element == null) {
                continue;
            }

            return element.getClass();
        }

        throw new IllegalArgumentException("Can't get generic type of empty Collection");
    }

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz.equals(Boolean.class) ||
                clazz.equals(Integer.class) ||
                clazz.equals(Character.class) ||
                clazz.equals(Byte.class) ||
                clazz.equals(Short.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(Float.class);
    }

}
