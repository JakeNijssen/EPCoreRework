package com.wolfeiii.epcore.ui.actionbar;

import com.wolfeiii.epcore.EPCore;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class ActionBarHandler {

    private final EPCore core;

    public ActionBarHandler(@NotNull EPCore core) {
        this.core = core;
    }

    public void sendActionBar(@NotNull Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    public void sendActionBar(@NotNull Player player, @NotNull String message, @NotNull Duration duration) {
        new BukkitRunnable() {
            final long targetMilliseconds = System.currentTimeMillis() + duration.toMillis();

            @Override
            public void run() {
                if (System.currentTimeMillis() >= targetMilliseconds) {
                    cancel();
                    return;
                }

                sendActionBar(player, message);
            }
        }.runTaskTimer(core, 0L, 20L);
    }
}
