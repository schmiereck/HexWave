package de.schmiereck.probWave.v2;

import java.awt.Point;
import java.io.Serializable;

public class VertexNodeStateDto implements Serializable { // Renamed
    private static final long serialVersionUID = 2L; // Changed ID

    public final int q;
    public final int r;
    public final int stateValue;
    public final double probabilityAngle;
    public final double velocityAngle;

    public VertexNodeStateDto(int q, int r, int stateValue, double probabilityAngle, double velocityAngle) {
        this.q = q;
        this.r = r;
        this.stateValue = stateValue;
        this.probabilityAngle = probabilityAngle;
        this.velocityAngle = velocityAngle;
    }

    public Point getCoords() {
        return new Point(q, r);
    }
}
