package de.schmiereck.probWave.v2;

import javax.swing.*;
import java.awt.*;

public class TriangularSimulationMain { // Renamed

    // --- Configuration --- (Can remain similar)
    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 15;
    private static final int MIN_STATE_VALUE = 0;
    private static final int MAX_STATE_VALUE = 100;
    private static final long CALCULATION_DELAY_MS = 50;
    private static final long RENDER_DELAY_MS = 40;

    private volatile boolean running = true;
    private SimulationService simulationService;
    private TriangularSimulationView simulationView; // Use TriangularSimulationView
    private Thread simulationThread;
    private Thread viewUpdateThread;

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            final TriangularSimulationMain sim = new TriangularSimulationMain(); // Create TriangularSimulation
            sim.start();
        });
    }

    public TriangularSimulationMain() {
        // 1. Create Grid (TriangularGrid) and Service
        final TriangularGrid grid = new TriangularGrid(GRID_WIDTH, GRID_HEIGHT, MIN_STATE_VALUE, MAX_STATE_VALUE);
        this.simulationService = new SimulationService(grid, CALCULATION_DELAY_MS); // Service uses the grid

        // 2. Create View (TriangularSimulationView)
        this.simulationView = new TriangularSimulationView(this.simulationService);
        //this.simulationView.setGridReference(grid); // Pass grid reference
        this.simulationView.revalidate();

        // 3. Create Main Frame
        final JFrame frame = new JFrame("Triangular Grid Simulation"); // Updated Title
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(this.simulationView, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void start() {
        this.simulationThread = new Thread(this.simulationService, "SimulationThread");
        this.simulationThread.start();

        this.viewUpdateThread = new Thread(this::viewUpdateLoop, "ViewUpdateThread");
        this.viewUpdateThread.start();
    }

    private void viewUpdateLoop() {
        System.out.println("View Update Thread started.");
        while (this.running) {
            try {
                final SimulationStateDto dto = simulationService.getSimulationStateDto();
                this.simulationView.updateState(dto); // Update the correct view
                Thread.sleep(RENDER_DELAY_MS);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                this.running = false;
                System.out.println("View Update Thread interrupted.");
            } catch (final Exception e) {
                System.err.println("Error in view update loop: " + e.getMessage());
                e.printStackTrace();
                this.running = false;
            }
        }
        System.out.println("View Update Thread stopped.");
    }

    private void shutdown() {
        System.out.println("Shutdown requested...");
        this.running = false;
        if (this.simulationService != null) {
            this.simulationService.stopSimulation();
        }
        try {
            if (this.simulationThread != null && this.simulationThread.isAlive()) {
                System.out.println("Waiting for simulation thread...");
                this.simulationThread.interrupt();
                this.simulationThread.join(1000);
            }
            if (this.viewUpdateThread != null && viewUpdateThread.isAlive()) {
                System.out.println("Waiting for view update thread...");
                this.viewUpdateThread.interrupt();
                this.viewUpdateThread.join(1000);
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted during shutdown wait.");
        }
        System.out.println("Shutdown complete.");
    }
}
