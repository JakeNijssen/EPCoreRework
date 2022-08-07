package net.emeraldprison.epcore.statistics.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatisticType {

    PLAYER_KILLS(
            "Most Player Killed",
            "kills"
    ),
    BLOCKS_MINED(
            "Most Blocks Mined",
            "blocks mined"
    );

    private final String name;
    private final String singleDataName;
}
