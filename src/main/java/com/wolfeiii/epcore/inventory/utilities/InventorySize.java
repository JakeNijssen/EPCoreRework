package com.wolfeiii.epcore.inventory.utilities;

import lombok.Getter;

public enum InventorySize {

    ONE_ROW(9),
    TWO_ROWS(18),
    THREE_ROWS(27),
    FOUR_ROWS(36),
    FIVE_ROWS(45),
    SIX_ROWS(56);

    @Getter
    private final int slots;

    InventorySize(int slots) {
        this.slots = slots;
    }
}
