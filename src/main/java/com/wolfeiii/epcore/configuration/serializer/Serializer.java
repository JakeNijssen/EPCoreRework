package com.wolfeiii.epcore.configuration.serializer;

import com.wolfeiii.epcore.configuration.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;

@SuppressWarnings("unchecked")
public abstract class Serializer<T> {

    private final Class<T> serializerType;

    public Serializer() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        if (!(type.getActualTypeArguments()[0] instanceof Class<?>)) {
            throw new IllegalStateException("Caught invalid value in generic.");
        }

        this.serializerType = (Class<T>) type.getActualTypeArguments()[0];
    }

    public final void serialize(@NotNull String path, @Nullable Object object, @NotNull Configuration configuration) {
        configuration.set(path, null);
        if (object == null)
            return;

        this.saveObject(path, (T) object, configuration);
    }

    public abstract void saveObject(@NotNull String path, @NotNull T object, @NotNull Configuration configuration);

    public abstract T deserialize(@NotNull String path, @NotNull Configuration configuration);

    public Class<T> getSerializerType() {
        return serializerType;
    }
}
