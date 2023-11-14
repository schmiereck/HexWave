package de.schmiereck.hexWave2.math;

import de.schmiereck.hexWave2.service.hexGrid.Cell;

import java.util.Arrays;
import java.util.Objects;

/**
 * 0 (100%, always)
 * x
 *
 * 0 1 (50%)
 * x -
 *
 * -1 0 (50%)
 *  x -
 *
 * 0 1 2
 * x - -
 *
 *  -2 1 0
 *   x x -
 *
 * -6 5 4 3 2 1 0
 *  x x x x x x -
 *
 *  0 1 2 3 4 5 6
 *  x - - - - - -
 *
 *  There is no coding for "never" - in the world of probabilities "never" does not exist...
 */
public class ProbabilityVector {
    /**
     * Limit.
     * 0: Every time.
     * {@link Integer#MAX_VALUE}: As rare as possible.
     * -x: Every time but not if 0.
     */
    //public int apLimit, anLimit, bpLimit, bnLimit, cpLimit, cnLimit;
    public int[] limitArr =  new int[Cell.Dir.values().length];

    /**
     * Counter.
     */
    //public int apCnt, anCnt, bpCnt, bnCnt, cpCnt, cnCnt;
    public int[] cntArr =  new int[Cell.Dir.values().length];
    private int[] probabilityDirArr = new int[Cell.Dir.values().length];
    public int stepLimitSum;

    public void setDirProbability(final Cell.Dir dir, final int probability) {
        this.probabilityDirArr[dir.ordinal()] = probability;
    }

    public int getDirProbability(final Cell.Dir dir) {
        return this.probabilityDirArr[dir.ordinal()];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProbabilityVector that = (ProbabilityVector) o;
        return //this.probability == that.probability &&
                //this.stepLimitSum == that.stepLimitSum &&
                Arrays.equals(this.limitArr, that.limitArr) &&
                Arrays.equals(this.cntArr, that.cntArr);
                //Arrays.equals(this.probabilityDirArr, that.probabilityDirArr);
    }

    @Override
    public int hashCode() {
        int result;// = Objects.hash(this.probability);//, this.stepLimitSum);
        result = //31 * result +
                Arrays.hashCode(this.limitArr);
        result = 31 * result + Arrays.hashCode(this.cntArr);
        //result = 31 * result + Arrays.hashCode(this.probabilityDirArr);
        return result;
    }
}
