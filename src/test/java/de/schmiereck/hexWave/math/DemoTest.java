package de.schmiereck.hexWave.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DemoTest {

    @Test
    public void test() {
        // Teilen heißt mit dem Kehrwert multiplizieren.
        //
        // (a/b) / c = a/b * 1/c = a/(b×c)
        // (1/2) / 2 = 1/2 * 1/2 = 1/(2×2) =

        Assertions.assertEquals(((1.0D / 2.0D) / 2.0D), 1.0D / (2.0D * 2.0D));
        Assertions.assertEquals((((1.0D / 2.0D) / 2.0D) / 2.0D), 1.0D / (2.0D * 2.0D * 2.0D));

        Assertions.assertEquals(((1.0D / 23.0D) / 23.0D), 1.0D / (23.0D * 23.0D));
        Assertions.assertEquals((((1.0D / 42.0D) / 42.0D) / 42.0D), 1.0D / (42.0D * 42.0D * 42.0D));
    }
}
