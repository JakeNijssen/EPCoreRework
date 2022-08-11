package com.wolfeiii.epcore.statistics.thread;

import lombok.RequiredArgsConstructor;
import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.statistics.StatisticsHandler;

import java.util.ArrayList;

@RequiredArgsConstructor
public class StatisticsThread implements Runnable {

    private final StatisticsHandler statisticsHandler;

    @Override
    public void run() {
        new ArrayList<>(EPCore.getPlugin().getUserHandler().getOnlineUsers()).forEach(
                this.statisticsHandler::updateGlobalStats);
    }
}
