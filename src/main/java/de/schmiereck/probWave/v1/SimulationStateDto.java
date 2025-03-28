package de.schmiereck.probWave.v1;

import java.awt.Point;
import java.io.Serializable;
import java.util.Map;

// Contains a snapshot of the simulation state for the view
public class SimulationStateDto implements Serializable {
    private static final long serialVersionUID = 1L;

    public final Map<Point, NodeStateDto> nodeStates; // Map: Axial Coords -> Node State
    public final int minValue;
    public final int maxValue;

    public SimulationStateDto(Map<Point, NodeStateDto> nodeStates, int minValue, int maxValue) {
        this.nodeStates = nodeStates; // Should be an immutable copy or used carefully
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}
