package de.schmiereck.probWave.v1;

import java.awt.Point;
import java.io.Serializable;

// Simple DTO to transfer immutable node state to the view
public class NodeStateDto implements Serializable {
    private static final long serialVersionUID = 1L; // For Serializable

    public final int q;
    public final int r;
    public final int stateValue;
    public final double probabilityAngle;
    public final double velocityAngle;

    public NodeStateDto(int q, int r, int stateValue, double probabilityAngle, double velocityAngle) {
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
