package de.schmiereck.oscillation1;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * see: https://codebase64.org/doku.php?id=base:generating_approximate_sines_in_assembly
 */
public class Oscillation1Main {
    //public static int MaxState = 3;
    public static int MaxState = 12;
    public static int Max2State = MaxState * 2;
    public static int MaxAmplitude = MaxState * MaxState;

    public static void main(final String[] args) {
        final OPart oPart = new OPart();
        oPart.state = 0;

        final List<Integer> amplitudeStateValueList = new ArrayList<>();
        final List<Integer> amplitudeSinusValueList = new ArrayList<>();
        final List<Integer> amplitudeSpeedValueList = new ArrayList<>();
        final List<Integer> amplitudeOscillatorValueList = new ArrayList<>();

        for (int i = 0; i < (Max2State * 4 + 1); i++) {
            final int amplitudeSinusValue = (int)(Math.sin((double) oPart.state / Max2State * Math.PI) * MaxAmplitude);
            final int amplitudeSpeedValue = calcAmplitudeValue(oPart.state);
            final int realSpeedState = calcRealState(oPart.state);
            final int amplitudeOscillatorValue = calcAmplitude2Value(oPart.state); // Parabel
            final int realParabelState = calcReal2State(oPart.state);
            amplitudeStateValueList.add(oPart.state);
            amplitudeSinusValueList.add(amplitudeSinusValue);
            amplitudeSpeedValueList.add(amplitudeSpeedValue);
            amplitudeOscillatorValueList.add(amplitudeOscillatorValue);
            System.out.printf("i:%d \t- state:%3d,  " +
                            "\t- real-stateSpeed:%3d, \tamplitudeSpeed:%4d, " +
                            "\t- real-stateOscillator:%3d, \tamplitudeOscillator:%4d, " +
                            " \tamplitudeSinus:%4d" +
                            "%n",
                    i, oPart.state,
                    realSpeedState, amplitudeSpeedValue,
                    realParabelState, amplitudeOscillatorValue,
                    amplitudeSinusValue);
            calcNextState(oPart);
        }

        final JFrame frame = new JFrame("Oscillator Amplitude Graph");

        final List<AmplitudeGraph> amplitudeGraphList = new ArrayList<>();

        amplitudeGraphList.add(new AmplitudeGraph("State", Color.BLACK, amplitudeStateValueList));
        amplitudeGraphList.add(new AmplitudeGraph("Sinus", Color.GRAY, amplitudeSinusValueList));
        amplitudeGraphList.add(new AmplitudeGraph("Speed", Color.GREEN, amplitudeSpeedValueList));
        amplitudeGraphList.add(new AmplitudeGraph("Oscillator", Color.BLUE, amplitudeOscillatorValueList));

        final AmplitudeGraphPanel panel = new AmplitudeGraphPanel(amplitudeGraphList);

        panel.setPreferredSize(new Dimension(800, 600));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static int calcAmplitudeValue(final int state) {
        if (state < 0) {
            final int realState = calcRealState(state);
            return -realState * realState;
        } else {
            final int realState = calcRealState(state);
            return realState * realState;
        }
    }

    private static int calcAmplitude2Value(final int state) {
        if (state < 0) {
            final int realState = calcReal2State(state);
            return -(MaxAmplitude - (realState * realState));
        } else {
            final int realState = calcReal2State(state);
            return MaxAmplitude - (realState * realState);
        }
    }

    private static int calcRealState(final int state) {
        final int realState;
        if (state < 0) {
            if (state < -MaxState) {
                realState = -Max2State - state;
            } else {
                realState = state;
            }
        } else {
            if (state <= MaxState) {
                realState = state;
            } else {
                realState = Max2State - state;
            }
        }
        return realState;
    }

    private static int calcReal2State(final int state) {
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

    private static void calcNextState(final OPart oPart) {
        if (oPart.state >= Max2State) {
            oPart.state = -Max2State;
        }
        oPart.state++;
    }
}
