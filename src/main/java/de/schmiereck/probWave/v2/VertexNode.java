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

    public VertexNode(int q, int r, int initialStateValue, double initialProbabilityAngle, double initialVelocityAngle) {
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
    public int getQ() { return q; }
    public int getR() { return r; }
    public Point getCoords() { return new Point(q, r); }
    public int getStateValue() { return stateValue; }
    public double getProbabilityAngle() { return probabilityAngle; }
    public double getVelocityAngle() { return velocityAngle; }

    // Methods for SimulationService remain the same...
    public void setNextState(int nextVal, double nextProbAngle, double nextVelAngle) {
        this.nextStateValue = nextVal;
        this.nextProbabilityAngle = nextProbAngle;
        this.nextVelocityAngle = nextVelAngle;
    }

    public void advanceState() {
        this.stateValue = this.nextStateValue;
        this.probabilityAngle = this.nextProbabilityAngle;
        this.velocityAngle = this.nextVelocityAngle;
    }

    public static double normalizeAngle(double angle) {
        double result = angle % 360.0;
        if (result < 0) {
            result += 360.0;
        }
        return result;
    }
}
