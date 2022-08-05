package net.emeraldprison.epcore.users.listeners;

import net.emeraldprison.epcore.EPCore;
import net.emeraldprison.epcore.users.object.CoreUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserQuitListener implements Listener {

    @EventHandler
    public void onUserQuit(PlayerQuitEvent event) {
        CoreUser coreUser = EPCore.getPlugin().getUserHandler().getUser(event.getPlayer());

        if (coreUser != null) {
            coreUser.updateDatabase(true);
        }
    }
}
