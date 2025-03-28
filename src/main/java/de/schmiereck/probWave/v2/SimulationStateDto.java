package de.schmiereck.probWave.v2;

import java.awt.Point;
import java.io.Serializable;
import java.util.Map;

public class SimulationStateDto implements Serializable {
    private static final long serialVersionUID = 1L; // Keep old ID or change? Let's keep for now

    // Map holds VertexNodeStateDto now
    public final Map<Point, VertexNodeStateDto> nodeStates;
    public final int minValue;
    public final int maxValue;

    // Constructor takes the updated DTO map type
    public SimulationStateDto(Map<Point, VertexNodeStateDto> nodeStates, int minValue, int maxValue) {
        this.nodeStates = nodeStates;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}
