package de.schmiereck.probWave.v2;

import java.awt.Point;

public class VertexNode { // Renamed from HexNode
    private final int q;
    private final int r;

    private int stateValue;
    private double probabilityAngle;
    private double velocityAngle;

    private int nextStateValue;
    private double nextProbabilityAngle;
    private double nextVelocityAngle;

    public VertexNode(final int q, final int r, final int initialStateValue, final double initialProbabilityAngle, final double initialVelocityAngle) {
        this.q = q;
        this.r = r;
        this.stateValue = initialStateValue;
        this.probabilityAngle = initialProbabilityAngle;
        this.velocityAngle = initialVelocityAngle;
        this.nextStateValue = initialStateValue;
        this.nextProbabilityAngle = initialProbabilityAngle;
        this.nextVelocityAngle = initialVelocityAngle;
    }

    // Getters remain the same...
    public int getQ() { return this.q; }
    public int getR() { return this.r; }
    public int getStateValue() { return this.stateValue; }
    public double getProbabilityAngle() { return this.probabilityAngle; }
    public double getVelocityAngle() { return this.velocityAngle; }

    // Methods for SimulationService remain the same...
    public void setNextState(final int nextVal, final double nextProbAngle, final double nextVelAngle) {
        this.nextStateValue = nextVal;
        this.nextProbabilityAngle = nextProbAngle;
        this.nextVelocityAngle = nextVelAngle;
    }

    public void advanceState() {
        this.stateValue = this.nextStateValue;
        this.probabilityAngle = this.nextProbabilityAngle;
        this.velocityAngle = this.nextVelocityAngle;
    }

    public static double normalizeAngle(final double angle) {
        double result = angle % 360.0;
        if (result < 0) {
            result += 360.0;
        }
        return result;
    }
}
