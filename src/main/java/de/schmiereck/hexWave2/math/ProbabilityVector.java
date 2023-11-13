package de.schmiereck.hexWave2.math;

/**
 * 0
 * x
 *
 * 0 1
 * x -
 *
 * 0 1 2
 * x - -
 *
 * -1 0
 *  x -
 *  -2 1 0
 *   x x -
 *
 * -6 5 4 3 2 1 0
 *  x x x x x x -
 *
 *  0 1 2 3 4 5 6
 *  x - - - - - -
 */
public class ProbabilityVector {
    /**
     * Limit.
     * 0: Every time.
     * {@link Integer#MAX_VALUE}: As rare as possible.
     * -x: Every time but not if 0.
     */
    public int apLimit, anLimit, bpLimit, bnLimit, cpLimit, cnLimit;

    /**
     * Counter.
     */
    public int apCnt, anCnt, bpCnt, bnCnt, cpCnt, cnCnt;
}
