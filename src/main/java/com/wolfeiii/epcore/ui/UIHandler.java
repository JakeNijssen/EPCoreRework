package com.wolfeiii.epcore.ui;

import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.ui.actionbar.ActionBarHandler;
import com.wolfeiii.epcore.ui.bossbar.BossBarHandler;
import com.wolfeiii.epcore.utilities.logging.LogLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class UIHandler {

    private final EPCore core;

    @Getter
    private ActionBarHandler actionBarHandler;

    public UIHandler(@NotNull EPCore core) {
        this.core = core;

        initializeActionBarHandler();
    }

    public void initializeActionBarHandler() {
        try {
            actionBarHandler = new ActionBarHandler(core);
        } catch (IllegalAccessException exception) {
            core.getCoreLogger().log(LogLevel.WARN, exception.getMessage());
            actionBarHandler = null;
        }
    }
}
