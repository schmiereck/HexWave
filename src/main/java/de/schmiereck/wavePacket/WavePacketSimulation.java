package de.schmiereck.wavePacket;

import de.schmiereck.fdtd.FDTDGraphPanel;
import de.schmiereck.oscillation1.Holder;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.locks.ReentrantLock;

public class WavePacketSimulation {

    // Parameter der Simulation
    static final int GRID_SIZE = 500;      // Anzahl der Zellen
    static final double TIME_STEP = 0.05; // Zeitschritt Δt
    static final double SPACE_STEP = 0.1; // Raumgitter Δx
    static final double C = 1.0;          // Wellenausbreitungsgeschwindigkeit
    static final double MAX = 10.0;          // Maximum Amplitude

    // Felder für die Wellenpakete (Amplitude und Phase getrennt)
    static double[] amplitude = new double[GRID_SIZE]; // Amplitude A(x, t)
    static double[] phase = new double[GRID_SIZE];     // Phase φ(x, t)

    // Felder zur Speicherung des nächsten Zustands
    static double[] amplitudeNext = new double[GRID_SIZE];
    static double[] phaseNext = new double[GRID_SIZE];

    static double[] uRender = new double[GRID_SIZE];  // Zustand für Rendering.

    public static void main(String[] args) {
        // Initialisierung der Wellenpakete
        initializeWavePacket();

        final JFrame frame = new JFrame("WavePacket-Simulation");

        final Holder<Integer> t = new Holder<>(0);

        final WavePacketGraphPanel panel = new WavePacketGraphPanel(t, uRender, MAX);

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
                        double realPart = amplitude[pos] * Math.cos(phase[pos]);
                        uRender[pos] = realPart;
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
            amplitude[i] = Math.exp(-distance * distance);

            // Lineare Phase (z. B. Welle, die nach rechts läuft)
            phase[i] = 2 * Math.PI * distance / (GRID_SIZE * SPACE_STEP);
        }
    }

    // Simuliere den nächsten Zeitschritt
    static void simulateNextStep() {
        double c2 = (C * TIME_STEP / SPACE_STEP) * (C * TIME_STEP / SPACE_STEP);

        for (int pos = 1; pos < GRID_SIZE - 1; pos++) {
            // Numerische Berechnung der Amplitude
            amplitudeNext[pos] = amplitude[pos]
                    + c2 * (amplitude[pos + 1] - 2 * amplitude[pos] + amplitude[pos - 1]);

            // Numerische Berechnung der Phase (Phasengeschwindigkeit)
            phaseNext[pos] = phase[pos]
                    + c2 * (phase[pos + 1] - 2 * phase[pos] + phase[pos - 1]);
        }

        // Ränder: Reflexive Randbedingungen
        amplitudeNext[0] = amplitudeNext[1];
        amplitudeNext[GRID_SIZE - 1] = amplitudeNext[GRID_SIZE - 2];
        phaseNext[0] = phaseNext[1];
        phaseNext[GRID_SIZE - 1] = phaseNext[GRID_SIZE - 2];

        // Zustände aktualisieren
        System.arraycopy(amplitudeNext, 0, amplitude, 0, GRID_SIZE);
        System.arraycopy(phaseNext, 0, phase, 0, GRID_SIZE);
    }

    // Ausgabe der Welle im aktuellen Zeitschritt
    static void printWave(int timeStep) {
        System.out.printf("Time Step %d:\n", timeStep);
        for (int i = 0; i < GRID_SIZE; i++) {
            double realPart = amplitude[i] * Math.cos(phase[i]);
            System.out.printf("%.2f ", realPart); // Reelle Komponente der Welle
        }
        System.out.println();
    }
}
