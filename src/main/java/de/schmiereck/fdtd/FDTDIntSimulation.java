package de.schmiereck.fdtd;

import de.schmiereck.oscillation1.Holder;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 1D-FDTD-Simulation einer Welle auf einem Gitter.
 * https://chatgpt.com/c/677f8133-318c-8013-840a-a66870ad3fcc
 */
public class FDTDIntSimulation {

    // Parameter der Simulation
    static final int GRID_SIZE = 180;      // Anzahl der Zellen
    static final int MAX = 1000;          // Maximum Amplitude

    // Felder für die Zustände der Welle
    static int[] uCurrent = new int[GRID_SIZE];  // Zustand zur aktuellen Zeit t
    static int[] uPrevious = new int[GRID_SIZE]; // Zustand zur Zeit t-Δt
    static int[] uNext = new int[GRID_SIZE];     // Zustand zur Zeit t+Δt

    static int[] uRender = new int[GRID_SIZE];  // Zustand für Rendering.

    public static void main(String[] args) {
        // Initialisierung der Welle (z. B. eine Gauß-Kurve in der Mitte des Gitters)
        initializeWave();

        final JFrame frame = new JFrame("FDTD-Simulation");

        final Holder<Integer> t = new Holder<>(0);

        final FDTDIntGraphPanel panel = new FDTDIntGraphPanel(t, uRender, (int)MAX);

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
        final int calcFPS = 15*2; // Frames per second
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
                    System.arraycopy(uCurrent, 0, uRender, 0, GRID_SIZE);
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

    // Initialisiere die Welle (z. B. ein Wellenpaket in der Mitte)
    static void initializeWave() {
        initializeWave2();
    }

    static void initializeWaveX() {
        final int gs = GRID_SIZE / 2;
        {
            final int center = gs * 1;
            {
                final int uPos = center;
                uCurrent[uPos] = (int)(0.5 * MAX);
                uPrevious[uPos] = uCurrent[uPos]; // Anfangszustand gleich vorherigem Zustand
            }
            {
                final int uPos = center - 1;
                uCurrent[uPos] = (int)(0.25 * MAX);
                uPrevious[uPos] = uCurrent[uPos]; // Anfangszustand gleich vorherigem Zustand
            }
            {
                final int uPos = center + 1;
                uCurrent[uPos] = (int)(0.25 * MAX);
                uPrevious[uPos] = uCurrent[uPos]; // Anfangszustand gleich vorherigem Zustand
            }
        }
    }
    static void initializeWaveA() {
        final int gs = GRID_SIZE / 4;
        {
            final int center = gs * 1;
            final int width = gs * 1;
            initializeWave(center, width);
        }
    }

    static void initializeWave2() {
        final int gs = GRID_SIZE / 2;
        {
            final int center = gs * 1;
            final int width = gs * 1;
            initializeWave(center, width);
        }
    }

    static void initializeWave6() {
        final int gs = GRID_SIZE / 6;
        {
            final int center = gs * 2;
            final int width = gs * 2;
            initializeWave(center, width);
        }
        {
            final int center = gs * 4;
            final int width = gs * 3;
            initializeWave(center, width);
        }
    }

    private static void initializeWave(final int center, final int width) {
        final int w2 = width / 2;
        for (int pos = 0; pos < width; pos++) {
            double distance = (pos - w2) / (double)(w2 / Math.E);
            final int uPos = center + (pos - w2);
            uCurrent[uPos] = (int)Math.round(Math.exp(-distance * distance) * MAX); // Gauß-Kurve
            uPrevious[uPos] = uCurrent[uPos]; // Anfangszustand gleich vorherigem Zustand
        }
    }

    // Berechne den nächsten Zeitschritt der Welle
    static void simulateNextStep() {
        double c2 = 1;//(C * TIME_STEP / SPACE_STEP) * (C * TIME_STEP / SPACE_STEP);

        for (int i = 1; i < GRID_SIZE - 1; i++) {
            uNext[i] = (int)(2 * uCurrent[i] - uPrevious[i]
                    + c2 * (uCurrent[i + 1] - 2 * uCurrent[i] + uCurrent[i - 1]));
        }

        // Ränder: Reflexive Randbedingungen
        uNext[0] = uNext[1];
        uNext[GRID_SIZE - 1] = uNext[GRID_SIZE - 2];

        // Zustände aktualisieren
        System.arraycopy(uCurrent, 0, uPrevious, 0, GRID_SIZE);
        System.arraycopy(uNext, 0, uCurrent, 0, GRID_SIZE);
    }

    // Ausgabe der Welle im aktuellen Zeitschritt
    static void printWave(int timeStep) {
        System.out.printf("Time Step %d: ", timeStep);
        for (int i = 0; i < GRID_SIZE; i++) {
            System.out.printf("%.2f ", uCurrent[i]);
        }
        System.out.println();
    }
}
