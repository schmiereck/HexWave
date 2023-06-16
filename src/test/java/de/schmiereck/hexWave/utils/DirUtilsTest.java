package de.schmiereck.hexWave.utils;

import static de.schmiereck.hexWave.utils.DirUtils.calcAxisByDirNumber;
import static de.schmiereck.hexWave.utils.DirUtils.calcDirNumberByAxis;
import static org.junit.jupiter.api.Assertions.*;

import de.schmiereck.hexWave.service.hexGrid.Cell;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;

public class DirUtilsTest {

    public static final double DELTA = 0.01D;

    @org.junit.jupiter.api.Test
    public void calcDirFunc() {
        assertEquals(HexGridService.PROBABILITY_0, DirUtils.calcDirFunc(-3.0D));
        assertEquals(HexGridService.PROBABILITY_0, DirUtils.calcDirFunc(-2.0D));
        assertEquals(HexGridService.PROBABILITY_1_4, DirUtils.calcDirFunc(-1.5D));
        assertEquals(HexGridService.PROBABILITY_4_10, DirUtils.calcDirFunc(-1.2D));
        assertEquals(HexGridService.PROBABILITY_1_2, DirUtils.calcDirFunc(-1.0D));
        assertEquals(HexGridService.PROBABILITY_3_4, DirUtils.calcDirFunc(-0.5D));
        assertEquals(HexGridService.PROBABILITY_9_10, DirUtils.calcDirFunc(-0.2D));
        assertEquals(HexGridService.PROBABILITY, DirUtils.calcDirFunc(0.0D));
        assertEquals(HexGridService.PROBABILITY_9_10, DirUtils.calcDirFunc(0.2D));
        assertEquals(HexGridService.PROBABILITY_3_4, DirUtils.calcDirFunc(0.5D));
        assertEquals(HexGridService.PROBABILITY_1_2, DirUtils.calcDirFunc(1.0D));
        assertEquals(HexGridService.PROBABILITY_4_10, DirUtils.calcDirFunc(1.2D));
        assertEquals(HexGridService.PROBABILITY_1_4, DirUtils.calcDirFunc(1.5D));
        assertEquals(HexGridService.PROBABILITY_0, DirUtils.calcDirFunc(2.0D));
        assertEquals(HexGridService.PROBABILITY_0, DirUtils.calcDirFunc(3.0D));
    }

    @org.junit.jupiter.api.Test
    public void calcDirProb() {
        // Dir-Pos 0.0:
        assertEquals(HexGridService.PROBABILITY_0, DirUtils.calcDirProb(0.0D, -2), DELTA);
        assertEquals(HexGridService.PROBABILITY_1_2, DirUtils.calcDirProb(0.0D, -1), DELTA);
        assertEquals(HexGridService.PROBABILITY, DirUtils.calcDirProb(0.0D, 0), DELTA);
        assertEquals(HexGridService.PROBABILITY_1_2, DirUtils.calcDirProb(0.0D, 1), DELTA);
        assertEquals(HexGridService.PROBABILITY_0, DirUtils.calcDirProb(0.0D, 2), DELTA);
        // Dir-Pos 0.2:
        assertEquals(HexGridService.PROBABILITY_0, DirUtils.calcDirProb(0.2D, -2), DELTA);
        assertEquals(HexGridService.PROBABILITY_4_10, DirUtils.calcDirProb(0.2D, -1), DELTA);
        assertEquals(HexGridService.PROBABILITY_9_10, DirUtils.calcDirProb(0.2D, 0), DELTA);
        assertEquals(HexGridService.PROBABILITY_6_10, DirUtils.calcDirProb(0.2D, 1), DELTA);
        assertEquals(HexGridService.PROBABILITY_1_10, DirUtils.calcDirProb(0.2D, 2), DELTA);
        // Dir-Pos 0.5:
        assertEquals(HexGridService.PROBABILITY_0, DirUtils.calcDirProb(0.5D, -2), DELTA);
        assertEquals(HexGridService.PROBABILITY_1_4, DirUtils.calcDirProb(0.5D, -1), DELTA);
        assertEquals(HexGridService.PROBABILITY_3_4, DirUtils.calcDirProb(0.5D, 0), DELTA);
        assertEquals(HexGridService.PROBABILITY_3_4, DirUtils.calcDirProb(0.5D, 1), DELTA);
        assertEquals(HexGridService.PROBABILITY_1_4, DirUtils.calcDirProb(0.5D, 2), DELTA);
        // Dir-Pos -0.5:
        assertEquals(HexGridService.PROBABILITY_1_4, DirUtils.calcDirProb(-0.5D, -2), DELTA);
        assertEquals(HexGridService.PROBABILITY_3_4, DirUtils.calcDirProb(-0.5D, -1), DELTA);
        assertEquals(HexGridService.PROBABILITY_3_4, DirUtils.calcDirProb(-0.5D, 0), DELTA);
        assertEquals(HexGridService.PROBABILITY_1_4, DirUtils.calcDirProb(-0.5D, 1), DELTA);
        assertEquals(HexGridService.PROBABILITY_0, DirUtils.calcDirProb(-0.5D, 2), DELTA);
        // Dir-Pos 0.8:
        assertEquals(HexGridService.PROBABILITY_0, DirUtils.calcDirProb(0.8D, -2), DELTA);
        assertEquals(HexGridService.PROBABILITY_1_10, DirUtils.calcDirProb(0.8D, -1), DELTA);
        assertEquals(HexGridService.PROBABILITY_6_10, DirUtils.calcDirProb(0.8D, 0), DELTA);
        assertEquals(HexGridService.PROBABILITY_9_10, DirUtils.calcDirProb(0.8D, 1), DELTA);
        assertEquals(HexGridService.PROBABILITY_4_10, DirUtils.calcDirProb(0.8D, 2), DELTA);
    }

    @org.junit.jupiter.api.Test
    public void test() {
        final int startDirNumber = calcDirNumberByAxis(Cell.Dir.AP);

        assertEquals(HexGridService.PROBABILITY_0, (int)Math.round(DirUtils.calcDirProb(0.0D, -2)));
        assertEquals(HexGridService.PROBABILITY_1_2, (int)Math.round(DirUtils.calcDirProb(0.0D, -1)));
        assertEquals(HexGridService.PROBABILITY, (int)Math.round(DirUtils.calcDirProb(0.0D, 0)));
        assertEquals(HexGridService.PROBABILITY_1_2, (int)Math.round(DirUtils.calcDirProb(0.0D, 1)));
        assertEquals(HexGridService.PROBABILITY_0, (int)Math.round(DirUtils.calcDirProb(0.0D, 2)));
    }

    @org.junit.jupiter.api.Test
    public void test2() {
        final int startDirNumber = calcDirNumberByAxis(Cell.Dir.AP);

        assertEquals(Cell.Dir.BN, calcAxisByDirNumber(startDirNumber + -2));
        assertEquals(Cell.Dir.CP, calcAxisByDirNumber(startDirNumber + -1));
        assertEquals(Cell.Dir.AP, calcAxisByDirNumber(startDirNumber + 0));
        assertEquals(Cell.Dir.BP, calcAxisByDirNumber(startDirNumber + 1));
        assertEquals(Cell.Dir.CN, calcAxisByDirNumber(startDirNumber + 2));
    }
}
