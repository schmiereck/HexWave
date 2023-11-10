package de.schmiereck.hexWave2.service.hexGrid;

public class GridNodeAreaRef {
    private final GridNodeArea gridNodeArea;
    private final double value;

    public GridNodeAreaRef(final GridNodeArea gridNodeArea, final double value) {
        this.gridNodeArea = gridNodeArea;
        this.value = value;
    }

    public GridNodeArea getGridNodeArea() {
        return this.gridNodeArea;
    }

    public double getValue() {
        return this.value;
    }
}
