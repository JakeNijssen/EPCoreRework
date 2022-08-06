package net.emeraldprison.epcore.settings;

import net.emeraldprison.epcore.EPCore;
import net.emeraldprison.epcore.database.utilities.SQLDataType;
import net.emeraldprison.epcore.database.utilities.SQLDefaultType;
import net.emeraldprison.epcore.database.utilities.TableBuilder;
import net.emeraldprison.epcore.users.object.CoreUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.util.*;

public class SettingsHandler {

    private final EPCore core;

    private final Map<String, Setting> settings = new HashMap<>();
    private final Map<UUID, LinkedHashMap<Setting, Boolean>> playerSettingValue = new HashMap<>();

    public SettingsHandler(@NotNull EPCore core) {
        this.core = core;
    }

    public boolean setup() {
        TableBuilder.newTable("settings", core.getDatabaseManager())
                .addColumn("uuid", SQLDataType.VARCHAR, 32, false, SQLDefaultType.NO_DEFAULT, true)
                .addColumn("setting", SQLDataType.VARCHAR, 100, false, SQLDefaultType.NO_DEFAULT, false)
                .addColumn("value", SQLDataType.BIT, -1, true, SQLDefaultType.NO_DEFAULT, false)
                .build();
        return true;
    }

    public Map<Setting, Boolean> retrieveSettings(UUID uuid) {
        Map<Setting, Boolean> settingsMap = new HashMap<>();

        try {
            ResultSet resultSet = EPCore.getPlugin().getDatabaseManager().getResults(
                    "settings ", "uuid=?", new HashMap<>() {{
                        put(1, uuid.toString());
                    }}
            );

            while (resultSet.next()) {
                Setting setting = settings.get(resultSet.getString("setting"));
                Boolean value = resultSet.getBoolean("value");

                settingsMap.put(setting, value);
            }

            settings.values().stream()
                    .filter(setting -> !settingsMap.containsKey(setting))
                    .forEach(setting -> {
                        settingsMap.put(setting, setting.isDefaultEnabled());
                    });
        } catch (Exception ignored) {
        }

        return settingsMap;
    }

    public void saveSettingsForUser(@NotNull CoreUser user) {
        Bukkit.getScheduler().runTaskAsynchronously(
                EPCore.getPlugin(),
                () -> {
                    settings.values().forEach(setting -> {
                        HashMap<String, Object> data = new HashMap<>() {{
                            put("uuid", user.getUuid().toString());
                            put("setting", setting.getName());
                            put("value", getValue(user.getUuid(), setting));
                        }};

                        if (!EPCore.getPlugin().getDatabaseManager().insert("settings", data)) {
                            EPCore.getPlugin().getDatabaseManager().update(
                                    "settings",
                                    new HashMap<>() {{
                                        put("value", getValue(user.getUuid(), setting));
                                    }},
                                    new HashMap<>() {{
                                        put("uuid", user.getUuid().toString());
                                        put("setting", setting.getName());
                                    }}
                            );
                        }
                    });
                }
        );
    }

    public boolean getValue(@NotNull UUID uuid, @NotNull Setting setting) {
        LinkedHashMap<Setting, Boolean> settings = playerSettingValue.get(uuid);
        return settings.getOrDefault(setting, setting.isDefaultEnabled());
    }
}
