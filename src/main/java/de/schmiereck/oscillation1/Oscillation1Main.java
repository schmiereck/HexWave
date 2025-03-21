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
    public static int MaxOscillationState = 12;
    public static int MaxState = MaxOscillationState * 2;
    public static int MaxAmplitude = MaxOscillationState * MaxOscillationState;

    public static void main(final String[] args) {
        final OPart oPart = new OPart();
        oPart.state = 0;

        final List<Integer> amplitudeStateValueList = new ArrayList<>();
        final List<Integer> amplitudeSinusValueList = new ArrayList<>();
        final List<Integer> amplitudeCoshValueList = new ArrayList<>();
        final List<Integer> amplitudeSinhApproxValueList = new ArrayList<>();
        final List<Integer> amplitudeSpeedValueList = new ArrayList<>();
        final List<Integer> amplitudeOscillatorValueList = new ArrayList<>();

        for (int i = 0; i < (MaxState * 4 + 1); i++) {
            final double xx = ((double) oPart.state) / MaxState;
            final int amplitudeSinusValue = (int)(Math.sin(xx * Math.PI) * MaxAmplitude);

            // https://slideplayer.com/slide/5289327/
            // https://en.wikipedia.org/wiki/Hyperbolic_functions
            // cosh(x) = (e pow(x) + (e pow(-x)) / 2
            //final int amplitudeCoshValue = (int)
            //        (
            //        (
            //                Math.pow(Math.E, (xx * Math.PI)) +
            //                Math.pow(Math.E, -(xx * Math.PI))
            //        ) / 2
            //        );
            final int amplitudeCoshValue = (int)
                    (((
                    (
                            //Math.pow(Math.E, (xx * Math.PI * 1.8D) +
                            //Math.pow(Math.E, -(xx * Math.PI * 1.8D)
                            Math.pow(Math.E, xx) +
                            Math.pow(Math.E, -xx)
                    ) / 2
                    ) - 1) * MaxAmplitude);
            //double x = xx * Math.PI;
            //// Bhaskara-I-Formel für 0 bis π
            //double absX = Math.abs(x);
            //double sign = (x < 0) ? -1.0 : 1.0;
            //final int amplitudeSinhApproxValue = (int)
            //        (
            //        (
            //                sign * (16.0 * absX * (Math.PI - absX)) / (5.0 * Math.PI * Math.PI - 4.0 * absX * (Math.PI - absX))
            //        ) * MaxAmplitude
            //        );
            final int amplitudeSinhApproxValue = (int)
                    (((
                    (
                            Math.pow(Math.E, xx * Math.PI) -
                            Math.pow(Math.E, -xx * Math.PI)
                    ) / 2
                    )) * 12);
            final int amplitudeSpeedValue = calcAmplitudeValue(oPart.state);
            final int realSpeedState = calcRealState(oPart.state);
            final int amplitudeOscillatorValue = calcAmplitude2Value(oPart.state); // Parabel
            final int realParabelState = calcReal2State(oPart.state);
            amplitudeStateValueList.add(oPart.state);
            amplitudeSinusValueList.add(amplitudeSinusValue);
            amplitudeCoshValueList.add(amplitudeCoshValue);
            amplitudeSinhApproxValueList.add(amplitudeSinhApproxValue);
            amplitudeSpeedValueList.add(amplitudeSpeedValue);
            amplitudeOscillatorValueList.add(amplitudeOscillatorValue);
            System.out.printf("i:%d \t- state:%3d,  " +
                            "\t- real-stateSpeed:%3d, \tamplitudeSpeed:%4d, " +
                            "\t- real-stateOscillator:%3d, \tamplitudeOscillator:%4d, " +
                            " \tSinus:%4d" +
                            " \tCosh:%4d" +
                            " \tSinhApprox:%4d" +
                            "%n",
                    i, oPart.state,
                    realSpeedState, amplitudeSpeedValue,
                    realParabelState, amplitudeOscillatorValue,
                    amplitudeSinusValue,
                    amplitudeCoshValue,
                    amplitudeSinhApproxValue);
            calcNextState(oPart);
        }

        final JFrame frame = new JFrame("Oscillator Amplitude Graph");

        final List<AmplitudeGraph> amplitudeGraphList = new ArrayList<>();

        amplitudeGraphList.add(new AmplitudeGraph("State", Color.BLACK, amplitudeStateValueList));
        amplitudeGraphList.add(new AmplitudeGraph("Sinus", Color.GRAY, amplitudeSinusValueList));
        amplitudeGraphList.add(new AmplitudeGraph("Cosh", Color.ORANGE, amplitudeCoshValueList));
        amplitudeGraphList.add(new AmplitudeGraph("SinhApprox", Color.PINK, amplitudeSinhApproxValueList));
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
            if (state < -MaxOscillationState) {
                realState = -MaxState - state;
            } else {
                realState = state;
            }
        } else {
            if (state <= MaxOscillationState) {
                realState = state;
            } else {
                realState = MaxState - state;
            }
        }
        return realState;
    }

    private static int calcReal2State(final int state) {
        final int realState;
        if (state < 0) {
            if (state < -MaxOscillationState) {
                realState = state - -MaxOscillationState;
            } else {
                realState = -MaxOscillationState - state;
            }
        } else {
            if (state <= MaxOscillationState) {
                realState = MaxOscillationState - state;
            } else {
                realState = state - MaxOscillationState;
            }
        }
        return realState;
    }

    private static void calcNextState(final OPart oPart) {
        if (oPart.state >= MaxState) {
            oPart.state = -MaxState;
        }
        oPart.state++;
    }
}
