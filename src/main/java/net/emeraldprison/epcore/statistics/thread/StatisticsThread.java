package net.emeraldprison.epcore.statistics.thread;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.emeraldprison.epcore.EPCore;
import net.emeraldprison.epcore.statistics.StatisticsHandler;

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
