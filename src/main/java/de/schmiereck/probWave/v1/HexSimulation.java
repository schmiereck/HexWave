package de.schmiereck.probWave.v1;

import javax.swing.*;
import java.awt.*;

public class HexSimulation {

    // --- Configuration ---
    private static final int GRID_WIDTH = 20; // Adjust as needed
    private static final int GRID_HEIGHT = 15; // Adjust as needed
    private static final int MIN_STATE_VALUE = 0;
    private static final int MAX_STATE_VALUE = 100; // Arbitrary max
    private static final long CALCULATION_DELAY_MS = 50; // Update rate for simulation logic
    private static final long RENDER_DELAY_MS = 40; // ~25 FPS target for rendering

    private volatile boolean running = true;
    private SimulationService simulationService;
    private Simulation2View simulationView;
    private Thread simulationThread;
    private Thread viewUpdateThread;


    public HexSimulation() {
        // 1. Create Grid and Service
        HexGrid grid = new HexGrid(GRID_WIDTH, GRID_HEIGHT, MIN_STATE_VALUE, MAX_STATE_VALUE);
        simulationService = new SimulationService(grid, CALCULATION_DELAY_MS);

        // 2. Create View
        simulationView = new Simulation2View();
        simulationView.setGridReference(grid); // Pass grid for size calculation

        // 3. Create Main Frame
        JFrame frame = new JFrame("Hexagonal Grid Simulation 2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(simulationView, BorderLayout.CENTER);
        frame.pack(); // Pack after adding components
        frame.setLocationRelativeTo(null); // Center window
        frame.setVisible(true);

        // Add shutdown hook to stop threads gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

    }

    public void start() {
        // 4. Start Simulation Thread
        simulationThread = new Thread(simulationService, "SimulationThread");
        simulationThread.start();

        // 5. Start View Update Thread
        viewUpdateThread = new Thread(this::viewUpdateLoop, "ViewUpdateThread");
        viewUpdateThread.start();
    }

    // Separate loop for fetching state and updating view
    private void viewUpdateLoop() {
        System.out.println("View Update Thread started.");
        while (running) {
            try {
                // Get the latest state DTO from the service
                SimulationStateDto dto = simulationService.getSimulationStateDto();

                // Update the view (repaint will be called internally)
                // No need for SwingUtilities.invokeLater if updateState just sets a variable
                // and repaint() is called, as repaint() is thread-safe.
                simulationView.updateState(dto);

                // Control the frame rate
                Thread.sleep(RENDER_DELAY_MS);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
                System.out.println("View Update Thread interrupted.");
            } catch (Exception e) {
                System.err.println("Error in view update loop: " + e.getMessage());
                e.printStackTrace();
                running = false; // Stop on error
            }
        }
        System.out.println("View Update Thread stopped.");
    }


    private void shutdown() {
        System.out.println("Shutdown requested...");
        running = false; // Signal threads to stop
        if (simulationService != null) {
            simulationService.stopSimulation();
        }

        // Wait briefly for threads to finish
        try {
            if (simulationThread != null && simulationThread.isAlive()) {
                System.out.println("Waiting for simulation thread...");
                simulationThread.interrupt(); // Interrupt sleep
                simulationThread.join(1000); // Wait max 1 second
            }
            if (viewUpdateThread != null && viewUpdateThread.isAlive()) {
                System.out.println("Waiting for view update thread...");
                viewUpdateThread.interrupt(); // Interrupt sleep
                viewUpdateThread.join(1000); // Wait max 1 second
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted during shutdown wait.");
        }
        System.out.println("Shutdown complete.");
    }

    public static void main(String[] args) {
        // Run the UI setup on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            HexSimulation sim = new HexSimulation();
            sim.start();
        });
    }
}
