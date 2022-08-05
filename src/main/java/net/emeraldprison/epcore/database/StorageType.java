package net.emeraldprison.epcore.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum StorageType {

    SQLITE(),
    MYSQL();

    public static StorageType parse(@Nullable String name) {
        if (name == null) {
            return null;
        }

        try {
            return valueOf(name.toUpperCase());
        } catch (Exception exception) {
            return SQLITE;
        }
    }
}
