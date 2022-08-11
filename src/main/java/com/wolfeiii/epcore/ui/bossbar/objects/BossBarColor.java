package com.wolfeiii.epcore.ui.bossbar.objects;

import org.jetbrains.annotations.NotNull;

public enum BossBarColor {

    PINK,
    BLUE,
    RED,
    GREEN,
    YELLOW,
    PURPLE,
    WHITE;

    @SuppressWarnings("unchecked")
    public <T> T from(@NotNull Class<T> type) {
        if (type == String.class) {
            return (T) name();
        }
        throw new UnsupportedOperationException("Can't get BossBarColor from other class than String.");
    }

}
