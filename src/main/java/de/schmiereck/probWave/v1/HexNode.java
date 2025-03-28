package de.schmiereck.probWave.v1;

import java.awt.Point; // Using Point for axial coordinates (q, r)

public class HexNode {
    // Axial coordinates
    private final int q;
    private final int r;

    // State properties
    private int stateValue; // "Schwingung"
    private double probabilityAngle; // 0-359 degrees
    private double velocityAngle; // 0-359 degrees

    // Temporary storage for next state (to avoid race conditions during update)
    private int nextStateValue;
    private double nextProbabilityAngle;
    private double nextVelocityAngle;

    public HexNode(int q, int r, int initialStateValue, double initialProbabilityAngle, double initialVelocityAngle) {
        this.q = q;
        this.r = r;
        this.stateValue = initialStateValue;
        this.probabilityAngle = initialProbabilityAngle;
        this.velocityAngle = initialVelocityAngle;
        // Initialize next state as current state
        this.nextStateValue = initialStateValue;
        this.nextProbabilityAngle = initialProbabilityAngle;
        this.nextVelocityAngle = initialVelocityAngle;
    }

    // Getters
    public int getQ() { return q; }
    public int getR() { return r; }
    public Point getCoords() { return new Point(q, r); }
    public int getStateValue() { return stateValue; }
    public double getProbabilityAngle() { return probabilityAngle; }
    public double getVelocityAngle() { return velocityAngle; }

    // --- Methods for SimulationService ---

    // Store the calculated next state
    public void setNextState(int nextVal, double nextProbAngle, double nextVelAngle) {
        this.nextStateValue = nextVal;
        this.nextProbabilityAngle = nextProbAngle;
        this.nextVelocityAngle = nextVelAngle;
    }

    // Apply the calculated next state
    public void advanceState() {
        this.stateValue = this.nextStateValue;
        this.probabilityAngle = this.nextProbabilityAngle;
        this.velocityAngle = this.nextVelocityAngle;
    }

    // Ensure angle stays within 0-359.99...
    public static double normalizeAngle(double angle) {
        double result = angle % 360.0;
        if (result < 0) {
            result += 360.0;
        }
        return result;
    }
}
