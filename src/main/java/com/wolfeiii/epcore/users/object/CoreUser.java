package com.wolfeiii.epcore.users.object;

import com.wolfeiii.epcore.api.events.user.UserLoadEvent;
import com.wolfeiii.epcore.statistics.goal.StatisticGoal;
import com.wolfeiii.epcore.statistics.objects.Statistic;
import com.wolfeiii.epcore.statistics.objects.StatisticType;
import lombok.Getter;
import lombok.Setter;
import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.settings.Setting;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

@Getter
public class CoreUser {

    private final UUID uuid;
    private final String name;

    private boolean loaded;
    private boolean ableToLoad;

    @Setter
    private Map<Setting, Boolean> settings;

    @Setter
    private Map<StatisticType, Statistic> statistics;

    @Setter
    private Map<StatisticGoal, Boolean> statisticGoals;

    private UserLoadEvent.UserLoadType loadType;

    public CoreUser(@NotNull UUID uuid, @NotNull String name, UserLoadEvent.UserLoadType loadType, boolean save) {
        this.uuid = uuid;
        this.name = name;
        this.loadType = loadType;

        // Save user to the user list.
        if (save) EPCore.getPlugin().getUserHandler().getUsers().put(uuid, this);
    }

    public CoreUser(@NotNull UUID uuid, @NotNull String name, @NotNull UserLoadEvent.UserLoadType loadType) {
        this(uuid, name, loadType, true);
    }

    public CoreUser(@NotNull UUID uuid, @NotNull String name) {
        this(uuid, name, UserLoadEvent.UserLoadType.NEW_INSTANCE, true);
    }

    public void updateDatabase(boolean quitEvent) {
        if (!quitEvent && isOnline()) {
            // Player is online, and they didn't recently quit, so we shouldn't modify anything.
            return;
        }

        EPCore.getPlugin().getStatisticsHandler().getGoalHandler().saveGoalsForUser(this);
        EPCore.getPlugin().getSettingsHandler().saveSettingsForUser(this);
        EPCore.getPlugin().getStatisticsHandler().saveStatisticsForUser(this);
        EPCore.getPlugin().getUserHandler().updateUserTable(getPlayer(), this);
        EPCore.getPlugin().getUserHandler().getUsers().remove(uuid);
    }

    public void delete(){
        if (getPlayer() != null && getPlayer().isOnline()){
            return;
        }

        // TODO: Remove also from database?
        EPCore.getPlugin().getUserHandler().getUsers().remove(getUuid());
    }

    public Statistic getStatistic(StatisticType statisticType){
        return this.statistics.get(statisticType);
    }

    public boolean hasCompleted(@NotNull StatisticGoal goal) {
        return statisticGoals.get(goal);
    }

    private boolean isOnline() {
        return getPlayer() != null && getPlayer().isOnline();
    }

    public Player getPlayer() {
        return Bukkit.getServer().getPlayer(uuid);
    }

}
