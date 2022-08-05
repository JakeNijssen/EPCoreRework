package net.emeraldprison.epcore.api.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Getter
    private final Player player;

    public PlayerEvent(@NotNull Player player) {
        this.player = player;
    }

    @Override @NotNull
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
