package com.wolfeiii.epcore.users.listeners;

import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.users.object.CoreUser;
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
