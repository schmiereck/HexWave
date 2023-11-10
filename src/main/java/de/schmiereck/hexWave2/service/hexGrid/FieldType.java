package de.schmiereck.hexWave2.service.hexGrid;

public class FieldType {
    private final int maxAreaDistance;
    private final boolean useRefection;

    public FieldType(final int maxAreaDistance, final boolean useRefection) {
        this.maxAreaDistance = maxAreaDistance;
        this.useRefection = useRefection;
    }

    public int getMaxAreaDistance() {
        return this.maxAreaDistance;
    }

    public boolean getUseRefection() {
        return this.useRefection;
    }
}
