package de.schmiereck.moveField2D;

import de.schmiereck.oscillation1.Holder;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class MoveField2DMain {

    public static void main(final String[] argArr) {
        final int maxFieldValue = 32;
        final int maxOscillationFieldValue = maxFieldValue / 2;
        final int maxAmplitude = maxOscillationFieldValue * maxOscillationFieldValue;

        final MoveFieldArr fieldArr = new MoveFieldArr(16);
        final MoveFieldArrDto fieldArrDto = new MoveFieldArrDto(fieldArr.getLength());

        final Holder<Integer> posHolder = new Holder<>(0);
        fieldArr.stream().forEach(field -> {
            posHolder.value++;

            field.field.freqCnt = 0;
            field.field.freqCntMax = 1;
            field.field.value = 0;
            field.field.outValue = 0;
            if (posHolder.value == ((fieldArr.getLength() / 4) * 1)) {
                field.moveField.freqCnt = 0;
                field.moveField.freqCntMax = 1;
                field.moveField.value = 0;
                field.moveField.outValue = 0;

                field.probability = 1;
            } else {
                if (posHolder.value == ((fieldArr.getLength() / 4) * 2)) {
                    field.moveField.freqCnt = 0;
                    field.moveField.freqCntMax = 3;
                    field.moveField.value = 0;
                    field.moveField.outValue = 0;

                    field.probability = 1;
                } else {
                    if (posHolder.value == ((fieldArr.getLength() / 4) * 3)) {
                        field.moveField.freqCnt = 0;
                        field.moveField.freqCntMax = 2;
                        field.moveField.value = 0;
                        field.moveField.outValue = 0;

                        field.probability = 1;
                    } else {
                        if (posHolder.value == ((fieldArr.getLength() / 4) * 4)) {
                            field.moveField.freqCnt = 0;
                            field.moveField.freqCntMax = -1;
                            field.moveField.value = 0;
                            field.moveField.outValue = 0;

                            field.probability = 1;
                        } else {
                            field.probability = 0;
                        }
                    }
                }
            }
        });

        final JFrame frame = new JFrame("Move Field-2D Graph");

        //final Field2DGraphPanel panel = new Field2DGraphPanel(fieldArrDto, maxFieldValue);
        final MoveField2DGraphPanel panel = new MoveField2DGraphPanel(fieldArrDto, maxAmplitude);

        panel.setPreferredSize(new Dimension(800, 600));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        final int showFPS = 15*2; // Frames per second
        final int calcFPS = 15*2; // Frames per second
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

    private static void calcNextFieldArrValue(final MoveFieldArr fieldArr, final int maxFieldValue, final int maxOscillationFieldValue, final int maxAmplitude) {
        for (final MoveField field : fieldArr.getFieldArr()) {
            calcNextField(field.field, maxFieldValue, maxOscillationFieldValue, maxAmplitude);
            calcNextField(field.moveField, maxFieldValue, maxOscillationFieldValue, maxAmplitude);
        }
        final MoveField[] sourceFieldArr = fieldArr.getFieldArr();
        final MoveField[] copyFieldArr = Arrays.copyOf(sourceFieldArr, sourceFieldArr.length);
        for (int pos = 0; pos < sourceFieldArr.length; pos++) {
            final MoveField field = sourceFieldArr[pos];

            // There is something with some probability and
            // the move-field-counter is zero?
            if ((field.probability > 0) &&
                    ((field.moveField.value == 0) && (field.moveField.freqCnt == 0)) &&
                    (field.moveField.freqCntMax != 0)) {
                // Move to right?
                if (field.moveField.freqCntMax > 0) {
                    copyFieldArr[pos] = retrieveField(sourceFieldArr, pos + 1);
                    submitField(copyFieldArr, pos + 1, field);
                    pos++;
                } else {
                    // Move to left?
                    if (field.moveField.freqCntMax < 0) {
                        copyFieldArr[pos] = sourceFieldArr[pos - 1];
                        copyFieldArr[pos - 1] = field;
                    }
                }
            } else {
                copyFieldArr[pos] = sourceFieldArr[pos];
            }
        }
        fieldArr.setFieldArr(copyFieldArr);
    }

    private static void submitField(final MoveField[] fieldArr, final int pos, final MoveField field) {
        fieldArr[calcPos(fieldArr, pos)] = field;
    }

    private static MoveField retrieveField(final MoveField[] fieldArr, final int pos) {
        return fieldArr[calcPos(fieldArr, pos)];
    }

    private static int calcPos(final MoveField[] fieldArr, final int pos) {
        final int usedPos;

        if (pos >= fieldArr.length) {
            usedPos = pos % fieldArr.length;
        } else {
            if (pos < 0) {
                usedPos = (fieldArr.length - pos) % fieldArr.length;
            } else {
                usedPos = pos;
            }
        }
        return usedPos;
    }

    private static void calcNextField(final Field field, final int maxFieldValue, final int maxOscillationFieldValue, final int maxAmplitude) {
        if (reachedFreqCntMax(field)) {
            resetFreqCnt(field);

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
            increaseFreqCnt(field);
        }
    }

    private static boolean reachedFreqCntMax(final Field field) {
        final boolean ret;
        if (field.freqCntMax > 0) {
            if (field.freqCnt >= field.freqCntMax) {
                ret = true;
            } else {
                ret = false;
            }
        } else {
            if (field.freqCntMax < 0) {
                if (field.freqCnt <= field.freqCntMax) {
                    ret = true;
                } else {
                    ret = false;
                }
            } else {
                ret = true;
            }
        }
        return ret;
    }

    private static void increaseFreqCnt(final Field field) {
        if (field.freqCntMax > 0) {
            field.freqCnt++;
        } else {
            if (field.freqCntMax < 0) {
                field.freqCnt--;
            }
        }
    }

    private static void resetFreqCnt(final Field field) {
        field.freqCnt = 0;
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
