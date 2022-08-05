package net.emeraldprison.epcore.users.listeners;

import net.emeraldprison.epcore.EPCore;
import net.emeraldprison.epcore.api.events.user.UserLoadEvent;
import net.emeraldprison.epcore.users.UserHandler;
import net.emeraldprison.epcore.users.object.CoreUser;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class UserJoinListener implements Listener {

    @EventHandler
    public void onUserPreJoin(AsyncPlayerPreLoginEvent event) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(
                EPCore.getPlugin(),
                () -> {
                    UserHandler userHandler = EPCore.getPlugin().getUserHandler();
                    CoreUser coreUser = userHandler.getUser(event.getUniqueId());

                    if (coreUser == null) {
                        // User is null, which means the player is not currently online, so we fetch it from the database.
                        // This code should always be executed, but is a safety if something goes wrong, so we don't get two of the same user.
                        coreUser = userHandler.createCoreUser(userHandler.loadFromUUID(event.getUniqueId()), true);
                        if (coreUser == null) {
                            // User is still null, which means it doesn't exist in the database, so it's a new player connecting.
                            coreUser = new CoreUser(
                                    event.getUniqueId(),
                                    event.getName(),
                                    UserLoadEvent.UserLoadType.NEW
                            );

                            CoreUser finalUser = coreUser;
                            Bukkit.getScheduler().runTask(EPCore.getPlugin(), () -> {
                                UserLoadEvent userLoadEvent = new UserLoadEvent(finalUser);
                                EPCore.getPlugin().getServer().getPluginManager().callEvent(userLoadEvent);
                            });
                        } else {
                            CoreUser finalUser = coreUser;
                            Bukkit.getScheduler().runTask(EPCore.getPlugin(), () -> {
                                UserLoadEvent userLoadEvent = new UserLoadEvent(finalUser);
                                EPCore.getPlugin().getServer().getPluginManager().callEvent(userLoadEvent);
                            });
                        }
                    }
                }
        );
    }
}
