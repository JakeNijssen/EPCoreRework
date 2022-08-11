package com.wolfeiii.epcore.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.statistics.objects.StatisticType;
import com.wolfeiii.epcore.users.object.CoreUser;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class PlaceholderHook extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "epcore";
    }

    @Override
    public @NotNull String getAuthor() {
        return "EmeraldPrison";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }


    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String placeholder) {
        // %epcore_statistic_blocks-broken% -> 1
        // %epcore_statistic_full_blocks-broken% -> 1 blocks broken
        if (placeholder.startsWith("statistic") || placeholder.startsWith("statistic_full")) {
            StatisticType statisticType = Arrays.stream(StatisticType.values())
                    .filter(type -> type.getName().replace("_", "-")
                            .equalsIgnoreCase(placeholder.substring(placeholder.lastIndexOf('_'))))
                    .findFirst()
                    .orElse(null);

            boolean fullStatistic = placeholder.startsWith("statistic_full");
            CoreUser coreUser = EPCore.getPlugin().getUserHandler().getUser(player);

            if (statisticType != null && coreUser != null) {
                return coreUser.getStatistic(statisticType).getValue() + (fullStatistic ? statisticType.getSingleDataName() : "");
            }
        }

        return null;
    }
}
