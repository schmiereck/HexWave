package de.schmiereck.hexWave2.utils;

public class MathUtils {
    public static double sigmoid(final double x) {
        return 1.0D / (1.0D + Math.exp(-x));
    }

    public static double sigmoidDerivative(final double x) {
        return x * (1.0D - x);
    }

}
