package de.schmiereck.field2d;

import de.schmiereck.oscillation1.AmplitudeGraphPanel;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.locks.ReentrantLock;

public class Field2DMain {

    public static void main(final String[] argArr) {
        final int maxFieldValue = 16;
        final int MaxState = maxFieldValue / 2;
        final int MaxAmplitude = MaxState * MaxState;

        final FieldArr fieldArr = new FieldArr(16);
        final FieldArrDto fieldArrDto = new FieldArrDto(fieldArr.getLength());

        final JFrame frame = new JFrame("Field-2D Graph");

        //final Field2DGraphPanel panel = new Field2DGraphPanel(fieldArrDto, maxFieldValue);
        final Field2DGraphPanel panel = new Field2DGraphPanel(fieldArrDto, MaxAmplitude);

        panel.setPreferredSize(new Dimension(800, 600));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        final int showFPS = 15; // Frames per second
        final int calcFPS = 15; // Frames per second
        final ReentrantLock lock = new ReentrantLock();

        Thread calculationThread = new Thread(() -> {
            while (true) {
                // Calculate next field values.
                calcNextFieldArrValue(fieldArr, maxFieldValue, MaxState, MaxAmplitude);
                lock.lock();
                try {
                    // Copy calculated values to render buffer.
                    fieldArrDto.copyFrom(fieldArr);
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

    private static void calcNextFieldArrValue(final FieldArr fieldArr, final int maxFieldValue, final int MaxState, final int MaxAmplitude) {
        for (final Field field : fieldArr.getFieldArr()) {
            calcNextField(field, maxFieldValue, MaxState, MaxAmplitude);
        }
    }

    private static void calcNextField(final Field field, final int maxFieldValue, final int MaxState, final int MaxAmplitude) {
        final int value = field.value++;
        final int nextValue;
        if (field.value >= maxFieldValue) {
            nextValue = -maxFieldValue;
        } else {
            nextValue = value + 1;
        }
        field.value = nextValue;
        //field.outValue = calcAmplitude2Value(nextValue, MaxState, MaxAmplitude) / 4;
        field.outValue = calcAmplitude2Value(nextValue, MaxState, MaxAmplitude);
    }

    private static int calcAmplitude2Value(final int state, final int MaxState, final int MaxAmplitude) {
        final int realState = calcReal2State(state, MaxState);
        if (state < 0) {
            return -(MaxAmplitude - (realState * realState));
        } else {
            return MaxAmplitude - (realState * realState);
        }
    }

    private static int calcReal2State(final int state, final int MaxState) {
        final int realState;
        if (state < 0) {
            if (state < -MaxState) {
                realState = state - -MaxState;
            } else {
                realState = -MaxState - state;
            }
        } else {
            if (state <= MaxState) {
                realState = MaxState - state;
            } else {
                realState = state - MaxState;
            }
        }
        return realState;
    }
}
