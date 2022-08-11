package com.wolfeiii.epcore.statistics.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.statistics.objects.StatisticType;
import com.wolfeiii.epcore.users.object.CoreUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class StatisticGoal {

    private final String name;
    private final String displayName;
    private final StatisticType type;
    private final double goal;
    private List<String> rewards;

    public boolean canComplete(@NotNull UUID uuid) {
        CoreUser coreUser = EPCore.getPlugin().getUserHandler().getUser(uuid);
        if (coreUser == null) {
            return false;
        }

        double currentValue = coreUser.getStatistic(type).getValue();
        boolean hasAlreadyReached = coreUser.hasCompleted(this);

        return currentValue >= goal && !hasAlreadyReached;
    }

    public void execute(@NotNull Player player) {
        this.rewards.forEach(reward -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward
                    .replace("%player%", player.getName())
                    .replace("%uuid%", player.getUniqueId().toString())
                    .replace("%goal%", "" + getGoal())
                    .replace("%display-name%", getDisplayName()));
        });
    }
}
