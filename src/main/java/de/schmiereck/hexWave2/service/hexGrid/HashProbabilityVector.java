package de.schmiereck.hexWave2.service.hexGrid;

import de.schmiereck.hexWave2.math.ProbabilityVector;

import java.util.Arrays;

public class HashProbabilityVector {
    public int[] limitArr =  new int[Cell.Dir.values().length];

    /**
     * Counter.
     * Increases every Step and reset if reached {@link #limitArr}.
     */
    public int[] cntArr =  new int[Cell.Dir.values().length];
    public int[] dirLastExtraPotentialProbabilityArr = new int[Cell.Dir.values().length];

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ProbabilityVector that = (ProbabilityVector) o;
        return Arrays.equals(this.limitArr, that.limitArr) &&
                Arrays.equals(this.cntArr, that.cntArr) &&
                Arrays.equals(this.dirLastExtraPotentialProbabilityArr, that.dirLastExtraPotentialProbabilityArr)
                ;
    }

    @Override
    public int hashCode() {
        int result;// = Objects.hash(this.probability);//, this.stepLimitSum);
        result = //31 * result +
                Arrays.hashCode(this.limitArr);
        result = 31 * result + Arrays.hashCode(this.cntArr);
        result = 31 * result + Arrays.hashCode(this.dirLastExtraPotentialProbabilityArr);
        return result;
    }
}
