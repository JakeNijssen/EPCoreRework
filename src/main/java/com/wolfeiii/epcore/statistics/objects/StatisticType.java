package com.wolfeiii.epcore.statistics.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatisticType {

    PLAYER_KILLS(
            "Most Players Killed",
            "kills"
    ),
    DEATHS(
            "Most Deaths",
            "deaths"
    ),
    BLOCKS_MINED(
            "Most Blocks Mined",
            "blocks mined"
    );

    private final String name;
    private final String singleDataName;
}
