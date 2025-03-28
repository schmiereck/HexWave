package de.schmiereck.probWave.v1;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimulationService implements Runnable {

    private final HexGrid grid;
    private volatile boolean running = true;
    private final long calculationDelayMs;

    // Configuration for physics (adjust these!)
    private static final double PROBABILITY_ROTATION_SPEED = 2.0; // degrees per step
    private static final double VELOCITY_ROTATION_SPEED = -1.5; // degrees per step
    private static final double NEIGHBOR_INFLUENCE_FACTOR = 0.1; // How much neighbors affect angles

    // Lock for safely creating the DTO
    private final Lock stateLock = new ReentrantLock();

    public SimulationService(HexGrid grid, long calculationDelayMs) {
        this.grid = grid;
        this.calculationDelayMs = calculationDelayMs;
    }

    public void stopSimulation() {
        running = false;
    }

    @Override
    public void run() {
        System.out.println("Simulation Service started.");
        while (running) {
            try {
                long startTime = System.nanoTime();

                updateGridState();

                long endTime = System.nanoTime();
                long durationNs = endTime - startTime;
                long sleepTime = calculationDelayMs - (durationNs / 1_000_000);

                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
                running = false; // Stop if interrupted
                System.out.println("Simulation Service interrupted.");
            } catch (Exception e) {
                System.err.println("Error in simulation loop: " + e.getMessage());
                e.printStackTrace();
                running = false; // Stop on other errors
            }
        }
        System.out.println("Simulation Service stopped.");
    }

    private void updateGridState() {
        // Phase 1: Calculate next state for all nodes based on current state + neighbors
        stateLock.lock(); // Lock during calculation phase if needed, depends on complexity
        try {
            for (HexNode node : grid.getAllNodes()) {
                calculateNextState(node);
            }
        } finally {
            stateLock.unlock();
        }


        // Phase 2: Apply the calculated next state to all nodes
        stateLock.lock(); // Lock while applying updates and creating DTO
        try {
            for (HexNode node : grid.getAllNodes()) {
                node.advanceState();
            }
        } finally {
            stateLock.unlock();
        }
    }

    // *** THIS IS THE CORE PHYSICS LOGIC - CUSTOMIZE IT! ***
    private void calculateNextState(HexNode node) {
        List<HexNode> neighbors = grid.getNeighbors(node);

        // --- State Value Calculation ---
        // Example 1: Simple cyclic increment
        int nextStateValue = node.getStateValue() + 1;
        if (nextStateValue > grid.getMaxValue()) {
            nextStateValue = grid.getMinValue();
        }

        // Example 2: Average of neighbors (more interactive)
        /*
        if (!neighbors.isEmpty()) {
            double avgValue = 0;
            for(HexNode neighbor : neighbors) {
                avgValue += neighbor.getStateValue();
            }
            avgValue /= neighbors.size();
            // Simple influence: move slightly towards average
             int diff = (int)Math.round(avgValue) - node.getStateValue();
             nextStateValue = node.getStateValue() + (int)Math.signum(diff); // Move 1 step towards avg
             // Clamp value
             nextStateValue = Math.max(grid.getMinValue(), Math.min(grid.getMaxValue(), nextStateValue));

        } else {
             // No neighbors? Keep current value or simple cycle
             nextStateValue = node.getStateValue() + 1;
             if (nextStateValue > grid.getMaxValue()) {
                 nextStateValue = grid.getMinValue();
             }
        }
        */


        // --- Probability Angle Calculation ---
        // Example 1: Simple constant rotation
        double nextProbAngle = HexNode.normalizeAngle(node.getProbabilityAngle() + PROBABILITY_ROTATION_SPEED);

        // Example 2: Rotation influenced by neighbor angle differences (simple diffusion idea)
        /*
        double avgNeighborProbAngle = 0;
        int validNeighbors = 0;
        if (!neighbors.isEmpty()) {
             // Simple angle averaging is tricky due to wrap-around (0/360)
             // Convert angles to vectors, average vectors, convert back to angle
             double avgX = 0, avgY = 0;
             for(HexNode neighbor : neighbors) {
                 avgX += Math.cos(Math.toRadians(neighbor.getProbabilityAngle()));
                 avgY += Math.sin(Math.toRadians(neighbor.getProbabilityAngle()));
             }
             avgX /= neighbors.size();
             avgY /= neighbors.size();
             double avgAngleRad = Math.atan2(avgY, avgX);
             avgNeighborProbAngle = Math.toDegrees(avgAngleRad);

             double angleDifference = HexNode.normalizeAngle(avgNeighborProbAngle - node.getProbabilityAngle());
             // Adjust difference to be in [-180, 180] for influence direction
             if (angleDifference > 180) angleDifference -= 360;
             if (angleDifference < -180) angleDifference += 360;

             nextProbAngle = HexNode.normalizeAngle(node.getProbabilityAngle()
                                                   + PROBABILITY_ROTATION_SPEED // Base rotation
                                                   + angleDifference * NEIGHBOR_INFLUENCE_FACTOR); // Neighbor influence
        } else {
             nextProbAngle = HexNode.normalizeAngle(node.getProbabilityAngle() + PROBABILITY_ROTATION_SPEED);
        }
        */


        // --- Velocity Angle Calculation ---
        // Example 1: Simple constant rotation (can be different speed/direction)
        double nextVelAngle = HexNode.normalizeAngle(node.getVelocityAngle() + VELOCITY_ROTATION_SPEED);

        // Example 2: Similar influenced rotation as probability angle (can use different factor)
        /*
        double avgNeighborVelAngle = 0;
         // ... (Calculate avgNeighborVelAngle similar to probability angle) ...
         if (!neighbors.isEmpty()) {
            // ... calculate avgNeighborVelAngle ...
             double angleDifference = HexNode.normalizeAngle(avgNeighborVelAngle - node.getVelocityAngle());
             if (angleDifference > 180) angleDifference -= 360;
             if (angleDifference < -180) angleDifference += 360;

             nextVelAngle = HexNode.normalizeAngle(node.getVelocityAngle()
                                                 + VELOCITY_ROTATION_SPEED // Base rotation
                                                 + angleDifference * NEIGHBOR_INFLUENCE_FACTOR); // Neighbor influence
         } else {
              nextVelAngle = HexNode.normalizeAngle(node.getVelocityAngle() + VELOCITY_ROTATION_SPEED);
         }
         */

        // Store the calculated next state in the node
        node.setNextState(nextStateValue, nextProbAngle, nextVelAngle);
    }

    // Method for the View thread to get the current state safely
    public SimulationStateDto getSimulationStateDto() {
        Map<Point, NodeStateDto> nodeStatesSnapshot = new HashMap<>();
        stateLock.lock(); // Ensure consistent state while copying
        try {
            for (HexNode node : grid.getAllNodes()) {
                nodeStatesSnapshot.put(node.getCoords(),
                        new NodeStateDto(
                                node.getQ(),
                                node.getR(),
                                node.getStateValue(),
                                node.getProbabilityAngle(),
                                node.getVelocityAngle()
                        )
                );
            }
        } finally {
            stateLock.unlock();
        }
        return new SimulationStateDto(nodeStatesSnapshot, grid.getMinValue(), grid.getMaxValue());
    }
}
