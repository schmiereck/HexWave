package de.schmiereck.hexWave.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * <code><pre>
 *    		1   	2   	3   	4
 * 0	0
 * 1	0,5	0,25	0,25	0,375	0,75
 * 2	1	0,5 	0,5 	0,75	1,5
 * 3	1,5	0,75	0,75	1,125	2,25
 * 4	2	1   	1   	1,5  	3
 * </pre></code>
 */
class Test_NumService_WHEN_calcNumber_is_called {

    @Test
    void calcNumber() {
        NumService numService = new NumService(100);

        Num num = numService.createNum();

        numService.divNum(num, 100);
        numService.divNum(num, 70);
        numService.divNum(num, 5);

        final double number = numService.calcNumber(num);

        assertEquals((100.0D) * (70.0D / 100.0D) * (5.0D / 100.0D), number, 0.01D);
    }

    @Test
    void calcNumberD2N0() {
        NumService numService = new NumService(2);

        Num num = numService.createNum();

        numService.divNum(num, 0);

        final double number = numService.calcNumber(num);

        assertEquals(2.0D * (0.0D / 2.0D), number, 0.01D);  // 0.0D
        assertEquals(0.0D, number, 0.01D);
    }

    @Test
    void calcNumberD2N1N1N1() {
        NumService numService = new NumService(2);

        Num num = numService.createNum();

        numService.divNum(num, 1);  // 1.0D
        numService.divNum(num, 1);  // 0.5D
        numService.divNum(num, 1);  // 0.25D

        final double number = numService.calcNumber(num);

        assertEquals(2.0D * (1.0D / 2.0D) * (1.0D / 2.0D) * (1.0D / 2.0D), number, 0.01D);  // 0.25D
        assertEquals(0.25D, number, 0.01D);
    }

    @Test
    void calcNumberD2N1N1() {
        NumService numService = new NumService(2);

        Num num = numService.createNum();

        numService.divNum(num, 1);  // 1.0D
        numService.divNum(num, 1);  // 0.5D

        final double number = numService.calcNumber(num);

        assertEquals(2.0D * (1.0D / 2.0D) * (1.0D / 2.0D), number, 0.01D);  // 0.5D
        assertEquals(0.5D, number, 0.01D);
    }

    @Test
    void calcNumberD2N1N1N3() {
        NumService numService = new NumService(2);

        Num num = numService.createNum();

        numService.divNum(num, 1);  // 1.0D
        numService.divNum(num, 1);  // 0.5D
        numService.divNum(num, 3);  // 0.75D

        final double number = numService.calcNumber(num);

        assertEquals(2.0D * (1.0D / 2.0D) * (1.0D / 2.0D) * (3.0D / 2.0D), number, 0.01D);  // 0.75D
        assertEquals(0.75D, number, 0.01D);
    }

    @Test
    void calcNumberD2N1() {
        NumService numService = new NumService(2);

        Num num = numService.createNum();

        numService.divNum(num, 1);

        final double number = numService.calcNumber(num);

        assertEquals(2.0D * (1.0D / 2.0D), number, 0.01D);  // 1.0D
        assertEquals(1.0D, number, 0.01D);
    }
    /*
    @Test
    void calcNumberD2N1N3x() {
        NumService numService = new NumService(2);

        Num num = numService.createNum();

        numService.divNum(num, 6);
        numService.divNum(num, 2);
        numService.divNum(num, 2);

        final double number = numService.calcNumber(num);

        assertEquals(2.0D * (6.0D / 2.0D) * (2.0D / 2.0D) * (2.0D / 2.0D) * (2.0D / 2.0D), number, 0.01D);  // 1.25D
        assertEquals(1.25D, number, 0.01D);
    }
    */

    @Test
    void calcNumberD2N1N3() {
        NumService numService = new NumService(2);

        Num num = numService.createNum();

        numService.divNum(num, 1);  // 1.0D
        numService.divNum(num, 3);  // 1.5D

        final double number = numService.calcNumber(num);

        assertEquals(2.0D * (1.0D / 2.0D) * (3.0D / 2.0D), number, 0.01D);  // 1.5D
        assertEquals(1.5D, number, 0.01D);
    }

    /*
    @Test
    void calcNumberD2N1m7() {
        NumService numService = new NumService(2);

        Num num = numService.createNum();

        numService.divNum(num, 4);
        numService.divNum(num, 4);
        numService.divNum(num, 4);
        numService.divNum(num, 4);
        numService.divNum(num, 4);
        numService.divNum(num, 4);
        numService.divNum(num, 4);

        final double number = numService.calcNumber(num);

        //assertEquals(2.0D * (1.0D / 2.0D) * (3.0D / 2.0D), number, 0.01D);  // 1.75D
        assertEquals(1.75D, number, 0.01D);
    }
    */

    @Test
    void calcNumberD2N2() {
        NumService numService = new NumService(2);

        Num num = numService.createNum();

        numService.divNum(num, 2);  // 2.0D

        final double number = numService.calcNumber(num);

        assertEquals(2.0D * (2.0D / 2.0D), number, 0.01D);  // 2.0D
        assertEquals(2.0D, number, 0.01D);
    }
}