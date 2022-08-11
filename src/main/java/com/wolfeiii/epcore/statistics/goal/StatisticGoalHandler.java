package com.wolfeiii.epcore.statistics.goal;

import com.wolfeiii.epcore.configuration.serializer.styling.NameStyle;
import com.wolfeiii.epcore.database.utilities.SQLDataType;
import com.wolfeiii.epcore.database.utilities.SQLDefaultType;
import com.wolfeiii.epcore.database.utilities.TableBuilder;
import com.wolfeiii.epcore.statistics.objects.StatisticType;
import com.wolfeiii.epcore.users.object.CoreUser;
import lombok.Setter;
import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.ResultSet;
import java.util.*;

public class StatisticGoalHandler {

    private final EPCore core;
    private final Configuration goalsConfiguration;

    @Setter
    private Map<String, StatisticGoal> goals = new HashMap<>();

    public StatisticGoalHandler(@NotNull EPCore core) {
        this.core = core;
        this.goalsConfiguration = new Configuration(core, new File(core.getDataFolder(), "goals.yml"), NameStyle.HYPHEN, true);

        // Register all the goals.
        this.registerGoals();

        TableBuilder.newTable("goals", core.getDatabaseManager())
                .addColumn("uuid", SQLDataType.VARCHAR, 36, false, SQLDefaultType.NO_DEFAULT, false)
                .addColumn("completedGoals", SQLDataType.VARCHAR, 8000, false, SQLDefaultType.NO_DEFAULT, false)
                .build();
    }

    public Map<StatisticGoal, Boolean> retrieveGoals(@NotNull UUID uuid) {
        Map<StatisticGoal, Boolean> statisticGoalMap = new HashMap<>();

        try {
            ResultSet resultSet = core.getDatabaseManager().getResults(
                    "goals ", "uuid=?", new HashMap<>() {{
                        put(1, uuid.toString());
                    }}
            );

            while (resultSet.next()) {
                String[] completedGoalsRaw = resultSet.getString("completedGoals").split(";");
                Arrays.stream(completedGoalsRaw)
                        .filter(rawGoal -> goals.containsKey(rawGoal))
                        .map(rawGoal -> goals.get(rawGoal))
                        .forEach(goal -> {
                            statisticGoalMap.put(goal, true);
                        });
            }

            goals.values().stream()
                    .filter(statisticGoalMap::containsKey)
                    .forEach(notCompletedGoal -> {
                        statisticGoalMap.put(notCompletedGoal, false);
                    });
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return statisticGoalMap;
    }

    public void saveGoalsForUser(@NotNull CoreUser coreUser) {
        List<StatisticGoal> saveGoals = coreUser.getStatisticGoals().keySet()
                .stream()
                .filter(goal -> coreUser.getStatisticGoals().get(goal)).toList();


        core.getServer().getScheduler().runTaskAsynchronously(
                core,
                () -> {
                    StringBuilder saveString = new StringBuilder();

                    for (int goalIndex = 0; goalIndex < saveGoals.size(); goalIndex++) {
                        saveString.append(saveGoals.get(goalIndex));
                        if (goalIndex != saveGoals.size() - 1) {
                            saveString.append(";");
                        }
                    }

                    HashMap<String, Object> data = new HashMap<>() {{
                        put("uuid", coreUser.getUuid().toString());
                        put("completedGoals", saveString.toString());
                    }};

                    if (!core.getDatabaseManager().insert("goals", data)) {
                        core.getDatabaseManager().update(
                                "goals",

                                new HashMap<>() {{
                                    put("completedGoals", saveString.toString());
                                }},

                                new HashMap<>() {{
                                    put("uuid", coreUser.getUuid().toString());
                                }}
                        );
                    }
                });
    }

    public void registerGoals() {
        ConfigurationSection section = goalsConfiguration.getConfigurationSection("goals");
        if (section == null || section.getKeys(false).size() == 0) {
            return;
        }

        Map<String, StatisticGoal> goals = new HashMap<>();
        for (String goalName : section.getKeys(false)) {
            ConfigurationSection currentGoal = section.getConfigurationSection(goalName);

            String displayName = currentGoal.getString("display-name");
            StatisticType type = StatisticType.valueOf(currentGoal.getString("type").toUpperCase());
            double goal = currentGoal.getDouble("goal");
            List<String> rewards = currentGoal.getStringList("rewards");

            goals.put(goalName, new StatisticGoal(goalName, displayName, type, goal, rewards));
        }

        setGoals(goals);
    }
}
