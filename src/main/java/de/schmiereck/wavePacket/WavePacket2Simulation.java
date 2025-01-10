package de.schmiereck.wavePacket;

import de.schmiereck.fdtd.FDTDGraphPanel;
import de.schmiereck.oscillation1.Holder;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.locks.ReentrantLock;

public class WavePacket2Simulation {

    // Parameter der Simulation
    static final int GRID_SIZE = 200;      // Anzahl der Zellen
    static final double TIME_STEP = 0.01; // Zeitschritt Δt
    static final double SPACE_STEP = 0.1; // Raumgitter Δx
    static final double C = 1.0;          // Wellenausbreitungsgeschwindigkeit
    static final double MAX = 10.0;          // Maximum Amplitude

    // Felder für die Wellenpakete (real und imaginär)
    static double[] realPart = new double[GRID_SIZE]; // Reelle Komponente
    static double[] imagPart = new double[GRID_SIZE]; // Imaginäre Komponente

    // Felder zur Speicherung des nächsten Zustands
    static double[] realNext = new double[GRID_SIZE];
    static double[] imagNext = new double[GRID_SIZE];

    static double[] uRender = new double[GRID_SIZE];  // Zustand für Rendering.

    public static void main(String[] args) {
        // Initialisierung der Wellenpakete
        initializeWavePacket();

        final JFrame frame = new JFrame("WavePacket 2-Simulation");

        final Holder<Integer> t = new Holder<>(0);

        final WavePacket2GraphPanel panel = new WavePacket2GraphPanel(t, uRender, MAX);

        panel.setPreferredSize(new Dimension(800, 600));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        // Hauptsimulationsschleife
//        for (int t = 0; t < 500; t++) { // 500 Zeitschritte
//            simulateNextStep();
//            printWave(t); // Welle zu jedem Zeitschritt ausgeben
//        }

        final int showFPS = 15*2; // Frames per second
        final int calcFPS = 15*20; // Frames per second
        final ReentrantLock lock = new ReentrantLock();

        final Thread calculationThread = new Thread(() -> {
            while (true) {
                // Calculate next field values.
                //calcNextFieldArrValue(fieldArr, maxFieldValue, maxOscillationFieldValue, maxAmplitude);
                simulateNextStep();
                t.value++;
                lock.lock();
                try {
                    // Copy calculated values to render buffer.
                    //fieldArrDto.copyFrom(fieldArr);

                    //System.arraycopy(uCurrent, 0, uRender, 0, GRID_SIZE);
                    for (int pos = 0; pos < GRID_SIZE; pos++) {
                        double amplitude = Math.sqrt(realPart[pos] * realPart[pos] + imagPart[pos] * imagPart[pos]);
                        uRender[pos] = amplitude;
                    }
                } finally {
                    lock.unlock();
                }
                try {
                    Thread.sleep(1000 / calcFPS);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        final Thread renderThread = new Thread(() -> {
            while (true) {
                lock.lock();
                try {
                    //printWave(t.value); // Welle zu jedem Zeitschritt ausgeben
                    panel.repaint();
                } finally {
                    lock.unlock();
                }
                try {
                    Thread.sleep(1000 / showFPS);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        calculationThread.start();
        renderThread.start();
    }

    // Initialisiere die Wellenpakete (Amplitude und Phase)
    static void initializeWavePacket() {
        int center = GRID_SIZE / 2;
        for (int i = 0; i < GRID_SIZE; i++) {
            double distance = (i - center) * SPACE_STEP;

            // Gaußförmige Amplitude
            double amplitude = Math.exp(-distance * distance);

            // Phasenverschiebung, um eine nach rechts laufende Welle zu simulieren
            double phase = 2 * Math.PI * distance / (GRID_SIZE * SPACE_STEP);

            // Initialisiere die reale und imaginäre Komponente
            realPart[i] = amplitude * Math.cos(phase);
            imagPart[i] = amplitude * Math.sin(phase);
        }
    }

    // Simuliere den nächsten Zeitschritt
    static void simulateNextStep() {
        double c2 = (C * TIME_STEP / SPACE_STEP) * (C * TIME_STEP / SPACE_STEP);

        for (int i = 1; i < GRID_SIZE - 1; i++) {
            // Update der realen Komponente
            realNext[i] = 2 * realPart[i] - realNext[i]
                    + c2 * (realPart[i + 1] - 2 * realPart[i] + realPart[i - 1]);

            // Update der imaginären Komponente
            imagNext[i] = 2 * imagPart[i] - imagNext[i]
                    + c2 * (imagPart[i + 1] - 2 * imagPart[i] + imagPart[i - 1]);
        }

        // Ränder: Reflexive Randbedingungen
        realNext[0] = realNext[1];
        realNext[GRID_SIZE - 1] = realNext[GRID_SIZE - 2];
        imagNext[0] = imagNext[1];
        imagNext[GRID_SIZE - 1] = imagNext[GRID_SIZE - 2];

        // Zustände aktualisieren
        System.arraycopy(realNext, 0, realPart, 0, GRID_SIZE);
        System.arraycopy(imagNext, 0, imagPart, 0, GRID_SIZE);
    }

    // Ausgabe der Welle im aktuellen Zeitschritt
    static void printWave(int timeStep) {
        System.out.printf("Time Step %d:\n", timeStep);
        for (int i = 0; i < GRID_SIZE; i++) {
            double amplitude = Math.sqrt(realPart[i] * realPart[i] + imagPart[i] * imagPart[i]);
            System.out.printf("%.2f ", amplitude); // Zeigt die Amplitude der Welle
        }
        System.out.println();
    }
}
