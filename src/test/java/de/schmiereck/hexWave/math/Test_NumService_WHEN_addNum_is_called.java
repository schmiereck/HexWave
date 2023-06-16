package de.schmiereck.hexWave.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class Test_NumService_WHEN_addNum_is_called {

    @Test
    void GIVEN_D2_0p5_plus_1p0_THEN_result_is_1p5() {
        NumService numService = new NumService(2);

        Num aNum = numService.createNum();  // 0.5D
        // 1   1          1
        // - * - = 0,25 = -
        // 2   2          4

        numService.divNum(aNum, 1);
        numService.divNum(aNum, 1);

        assertEquals(2.0D * (1.0D / 2.0D) * (1.0D / 2.0D), numService.calcNumber(aNum), 0.01D);  // 0.5D

        Num bNum = numService.createNum();  // 1.0D
        // 1         1
        // - = 0,5 = -
        // 2         2

        numService.divNum(bNum, 1);

        assertEquals(2.0D * (1.0D / 2.0D), numService.calcNumber(bNum), 0.01D);  // 1.0D

        final boolean added = numService.addNum(aNum, bNum);

        // 1   2   3   1   3
        // - + - = - = - * - = 0,75
        // 4   4   4   2   2
        final double number = numService.calcNumber(aNum);

        assertTrue(added);
        assertEquals(2.0D * (1.0D / 2.0D) * (3.0D / 2.0D), number, 0.01D);  // 1.5D
        assertEquals(1.5D, number, 0.01D);
    }

    @Test
    void GIVEN_D2_0p5_plus_0p25_THEN_result_is_0p75() {
        NumService numService = new NumService(2);

        Num aNum = numService.createNum();  // 0.5D
        // 1   1          1
        // - * - = 0,25 = -
        // 2   2          4

        numService.divNum(aNum, 1);
        numService.divNum(aNum, 1);

        assertEquals(2.0D * (1.0D / 2.0D) * (1.0D / 2.0D), numService.calcNumber(aNum), 0.01D);  // 0.5D
        assertEquals(0.5D, numService.calcNumber(aNum), 0.01D);

        Num bNum = numService.createNum();  // 0.25D
        // 1   1   1           1
        // - * - * - = 0,125 = -
        // 2   2   2           8

        numService.divNum(bNum, 1);
        numService.divNum(bNum, 1);
        numService.divNum(bNum, 1);

        assertEquals(2.0D * (1.0D / 2.0D) * (1.0D / 2.0D) * (1.0D / 2.0D), numService.calcNumber(bNum), 0.01D);  // 0.25D
        assertEquals(0.25D, numService.calcNumber(bNum), 0.01D);

        final boolean added = numService.addNum(aNum, bNum);

        // 2   1   3   1   1   3
        // - + - = - = - * - * - = 0,375
        // 8   8   8   2   2   2
        final double number = numService.calcNumber(aNum);

        assertTrue(added);
        assertEquals(2.0D * (1.0D / 2.0D) * (1.0D / 2.0D) * (3.0D / 2.0D), number, 0.01D);  // 0.75D
        assertEquals(0.75D, number, 0.01D);
    }

    @Test
    void GIVEN_D2_0p5_plus_1p5_THEN_result_is_2p0() {
        NumService numService = new NumService(2);

        Num aNum = numService.createNum();  // 0.5D
        // 1   1          1
        // - * - = 0,25 = -
        // 2   2          4

        numService.divNum(aNum, 1);
        numService.divNum(aNum, 1);

        assertEquals(2.0D * (1.0D / 2.0D) * (1.0D / 2.0D), numService.calcNumber(aNum), 0.01D);  // 0.5D
        assertEquals(0.5D, numService.calcNumber(aNum), 0.01D);

        Num bNum = numService.createNum();  // 1.5D
        // 1   3          3
        // - * - = 0,75 = -
        // 2   2          4

        numService.divNum(bNum, 1);
        numService.divNum(bNum, 3);

        assertEquals(2.0D * (1.0D / 2.0D) * (3.0D / 2.0D), numService.calcNumber(bNum), 0.01D);  // 1.5D
        assertEquals(1.5D, numService.calcNumber(bNum), 0.01D);

        final boolean added = numService.addNum(aNum, bNum);

        // 1   3   4
        // - + - = - = 1,00
        // 4   4   4
        final double number = numService.calcNumber(aNum);

        assertTrue(added);
        assertEquals(2.0D * (2.0D / 2.0D), number, 0.01D);  // 2.0D
        assertEquals(2.0D, number, 0.01D);
    }

    @Test
    void GIVEN_D2_0p25_plus_1p5_THEN_result_is_1p75() {
        NumService numService = new NumService(2);

        Num aNum = numService.createNum();  // 0.25D
        // 1   1   1           1
        // - * - * - = 0,125 = -
        // 2   2   2           8

        numService.divNum(aNum, 1);
        numService.divNum(aNum, 1);
        numService.divNum(aNum, 1);

        assertEquals(2.0D * (1.0D / 2.0D) * (1.0D / 2.0D) * (1.0D / 2.0D), numService.calcNumber(aNum), 0.01D);  // 0.25D
        assertEquals(0.25D, numService.calcNumber(aNum), 0.01D);

        Num bNum = numService.createNum();  // 1.5D
        // 1   3          3
        // - * - = 0,75 = -
        // 2   2          4

        numService.divNum(bNum, 1);
        numService.divNum(bNum, 3);

        assertEquals(2.0D * (1.0D / 2.0D) * (3.0D / 2.0D), numService.calcNumber(bNum), 0.01D);  // 1.5D
        assertEquals(1.5D, numService.calcNumber(bNum), 0.01D);

        final boolean added = numService.addNum(aNum, bNum);

        // 1   1   1   1   3    1   6   7
        // - * - * - + - * - =  - + - = - = 0,875
        // 2   2   2   2   2    8   8   8
        final double number = numService.calcNumber(aNum);

        assertFalse(added);
    }
}
