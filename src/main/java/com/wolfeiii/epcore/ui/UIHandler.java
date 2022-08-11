package com.wolfeiii.epcore.ui;

import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.ui.actionbar.ActionBarHandler;
import com.wolfeiii.epcore.ui.bossbar.BossBarHandler;
import com.wolfeiii.epcore.utilities.logging.LogLevel;
import lombok.Getter;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

public class UIHandler {

    private final EPCore core;

    @Getter
    private ActionBarHandler actionBarHandler;

    public UIHandler(@NotNull EPCore core) {
        this.core = core;
        this.actionBarHandler = new ActionBarHandler(core);
    }
}
