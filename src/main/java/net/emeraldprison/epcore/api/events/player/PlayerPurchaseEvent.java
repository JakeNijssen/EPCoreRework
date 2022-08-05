package net.emeraldprison.epcore.api.events.player;

import lombok.Getter;
import net.emeraldprison.epcore.api.events.PlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class PlayerPurchaseEvent extends PlayerEvent implements Cancellable {

    @Getter
    private final double amount;
    private boolean cancelled;

    public PlayerPurchaseEvent(@NotNull Player player, double amount) {
        super(player);
        this.amount = amount;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
