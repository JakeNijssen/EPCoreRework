package net.emeraldprison.epcore.statistics;

import net.emeraldprison.epcore.EPCore;
import net.emeraldprison.epcore.database.utilities.SQLDataType;
import net.emeraldprison.epcore.database.utilities.SQLDefaultType;
import net.emeraldprison.epcore.database.utilities.TableBuilder;
import net.emeraldprison.epcore.statistics.objects.Statistic;
import net.emeraldprison.epcore.statistics.objects.StatisticType;
import net.emeraldprison.epcore.users.object.CoreUser;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.util.*;

public class StatisticsHandler {

    private final EPCore core;

    private final Map<StatisticType, LinkedHashMap<String, Double>> globalStatistics = new HashMap<>();

    public StatisticsHandler(@NotNull EPCore core) {
        this.core = core;
    }

    public boolean setup() {
        new TableBuilder("statistics", core.getDatabaseManager())
                .addColumn("uuid", SQLDataType.VARCHAR, 36, false, SQLDefaultType.NO_DEFAULT, false)
                .addColumn("type", SQLDataType.VARCHAR, 100, false, SQLDefaultType.NO_DEFAULT, false)
                .addColumn("data", SQLDataType.DOUBLE, -1, false, SQLDefaultType.CUSTOM.setCustom(0), false)
                .setConstraints("uuid", "type")
                .build();
        return true;
    }

    public void updateGlobalStats(@NotNull CoreUser user) {
        Arrays.stream(StatisticType.values()).forEach(statisticType -> {
            LinkedHashMap<String, Double> map = new LinkedHashMap<>();

            ResultSet resultSet = core.getDatabaseManager().executeQuery(
                    "SELECT users.name, users.uuid, stats.value FROM ep_statistics AS stats " +
                            "LEFT JOIN ep_users AS users ON stats.uuid = users.uuid " +
                            "WHERE stats.type = '" + statisticType.toString().toUpperCase() + "' " +
                            "ORDER BY data DESC LIMIT 10"
            );

            if (resultSet != null) {
                try {
                    while (resultSet.next()) {
                        String username = resultSet.getString("name");
                        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                        double value = resultSet.getDouble("value");

                        CoreUser targetUser = core.getUserHandler().getUser(uuid);
                        if (targetUser != null) {
                            value = targetUser.getStatistic(statisticType).getValue();
                        }

                        if (value <= 0) {
                            continue;
                        }

                        map.put(username, value);
                    }

                    LinkedHashMap<String, Double> reverseSortedMap = new LinkedHashMap<>();

                    map.entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

                    this.globalStatistics.put(statisticType, reverseSortedMap);
                } catch (Exception ignored) {
                }
            }
        });
    }

    public Map<StatisticType, Statistic> retrieveStatistics(@NotNull UUID uuid) {
        Map<StatisticType, Statistic> statisticMap = new HashMap<>();

        try {
            ResultSet resultSet = core.getDatabaseManager().getResults(
                    "statistics ", "uuid=?", new HashMap<>() {{
                        put(1, uuid.toString());
                    }}
            );

            while (resultSet.next()) {
                StatisticType statisticType = StatisticType.valueOf(resultSet.getString("type").toUpperCase());
                double value = resultSet.getDouble("value");

                statisticMap.put(statisticType, new Statistic(value));
            }

            Arrays.stream(StatisticType.values())
                    .filter(statisticType -> !statisticMap.containsKey(statisticType))
                    .forEach(statisticType -> {
                        statisticMap.put(statisticType, new Statistic(0D));
                    });
        } catch (Exception ignored) {
            // We should just ignore the exception.
        }

        return statisticMap;
    }

    public void saveStatisticsForUser(@NotNull CoreUser coreUser) {
        core.getServer().getScheduler().runTaskAsynchronously(
                core,
                () -> coreUser.getStatistics()
                        .forEach((statisticType, statistic) -> {
                            if (statistic.getValue() > 0) {
                                HashMap<String, Object> data = new HashMap<>() {{
                                    put("uuid", coreUser.getUuid().toString());
                                    put("type", statisticType.toString().toUpperCase());
                                    put("value", statistic.getValue());
                                }};

                                if (!core.getDatabaseManager().insert("statistics", data)) {
                                    core.getDatabaseManager().update(
                                            "statistics",

                                            // Update Data
                                            new HashMap<>() {{
                                                put("value", statistic.getValue());
                                            }},

                                            // Where Data
                                            new HashMap<>() {{
                                                put("uuid", coreUser.getUuid().toString());
                                                put("type", statisticType.toString().toUpperCase());
                                            }}
                                    );
                                }
                            }
                        })
        );
    }
}
