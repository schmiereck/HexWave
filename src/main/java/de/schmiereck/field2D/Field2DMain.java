package de.schmiereck.field2D;

import de.schmiereck.oscillation1.Holder;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.locks.ReentrantLock;

public class Field2DMain {

    public static void main(final String[] argArr) {
        final int maxFieldValue = 32;
        final int maxOscillationFieldValue = maxFieldValue / 2;
        final int maxAmplitude = maxOscillationFieldValue * maxOscillationFieldValue;

        final FieldArr fieldArr = new FieldArr(64);
        final FieldArrDto fieldArrDto = new FieldArrDto(fieldArr.getLength());

        final Holder<Integer> posHolder = new Holder<>(0);
        fieldArr.stream().forEach(field -> {
            posHolder.value++;

            field.freqCnt = 0;
            field.freqCntMax = posHolder.value;
            field.value = 0;
            field.outValue = 0;
        });

        final JFrame frame = new JFrame("Field-2D Graph");

        //final Field2DGraphPanel panel = new Field2DGraphPanel(fieldArrDto, maxFieldValue);
        final Field2DGraphPanel panel = new Field2DGraphPanel(fieldArrDto, maxAmplitude);

        panel.setPreferredSize(new Dimension(800, 600));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        final int showFPS = 15*4; // Frames per second
        final int calcFPS = 15*4*4; // Frames per second
        final ReentrantLock lock = new ReentrantLock();

        Thread calculationThread = new Thread(() -> {
            while (true) {
                // Calculate next field values.
                calcNextFieldArrValue(fieldArr, maxFieldValue, maxOscillationFieldValue, maxAmplitude);
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

    private static void calcNextFieldArrValue(final FieldArr fieldArr, final int maxFieldValue, final int maxOscillationFieldValue, final int maxAmplitude) {
        for (final Field field : fieldArr.getFieldArr()) {
            calcNextField(field, maxFieldValue, maxOscillationFieldValue, maxAmplitude);
        }
    }

    private static void calcNextField(final Field field, final int maxFieldValue, final int maxOscillationFieldValue, final int maxAmplitude) {
        if (field.freqCnt >= field.freqCntMax) {
            field.freqCnt = 0;

            final int value = field.value++;
            final int nextValue;
            if (field.value >= maxFieldValue) {
                nextValue = -maxFieldValue;
            } else {
                nextValue = value + 1;
            }
            field.value = nextValue;
            //field.outValue = calcAmplitude2Value(nextValue, maxOscillationFieldValue, maxAmplitude) / 4;
            field.outValue = calcAmplitude2Value(nextValue, maxOscillationFieldValue, maxAmplitude);
        } else {
            field.freqCnt++;
        }
    }

    private static int calcAmplitude2Value(final int state, final int maxOscillationFieldValue, final int maxAmplitude) {
        final int realState = calcReal2State(state, maxOscillationFieldValue);
        if (state < 0) {
            return -(maxAmplitude - (realState * realState));
        } else {
            return maxAmplitude - (realState * realState);
        }
    }

    private static int calcReal2State(final int state, final int maxOscillationFieldValue) {
        final int realState;
        if (state < 0) {
            if (state < -maxOscillationFieldValue) {
                realState = state - -maxOscillationFieldValue;
            } else {
                realState = -maxOscillationFieldValue - state;
            }
        } else {
            if (state <= maxOscillationFieldValue) {
                realState = maxOscillationFieldValue - state;
            } else {
                realState = state - maxOscillationFieldValue;
            }
        }
        return realState;
    }
}
