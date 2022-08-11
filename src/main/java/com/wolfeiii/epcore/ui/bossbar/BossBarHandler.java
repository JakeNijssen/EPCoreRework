package com.wolfeiii.epcore.ui.bossbar;

import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.ui.bossbar.objects.BossBar;
import com.wolfeiii.epcore.ui.bossbar.objects.BossBarColor;
import com.wolfeiii.epcore.ui.bossbar.objects.BossBarDivision;
import com.wolfeiii.epcore.ui.bossbar.types.ProgressBossBar;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BossBarHandler {

    public static void newProgressBar(@NotNull BossBar bossBar, int durationSeconds, @Nullable Runnable onEndReached) {
        new ProgressBossBar(bossBar, durationSeconds, onEndReached);
    }

    public static BossBar.BossBarBuilder newBar() {
        return new BossBar.BossBarBuilder();
    }
}
