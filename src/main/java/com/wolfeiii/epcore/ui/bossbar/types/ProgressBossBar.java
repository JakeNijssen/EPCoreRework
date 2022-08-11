package com.wolfeiii.epcore.ui.bossbar.types;

import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.ui.bossbar.objects.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.function.Consumer;

public class ProgressBossBar {

    private final BossBar bossBar;
    private final BukkitTask updateTask;

    private String originalTitle;
    private double additionPerSecond;

    public ProgressBossBar(@NotNull BossBar bossBar, int seconds, @Nullable Runnable onEndReached) {
        this.bossBar = bossBar;
        this.originalTitle = bossBar.title();
        this.updateTask = new BukkitRunnable() {

            int currentSeconds = seconds + 1;

            @Override
            public void run() {
                currentSeconds--;
                bossBar.progress(currentSeconds / (float) seconds);
                bossBar.title(originalTitle.replace("%seconds%", String.valueOf(currentSeconds)));

                if (currentSeconds <= 0) {
                    cancel();

                    bossBar.remove();
                    if (onEndReached != null) {
                        onEndReached.run();
                    }
                }
            }
        }.runTaskTimer(EPCore.getPlugin(), 0L, 20L);
    }
}
