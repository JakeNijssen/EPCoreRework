package net.emeraldprison.epcore.api.events.user;

import net.emeraldprison.epcore.api.events.UserEvent;
import net.emeraldprison.epcore.users.object.CoreUser;
import org.jetbrains.annotations.NotNull;

public class UserLoadEvent extends UserEvent {

    public UserLoadEvent(@NotNull CoreUser user) {
        super(user);
    }

    public enum UserLoadType {
        NEW,
        RETRIEVED_FROM_DATABASE,
        NEW_INSTANCE
    }
}
