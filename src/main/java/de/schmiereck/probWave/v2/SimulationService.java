package de.schmiereck.probWave.v2;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimulationService implements Runnable {

    private final TriangularGrid grid; // Use TriangularGrid
    private volatile boolean running = true;
    private final long calculationDelayMs;

    // Physics constants remain the same
    private static final double PROBABILITY_ROTATION_SPEED = 2.0;
    private static final double VELOCITY_ROTATION_SPEED = -1.5;
    private static final double NEIGHBOR_INFLUENCE_FACTOR = 0.1;

    private final Lock stateLock = new ReentrantLock();

    // Constructor takes TriangularGrid
    public SimulationService(TriangularGrid grid, long calculationDelayMs) {
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
                if (sleepTime > 0) Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
                System.out.println("Simulation Service interrupted.");
            } catch (Exception e) {
                System.err.println("Error in simulation loop: " + e.getMessage());
                e.printStackTrace();
                running = false;
            }
        }
        System.out.println("Simulation Service stopped.");
    }

    private void updateGridState() {
        stateLock.lock();
        try {
            // Iterate over VertexNode
            for (VertexNode node : grid.getAllNodes()) {
                calculateNextState(node);
            }
        } finally {
            stateLock.unlock();
        }

        stateLock.lock();
        try {
            // Iterate over VertexNode
            for (VertexNode node : grid.getAllNodes()) {
                node.advanceState();
            }
        } finally {
            stateLock.unlock();
        }
    }

    // Method signature uses VertexNode now, logic remains the same
    private void calculateNextState(VertexNode node) {
        List<VertexNode> neighbors = grid.getNeighbors(node);

        // --- State Value Calculation (Example: Simple cycle) ---
        int nextStateValue = node.getStateValue() + 1;
        if (nextStateValue > grid.getMaxValue()) {
            nextStateValue = grid.getMinValue();
        }
        // Other physics logic (averaging, etc.) would work similarly

        // --- Angle Calculations (Example: Simple rotation) ---
        double nextProbAngle = VertexNode.normalizeAngle(node.getProbabilityAngle() + PROBABILITY_ROTATION_SPEED);
        double nextVelAngle = VertexNode.normalizeAngle(node.getVelocityAngle() + VELOCITY_ROTATION_SPEED);
        // Neighbor influence logic would also work similarly

        node.setNextState(nextStateValue, nextProbAngle, nextVelAngle);
    }

    public SimulationStateDto getSimulationStateDto() {
        // Use VertexNodeStateDto
        Map<Point, VertexNodeStateDto> nodeStatesSnapshot = new HashMap<>();
        stateLock.lock();
        try {
            // Iterate over VertexNode
            for (VertexNode node : grid.getAllNodes()) {
                nodeStatesSnapshot.put(node.getCoords(),
                        new VertexNodeStateDto( // Create VertexNodeStateDto
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
