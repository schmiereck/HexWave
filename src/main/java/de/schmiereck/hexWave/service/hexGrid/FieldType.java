package de.schmiereck.hexWave.service.hexGrid;

public class FieldType {
    private final int maxAreaDistance;

    public FieldType(final int maxAreaDistance) {
        this.maxAreaDistance = maxAreaDistance;
    }

    public int getMaxAreaDistance() {
        return this.maxAreaDistance;
    }
}
