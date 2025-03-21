package de.schmiereck.fdtd.cell;

import de.schmiereck.oscillation1.Holder;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.locks.ReentrantLock;

import static de.schmiereck.fdtd.cell.FDTDUtils.copyToRenderBuffer;
import static de.schmiereck.fdtd.cell.FDTDUtils.updateCurrentFromNextState;

public class FDTDCellSimulation {

    // Parameter der Simulation
    static final int GRID_SIZE = 120;      // Anzahl der Zellen
    static final int MAX = 100;          // Maximum Amplitude
    //static final int MAX = 3;          // Maximum Amplitude
    //static final int MAX = 2;          // Maximum Amplitude
    //static final int MAX = 1;          // Maximum Amplitude

    public static void main(String[] args) {
        // Felder für die Zustände der Welle
        final FDTDCell[] cellArr = new FDTDCell[GRID_SIZE];  // Zustand zur aktuellen Zeit t
        final FDTDCellDto[] uRender = new FDTDCellDto[GRID_SIZE];  // Zustand für Rendering.

        for (int pos = 0; pos < cellArr.length; pos++) {
            cellArr[pos] = new FDTDCell();
            uRender[pos] = new FDTDCellDto();
        }

        // Initialisierung der Welle (z. B. eine Gauß-Kurve in der Mitte des Gitters)
        initializeWave(cellArr);

        final JFrame frame = new JFrame("FDTD-Simulation");

        final Holder<Integer> t = new Holder<>(0);

        final FDTDCellGraphPanel panel = new FDTDCellGraphPanel(t, uRender, (int)MAX);

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
        final int calcFPS = 15*1; // Frames per second
        final ReentrantLock lock = new ReentrantLock();

        final Thread calculationThread = new Thread(() -> {
            while (true) {
                // Calculate next field values.
                //calcNextFieldArrValue(fieldArr, maxFieldValue, maxOscillationFieldValue, maxAmplitude);
                simulateNextStep(cellArr);
                t.value++;
                lock.lock();
                try {
                    // Copy calculated values to render buffer.
                    //fieldArrDto.copyFrom(fieldArr);
                    //System.arraycopy(uCurrent, 0, uRender, 0, GRID_SIZE);
                    copyToRenderBuffer(cellArr, uRender);
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
    static void initializeWave(final FDTDCell[] cellArr) {
        initializeWave2(cellArr);
        //initializeWave21(cellArr);
        //initializeWave6(cellArr);
        //initializeWaveX(cellArr);
    }

    static void initializeWaveX(final FDTDCell[] cellArr) {
        final int gs = GRID_SIZE / 2;
        {
            final int center = gs * 1;
            {
                final int uPos = center;
                cellArr[uPos].uCurrent = (int)(0.5 * MAX);
                cellArr[uPos].uPrevious = cellArr[uPos].uCurrent; // Anfangszustand gleich vorherigem Zustand
            }
            {
                final int uPos = center - 1;
                cellArr[uPos].uCurrent = (int)(0.25 * MAX);
                cellArr[uPos].uPrevious = cellArr[uPos].uCurrent; // Anfangszustand gleich vorherigem Zustand
            }
            {
                final int uPos = center + 1;
                cellArr[uPos].uCurrent = (int)(0.25 * MAX);
                cellArr[uPos].uPrevious = cellArr[uPos].uCurrent; // Anfangszustand gleich vorherigem Zustand
            }
        }
    }
    static void initializeWaveA(final FDTDCell[] cellArr) {
        final int gs = GRID_SIZE / 4;
        {
            final int center = gs * 1;
            final int width = gs * 1;
            initializeWave(cellArr, center, width);
        }
    }

    static void initializeWave2(final FDTDCell[] cellArr) {
        final int gs = GRID_SIZE / 2;
        {
            final int center = gs * 1;
            final int width = gs * 1;
            initializeWave(cellArr, center, width);
        }
    }

    static void initializeWave21(final FDTDCell[] cellArr) {
        final int gs = GRID_SIZE / 4;
        {
            final int center = gs * 1;
            final int width = gs * 2;
            initializeWave(cellArr, center, width);
        }
    }

    static void initializeWave6(final FDTDCell[] cellArr) {
        final int gs = GRID_SIZE / 6;
        {
            final int center = gs * 2;
            final int width = gs * 2;
            initializeWave(cellArr, center, width);
        }
        {
            final int center = gs * 4;
            final int width = gs * 3;
            initializeWave(cellArr, center, width);
        }
    }

    private static void initializeWave(final FDTDCell[] cellArr, final int center, final int width) {
        final int w2 = width / 2;
        for (int pos = 0; pos < width; pos++) {
            double distance = (pos - w2) / (double)(w2 / Math.E);
            final int uPos = center + (pos - w2);
            cellArr[uPos].uCurrent = (int)Math.round(Math.exp(-distance * distance) * MAX); // Gauß-Kurve
            cellArr[uPos].uPrevious = cellArr[uPos].uCurrent; // Anfangszustand gleich vorherigem Zustand
        }
    }

    // Berechne den nächsten Zeitschritt der Welle
    static void simulateNextStep(final FDTDCell[] cellArr) {
        for (int pos = 1; pos < GRID_SIZE - 1; pos++) {
            //final int current = 2 * cellArr[pos].uCurrent;
            final int current = cellArr[pos].uCurrent;
            final int currentR = cellArr[pos + 1].uCurrent;
            final int currentL = cellArr[pos - 1].uCurrent;
            final int previous = cellArr[pos].uPrevious;

            // zweite Ableitung der Wellenfunktion (approximiert durch currentR - current + currentL)
            final int currentWave = currentR - current + currentL;

            cellArr[pos].uNext =  current - previous + currentWave;
        }

        // Ränder: Reflexive Randbedingungen
        cellArr[0].uNext = cellArr[1].uNext;
        cellArr[GRID_SIZE - 1].uNext = cellArr[GRID_SIZE - 2].uNext;

        // Zustände aktualisieren
        //System.arraycopy(uCurrent, 0, uPrevious, 0, GRID_SIZE);
        //System.arraycopy(uNext, 0, uCurrent, 0, GRID_SIZE);
        updateCurrentFromNextState(cellArr);
    }

}
