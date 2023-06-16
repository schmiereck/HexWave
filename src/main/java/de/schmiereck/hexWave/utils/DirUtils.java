package de.schmiereck.hexWave.utils;

import de.schmiereck.hexWave.service.hexGrid.Cell;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;

public class DirUtils {

    private static final Cell.Dir[] OppositeDirArr = new Cell.Dir[Cell.Dir.values().length];
    static {
        //!np OppositeDirArr[Cell.Dir.NP.ordinal()] = Cell.Dir.NP;
        OppositeDirArr[Cell.Dir.AN.ordinal()] = Cell.Dir.AP;
        OppositeDirArr[Cell.Dir.BN.ordinal()] = Cell.Dir.BP;
        OppositeDirArr[Cell.Dir.CN.ordinal()] = Cell.Dir.CP;
        OppositeDirArr[Cell.Dir.AP.ordinal()] = Cell.Dir.AN;
        OppositeDirArr[Cell.Dir.BP.ordinal()] = Cell.Dir.BN;
        OppositeDirArr[Cell.Dir.CP.ordinal()] = Cell.Dir.CN;
    }

    public static Cell.Dir calcOppositeDir(final Cell.Dir dir) {
        return OppositeDirArr[dir.ordinal()];
    }

    public static double calcDirProb(final double dir, final int axis) {
        if (dir >= 0.0D) {
            return switch (axis) {
                case -2 -> calcDirFunc(-2.0D - dir);
                case -1 -> calcDirFunc(-1.0D - dir);
                case 0 -> calcDirFunc(dir);
                case 1 -> calcDirFunc(1.0D - dir);
                case 2 -> calcDirFunc(2.0D - dir);
                default -> throw new IllegalStateException("Unexpected axis value: " + axis);
            };
        } else {
            return switch (axis) {
                case -2 -> calcDirFunc(-2.0D - dir);
                case -1 -> calcDirFunc(-1.0D - dir);
                case 0 -> calcDirFunc(-dir);
                case 1 -> calcDirFunc(1.0D - dir);
                case 2 -> calcDirFunc(2.0D - dir);
                default -> throw new IllegalStateException("Unexpected axis value: " + axis);
            };
        }
    }

    /**
     * <pre><code>
     *          *
     *        . | .
     *      .   |   .
     *    .     |     .
     *  +---+---+---+---+---+
     * -2  -1   0   1   2   3
     * </code></pre>
     */
    public static double calcDirFunc(final double dir) {
        if ((dir > -2.0D) && (dir < 2.0D)) {
            if (dir >= 0.0D) {
                return ((2.0D - dir) / 2.0D) * HexGridService.PROBABILITY;
            } else {
                return ((2.0D + dir) / 2.0D) * HexGridService.PROBABILITY;
            }
        } else {
            return 0.0D;
        }
    }

    /**
     *  4   bn  cp  5
     *       \ /
     * 3 an---A---ap 0
     *       / \
     *  2   cn  bp  1
     */
    private static final Cell.Dir[] DirArr = {
            Cell.Dir.AP,    // 0
            Cell.Dir.BP,    // 1
            Cell.Dir.CN,    // 2
            Cell.Dir.AN,    // 3
            Cell.Dir.BN,    // 4
            Cell.Dir.CP,    // 5
    };
    private static final int[] DirNumberArr = new int[Cell.Dir.values().length];
    static {
        DirNumberArr[Cell.Dir.AP.ordinal()] = 0;
        DirNumberArr[Cell.Dir.BP.ordinal()] = 1;
        DirNumberArr[Cell.Dir.CN.ordinal()] = 2;
        DirNumberArr[Cell.Dir.AN.ordinal()] = 3;
        DirNumberArr[Cell.Dir.BN.ordinal()] = 4;
        DirNumberArr[Cell.Dir.CP.ordinal()] = 5;
    }

    public static Cell.Dir calcAxisByDirNumber(final int dirNumber) {
        final int retDirNumber;
        if (dirNumber >= 0) {
            retDirNumber = dirNumber % DirArr.length;
        } else {
            retDirNumber = ((DirArr.length) + dirNumber) % DirArr.length;
        }
        return DirArr[retDirNumber];
    }

    public static int calcDirNumberByAxis(final Cell.Dir dir) {
        return DirNumberArr[dir.ordinal()];
    }


    private static Cell.Dir calcNextRotDir(final Cell.Dir rotDir) {
        return calcAxisByDirNumber(calcDirNumberByAxis(rotDir) + 1);
    }

    public static Cell.Dir calcDirByRotOffset(final Cell.Dir rotDir, final int rotOffset) {
        return calcAxisByDirNumber(calcDirNumberByAxis(rotDir) + rotOffset);
    }
}
