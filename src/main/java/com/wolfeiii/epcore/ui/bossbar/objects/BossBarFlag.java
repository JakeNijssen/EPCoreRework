package com.wolfeiii.epcore.ui.bossbar.objects;

import org.jetbrains.annotations.NotNull;

public enum BossBarFlag {

    DARKEN_SCREEN,
    PLAY_BOSS_MUSIC,
    CREATE_WORLD_FOG;

    @SuppressWarnings("unchecked")
    public <T> T from(@NotNull Class<T> type) {
        if (type == String.class) {
            return (T) name();
        }
        throw new UnsupportedOperationException("Can't get BossBarFlag from other class than String.");
    }
}
