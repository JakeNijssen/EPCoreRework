package com.wolfeiii.epcore.ui.bossbar.objects;

import org.jetbrains.annotations.NotNull;

public enum BossBarDivision {

    NO_DIVISION,
    NOTCHED_6,
    NOTCHED_10,
    NOTCHED_12,
    NOTCHED_20;

    @SuppressWarnings("unchecked")
    public <T> T from(@NotNull Class<T> type) {
        if (type == String.class) {
            return (T) name();
        }
        throw new UnsupportedOperationException("Can't get BossBarDivision from other class than String.");
    }
}
