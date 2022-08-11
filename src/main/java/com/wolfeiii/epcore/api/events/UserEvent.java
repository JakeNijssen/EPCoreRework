package com.wolfeiii.epcore.api.events;

import lombok.Getter;
import com.wolfeiii.epcore.users.object.CoreUser;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Getter
    private final CoreUser user;

    public UserEvent(@NotNull CoreUser user) {
        this.user = user;
    }

    @Override @NotNull
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
