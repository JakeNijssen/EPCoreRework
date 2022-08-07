package net.emeraldprison.epcore.statistics.objects;

import org.jetbrains.annotations.NotNull;

public class Statistic {

    private double value;

    public Statistic(double value) {
        this.value = value;
    }

    public void overwriteValue(double newAmount) {
        this.value = newAmount;
    }

    public void increaseValue(double amount) {
        this.value += amount;
    }

    public double getValue() {
        return this.value;
    }
}
