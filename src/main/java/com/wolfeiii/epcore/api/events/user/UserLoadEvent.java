package com.wolfeiii.epcore.api.events.user;

import com.wolfeiii.epcore.api.events.UserEvent;
import com.wolfeiii.epcore.users.object.CoreUser;
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
