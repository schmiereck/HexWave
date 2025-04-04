package de.schmiereck.probWave.v2;

import java.awt.Point;
import java.io.Serializable;
import java.util.Map;

public class SimulationStateDto implements Serializable {
    private static final long serialVersionUID = 1L; // Keep old ID or change? Let's keep for now

    public final int width;
    public final int height;
    // Array holds VertexNodeStateDto now
    public final VertexNodeStateDto[][] nodeStateArr;
    public final int minValue;
    public final int maxValue;

    /**
     * Constructor takes the updated DTO map type.
     */
    public SimulationStateDto(final int width, final int height, final VertexNodeStateDto[][] nodeStateArr, final int minValue, final int maxValue) {
        this.width = width;
        this.height = height;
        this.nodeStateArr = nodeStateArr;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}
