package de.schmiereck.probWave.v2;

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
    public SimulationService(final TriangularGrid grid, final long calculationDelayMs) {
        this.grid = grid;
        this.calculationDelayMs = calculationDelayMs;
    }

    public void stopSimulation() {
        this.running = false;
    }

    @Override
    public void run() {
        System.out.println("Simulation Service started.");
        while (this.running) {
            try {
                final long startTime = System.nanoTime();
                updateGridState();
                final long endTime = System.nanoTime();
                final long durationNs = endTime - startTime;
                final long sleepTime = calculationDelayMs - (durationNs / 1_000_000);
                if (sleepTime > 0) Thread.sleep(sleepTime);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                this.running = false;
                System.out.println("Simulation Service interrupted.");
            } catch (final Exception e) {
                System.err.println("Error in simulation loop: " + e.getMessage());
                e.printStackTrace();
                this.running = false;
            }
        }
        System.out.println("Simulation Service stopped.");
    }

    private void updateGridState() {
        this.stateLock.lock();
        try {
            // Iterate over VertexNode
            for (int xPos = 0; xPos < this.grid.getWidth(); xPos++) {
                for (int yPos = 0; yPos < this.grid.getHeight(); yPos++) {
                    final VertexNode actNode = this.grid.getActNode(xPos, yPos);
                    final VertexNode nextNode = this.grid.getNextNode(xPos, yPos);
                    this.calculateNextState(actNode, nextNode);
                }
            }
        } finally {
            this.stateLock.unlock();
        }

        this.stateLock.lock();
        try {
        //    // Iterate over VertexNode
        //    for (int xPos = 0; xPos < this.grid.getWidth(); xPos++) {
        //        for (int yPos = 0; yPos < this.grid.getHeight(); yPos++) {
        //            final VertexNode node = this.grid.getActNode(xPos, yPos);
        //            node.advanceState();
        //        }
        //    }
            this.grid.setActTimePos(TriangularGrid.calcNextTimePos(this.grid.getActTimePos()));
        } finally {
            this.stateLock.unlock();
        }
    }

    // Method signature uses VertexNode now, logic remains the same
    private void calculateNextState(final VertexNode actNode, final VertexNode nextNode) {
        //List<VertexNode> neighbors = grid.getNeighbors(actNode);

        // --- State Value Calculation (Example: Simple cycle) ---
        final int nextStateValue;
        if (actNode.getStateValue() + 1 > grid.getMaxValue()) {
            nextStateValue = grid.getMinValue();
        } else {
            nextStateValue = actNode.getStateValue() + 1;
        }
        // Other physics logic (averaging, etc.) would work similarly

        // --- Angle Calculations (Example: Simple rotation) ---
        final double nextProbAngle = VertexNode.normalizeAngle(actNode.getProbabilityAngle() + PROBABILITY_ROTATION_SPEED);
        final double nextVelAngle = VertexNode.normalizeAngle(actNode.getVelocityAngle() + VELOCITY_ROTATION_SPEED);
        // Neighbor influence logic would also work similarly

        nextNode.setNextState(nextStateValue, nextProbAngle, nextVelAngle);
    }

    public SimulationStateDto getSimulationStateDto() {
        // Use VertexNodeStateDto
        final VertexNodeStateDto[][] nodeStateSnapshotArr = new VertexNodeStateDto[this.grid.getWidth()][this.grid.getHeight()];
        this.stateLock.lock();
        try {
            // Iterate over VertexNode
            for (int xPos = 0; xPos < this.grid.getWidth(); xPos++) {
                for (int yPos = 0; yPos < this.grid.getHeight(); yPos++) {
                    final VertexNode node = this.grid.getActNode(xPos, yPos);
                    nodeStateSnapshotArr[xPos][yPos] = new VertexNodeStateDto( // Create VertexNodeStateDto
                            node.getQ(),
                            node.getR(),
                            node.getStateValue(),
                            node.getProbabilityAngle(),
                            node.getVelocityAngle()
                    );
                }
            }
        } finally {
            this.stateLock.unlock();
        }
        return new SimulationStateDto(grid.getWidth(), grid.getHeight(), nodeStateSnapshotArr, grid.getMinValue(), grid.getMaxValue());
    }

    public TriangularGridDto retrieveTriangularGridDto() {
        final TriangularGridDto triangularGridDto = new TriangularGridDto();
        triangularGridDto.setWidth(this.grid.getWidth());
        triangularGridDto.setHeight(this.grid.getHeight());
        return triangularGridDto;
    }
}
