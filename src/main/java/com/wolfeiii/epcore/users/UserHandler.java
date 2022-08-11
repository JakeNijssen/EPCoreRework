package com.wolfeiii.epcore.users;

import lombok.Getter;
import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.api.events.user.UserLoadEvent;
import com.wolfeiii.epcore.settings.Setting;
import com.wolfeiii.epcore.users.object.CoreUser;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Getter
public class UserHandler {

    private final EPCore core;
    private final Map<UUID, CoreUser> users = new HashMap<>();

    public UserHandler(@NotNull EPCore core) {
        this.core = core;

    }

    public Collection<CoreUser> getOnlineUsers() {
        return this.users.values();
    }

    public CoreUser getFakeUser(@NotNull String name) {
        CoreUser coreUser = getUser(name, false);
        if (coreUser != null)
            return coreUser;

        return createCoreUser(loadFromUsername(name), false);
    }

    public CoreUser getUser(@NotNull UUID uuid) {
        return getUser(uuid, false);
    }

    public CoreUser getUser(@NotNull Player player) {
        return getUser(player.getUniqueId(), false);
    }

    public CoreUser getUser(@NotNull String name, boolean database) {
        for (CoreUser user : users.values()) {
            if (user.getName().equalsIgnoreCase(name)) {
                return user;
            }
        }

        // No online user found, should we fetch from database?
        if (database)
            return createCoreUser(loadFromUsername(name), true);

        return null;
    }

    public CoreUser getUser(@NotNull UUID uuid, boolean database) {
        for (CoreUser user : users.values()) {
            if (user.getUuid().equals(uuid)) {
                return user;
            }
        }

        // No online user found, should we fetch from database?
        if (database)
            return createCoreUser(loadFromUUID(uuid), true);

        return null;
    }

    public ResultSet loadFromUsername(@NotNull String name) {
        try {
            return EPCore.getPlugin().getDatabaseManager().getResults(
                    "users ", "name=?", new HashMap<>(){{
                        put(1, name);
                    }}
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }


    public ResultSet loadFromUUID(@NotNull UUID uuid) {
        try {
            return EPCore.getPlugin().getDatabaseManager().getResults(
                    "users ", "uuid=?", new HashMap<>(){{
                        put(1, uuid);
                    }}
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public CoreUser createCoreUser(@Nullable ResultSet resultSet, boolean save) {
        if (resultSet == null) {
            return null;
        }

        try {
            if (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));

                Map<Setting, Boolean> settings = EPCore.getPlugin().getSettingsHandler()
                        .retrieveSettings(uuid);

                CoreUser coreUser = new CoreUser(
                        uuid,
                        resultSet.getString("name"),
                        UserLoadEvent.UserLoadType.RETRIEVED_FROM_DATABASE,
                        save
                );

                coreUser.setSettings(settings);
                return coreUser;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }

        return null;
    }

    public void updateUserTable(@NotNull Player player, @NotNull CoreUser coreUser) {
        // Run task async because we send a SQL request :)
        getCore().getServer().getScheduler().runTaskAsynchronously(
                EPCore.getPlugin(),
                () -> {
                    HashMap<String, Object> data = new HashMap<>() {{
                        put("uuid", coreUser.getUuid().toString());
                        put("name", player.getName());
                    }};

                    if (!getCore().getDatabaseManager().insert("users", data)) {
                        // We couldn't insert a new CoreUser, most likely because it already exists, so we update that instead.
                        getCore().getDatabaseManager().update(
                                "users",
                                data,
                                new HashMap<>() {{
                                    put("uuid", coreUser.getUuid().toString());
                                }}
                        );
                    }
                }
        );
    }
}
