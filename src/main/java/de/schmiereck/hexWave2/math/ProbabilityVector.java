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
     * <li>{@link Integer#MIN_VALUE}: Every time.</li>
     * <li>1 or -1: 50%</li>
     * <li>{@link Integer#MAX_VALUE}: As rare as possible.</li>
     */
    public int[] limitArr =  new int[Cell.Dir.values().length];

    /**
     * Counter.
     * Increases every Step and reset if reached {@link #limitArr}.
     */
    public int[] cntArr =  new int[Cell.Dir.values().length];
    public int[] dirLastExtraPotentialProbabilityArr = new int[Cell.Dir.values().length];

    /**
     * Temporarily Sum of all reached limits in this step.
     * @see ProbabilityService#calcNext(ProbabilityVector)
     * @see ProbabilityService#calcProbabilityByLimit(int, int)
     */
    public int stepLimitSum;
    public int limitSum;
    public int limitCnt;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ProbabilityVector that = (ProbabilityVector) o;
        return //this.probability == that.probability &&
                //this.stepLimitSum == that.stepLimitSum &&
                Arrays.equals(this.limitArr, that.limitArr)// &&
                //Arrays.equals(this.cntArr, that.cntArr)
                //Arrays.equals(this.probabilityDirArr, that.probabilityDirArr)
                ;
    }

    @Override
    public int hashCode() {
        int result;// = Objects.hash(this.probability);//, this.stepLimitSum);
        result = //31 * result +
                Arrays.hashCode(this.limitArr);
        //result = 31 * result + Arrays.hashCode(this.cntArr);
        //result = 31 * result + Arrays.hashCode(this.probabilityDirArr);
        return result;
    }
}
