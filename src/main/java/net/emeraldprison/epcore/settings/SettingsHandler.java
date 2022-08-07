package net.emeraldprison.epcore.settings;

import net.emeraldprison.epcore.EPCore;
import net.emeraldprison.epcore.configuration.Configuration;
import net.emeraldprison.epcore.configuration.serializer.styling.NameStyle;
import net.emeraldprison.epcore.database.utilities.SQLDataType;
import net.emeraldprison.epcore.database.utilities.SQLDefaultType;
import net.emeraldprison.epcore.database.utilities.TableBuilder;
import net.emeraldprison.epcore.users.object.CoreUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.ResultSet;
import java.util.*;

public class SettingsHandler {

    private final EPCore core;

    private final Map<String, Setting> settings = new HashMap<>();
    private final Map<UUID, LinkedHashMap<Setting, Boolean>> playerSettingValue = new HashMap<>();

    private final Configuration settingsConfiguration;

    public SettingsHandler(@NotNull EPCore core) {
        this.core = core;
        this.settingsConfiguration = new Configuration(core, new File(core.getDataFolder(), "settings.yml"), NameStyle.HYPHEN, true);
    }

    public boolean setup() {
        TableBuilder.newTable("settings", core.getDatabaseManager())
                .addColumn("uuid", SQLDataType.VARCHAR, 36, false, SQLDefaultType.NO_DEFAULT, true)
                .addColumn("setting", SQLDataType.VARCHAR, 100, false, SQLDefaultType.NO_DEFAULT, false)
                .addColumn("value", SQLDataType.INTEGER, -1, true, SQLDefaultType.NO_DEFAULT, false)
                .build();

        loadSettings();
        return true;
    }

    public void loadSettings() {
        ConfigurationSection section = settingsConfiguration.getConfigurationSection("settings");
        if (section == null) {
            throw new RuntimeException("Tried to load settings from settings.yml, but the section 'settings' could not be found.");
        }

        List<Setting> settings = new ArrayList<>();
        for (String configSetting : section.getKeys(false)) {
            ConfigurationSection settingSection = section.getConfigurationSection(configSetting);
            if (settingSection == null)
                continue;

            settings.add(settingsConfiguration.get("settings." + configSetting, Setting.class));
        }

        settings.forEach(setting -> this.settings.put(setting.getName(), setting));
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
                    user.getSettings().keySet().forEach(setting -> {
                        HashMap<String, Object> data = new HashMap<>() {{
                            put("uuid", user.getUuid().toString());
                            put("setting", setting.getName());
                            put("value", getValue(user, setting));
                        }};

                        if (!EPCore.getPlugin().getDatabaseManager().insert("settings", data)) {
                            EPCore.getPlugin().getDatabaseManager().update(
                                    "settings",
                                    new HashMap<>() {{
                                        put("value", getValue(user, setting));
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

    public boolean getValue(@NotNull CoreUser user, @NotNull Setting setting) {
        return user.getSettings().getOrDefault(setting, setting.isDefaultEnabled());
    }

    public boolean getValue(@NotNull CoreUser user, @NotNull String settingName) {
        Setting setting = settings.get(settingName);
        if (setting != null) {
            return user.getSettings().get(setting);
        }

        return false;
    }

    public void setValue(@NotNull CoreUser user, @NotNull Setting setting, boolean newValue) {
        user.getSettings().put(setting, newValue);
    }
}
