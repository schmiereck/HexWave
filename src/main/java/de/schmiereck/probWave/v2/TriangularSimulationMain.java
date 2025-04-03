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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TriangularSimulationMain sim = new TriangularSimulationMain(); // Create TriangularSimulation
            sim.start();
        });
    }

    public TriangularSimulationMain() {
        // 1. Create Grid (TriangularGrid) and Service
        TriangularGrid grid = new TriangularGrid(GRID_WIDTH, GRID_HEIGHT, MIN_STATE_VALUE, MAX_STATE_VALUE);
        simulationService = new SimulationService(grid, CALCULATION_DELAY_MS); // Service uses the grid

        // 2. Create View (TriangularSimulationView)
        simulationView = new TriangularSimulationView();
        simulationView.setGridReference(grid); // Pass grid reference

        // 3. Create Main Frame
        JFrame frame = new JFrame("Triangular Grid Simulation"); // Updated Title
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(simulationView, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void start() {
        simulationThread = new Thread(simulationService, "SimulationThread");
        simulationThread.start();

        viewUpdateThread = new Thread(this::viewUpdateLoop, "ViewUpdateThread");
        viewUpdateThread.start();
    }

    private void viewUpdateLoop() {
        System.out.println("View Update Thread started.");
        while (running) {
            try {
                SimulationStateDto dto = simulationService.getSimulationStateDto();
                simulationView.updateState(dto); // Update the correct view
                Thread.sleep(RENDER_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
                System.out.println("View Update Thread interrupted.");
            } catch (Exception e) {
                System.err.println("Error in view update loop: " + e.getMessage());
                e.printStackTrace();
                running = false;
            }
        }
        System.out.println("View Update Thread stopped.");
    }

    private void shutdown() {
        System.out.println("Shutdown requested...");
        running = false;
        if (simulationService != null) {
            simulationService.stopSimulation();
        }
        try {
            if (simulationThread != null && simulationThread.isAlive()) {
                System.out.println("Waiting for simulation thread...");
                simulationThread.interrupt();
                simulationThread.join(1000);
            }
            if (viewUpdateThread != null && viewUpdateThread.isAlive()) {
                System.out.println("Waiting for view update thread...");
                viewUpdateThread.interrupt();
                viewUpdateThread.join(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted during shutdown wait.");
        }
        System.out.println("Shutdown complete.");
    }
}
