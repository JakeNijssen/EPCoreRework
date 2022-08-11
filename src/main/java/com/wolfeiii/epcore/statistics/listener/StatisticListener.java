package com.wolfeiii.epcore.statistics.listener;

import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.statistics.objects.StatisticType;
import com.wolfeiii.epcore.users.object.CoreUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class StatisticListener implements Listener {

    @EventHandler
    public void onPlayerDeath(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) && !(event.getDamager() instanceof Player)) {
            return;
        }

        CoreUser damaged = EPCore.getPlugin().getUserHandler().getUser((Player) event.getEntity());
        CoreUser killer = EPCore.getPlugin().getUserHandler().getUser((Player) event.getDamager());

        if (damaged == null || killer == null || event.isCancelled()) {
            return;
        }

        if (damaged.getPlayer().getHealth() - event.getFinalDamage() <= 0.0) {
            // Player dies because of this.
            damaged.getStatistic(StatisticType.DEATHS).increaseValue(1);
            killer.getStatistic(StatisticType.PLAYER_KILLS).increaseValue(1);
        }
    }

    @EventHandler
    public void onBlockMine(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        CoreUser coreUser = EPCore.getPlugin().getUserHandler().getUser(event.getPlayer());
        coreUser.getStatistic(StatisticType.BLOCKS_MINED).increaseValue(1);
    }
}
