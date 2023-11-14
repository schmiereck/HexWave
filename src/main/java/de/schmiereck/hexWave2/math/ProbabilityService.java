package de.schmiereck.hexWave2.math;

import de.schmiereck.hexWave2.service.hexGrid.Cell;

/**
 * time   probability-Limit
 *     %  0123456789             times per 10 steps
 *    10  x--------- x---------  x
 *    12  x--------x --------    xx
 *    14  x-------x- ------      xx
 *    16  x------x-- ----        xx
 *    18  x-----x--- --          xx
 *    20  x----x----             xx
 *    30  x---x---x- --          xxx
 *    40  x--x--x--x --          xxxx
 *    50  x-x-x-x-x-             xxxxx
 *    50  -x-x-x-x-x             xxxxx
 *    60  -xx-xx-xx- xx          xxxxxx
 *    70  -xxx-xxx-x xx          xxxxxxx
 *    80  -xxxx-xxxx             xxxxxxxx
 *    82  -xxxxx-xxx xx          xxxxxxxx
 *    84  -xxxxxx-xx xxxx        xxxxxxxx
 *    86  -xxxxxxx-x xxxxxx      xxxxxxxx
 *    88  -xxxxxxxx- xxxxxxxx    xxxxxxxx
 *    90  -xxxxxxxxx -xxxxxxxxx  xxxxxxxxx
 */
public class ProbabilityService {
    public static void calcNext(final ProbabilityVector probabilityVector) {
        //probabilityVector.apCnt = calcCnt(probabilityVector.apCnt, probabilityVector.apLimit);
        //probabilityVector.anCnt = calcCnt(probabilityVector.anCnt, probabilityVector.anLimit);

        //probabilityVector.bpCnt = calcCnt(probabilityVector.bpCnt, probabilityVector.bpLimit);
        //probabilityVector.bnCnt = calcCnt(probabilityVector.bnCnt, probabilityVector.bnLimit);

        //probabilityVector.cpCnt = calcCnt(probabilityVector.cpCnt, probabilityVector.cpLimit);
        //probabilityVector.cnCnt = calcCnt(probabilityVector.cnCnt, probabilityVector.cnLimit);

        probabilityVector.stepLimitSum = 0;

        for (int dirPos = 0; dirPos < Cell.Dir.values().length; dirPos++) {
            final Cell.Dir dir = Cell.Dir.values()[dirPos];
            final int limit = probabilityVector.limitArr[dirPos];
            probabilityVector.cntArr[dirPos] = calcCnt(probabilityVector.cntArr[dirPos], limit);
            if (checkDir(probabilityVector, dir)) {
                probabilityVector.stepLimitSum += limit;
            }
        }
    }

    private static int calcCnt(final int cnt, final int limit) {
        final int nextCnt = cnt + 1;
        final int retCnt;
        if (limit > 0) {
            if (nextCnt > limit) {
                retCnt = 0;
            } else {
                retCnt = nextCnt;
            }
        } else {
            if (limit < 0) {
                if (nextCnt > 0) {
                    retCnt = limit;
                } else {
                    retCnt = nextCnt;
                }
            } else {
                retCnt = 0;
            }
        }
        return retCnt;
    }

    public static boolean checkDir(final ProbabilityVector probabilityVector, final Cell.Dir dir) {
        if (calcLimit(probabilityVector, dir) >= 0) {
            //return switch (dir) {
            //    case AP -> probabilityVector.apCnt == 0;
            //    case AN -> probabilityVector.anCnt == 0;
            //
            //    case BP -> probabilityVector.bpCnt == 0;
            //    case BN -> probabilityVector.bnCnt == 0;
            //
            //    case CP -> probabilityVector.cpCnt == 0;
            //    case CN -> probabilityVector.cnCnt == 0;
            //};
            return probabilityVector.cntArr[dir.ordinal()] == 0;
        } else {
            //return switch (dir) {
            //    case AP -> probabilityVector.apCnt < 0;
            //    case AN -> probabilityVector.anCnt < 0;
            //
            //    case BP -> probabilityVector.bpCnt < 0;
            //    case BN -> probabilityVector.bnCnt < 0;
            //
            //    case CP -> probabilityVector.cpCnt < 0;
            //    case CN -> probabilityVector.cnCnt < 0;
            //};
            return probabilityVector.cntArr[dir.ordinal()] < 0;
        }
    }

    public static int calcLimit(final ProbabilityVector probabilityVector, final Cell.Dir dir) {
        //return switch (dir) {
        //    case AP -> probabilityVector.apLimit;
        //    case AN -> probabilityVector.anLimit;
        //
        //    case BP -> probabilityVector.bpLimit;
        //    case BN -> probabilityVector.bnLimit;
        //
        //    case CP -> probabilityVector.cpLimit;
        //    case CN -> probabilityVector.cnLimit;
        //};
        return probabilityVector.limitArr[dir.ordinal()];
    }

    public static void setProbabilityLimit(final ProbabilityVector probabilityVector, final Cell.Dir dir, final int limit) {
        final int cnt;
        if (limit >= 0) {
            cnt = 0;
        } else {
            cnt = 0;//limit;
        }
        //switch (dir) {
        //    case AP -> { probabilityVector.apLimit = limit; probabilityVector.apCnt = cnt; }
        //    case AN -> { probabilityVector.anLimit = limit; probabilityVector.anCnt = cnt; }
        //
        //    case BP -> { probabilityVector.bpLimit = limit; probabilityVector.bpCnt = cnt; }
        //    case BN -> { probabilityVector.bnLimit = limit; probabilityVector.bnCnt = cnt; }
        //
        //    case CP -> { probabilityVector.cpLimit = limit; probabilityVector.cpCnt = cnt; }
        //    case CN -> { probabilityVector.cnLimit = limit; probabilityVector.cnCnt = cnt; }
        //}
        probabilityVector.limitArr[dir.ordinal()] = limit;
        probabilityVector.cntArr[dir.ordinal()] = cnt;
    }

    public static ProbabilityVector createVector(final ProbabilityVector probabilityVector) {
        final ProbabilityVector retProbabilityVector = new ProbabilityVector();

        //retProbabilityVector.apLimit = probabilityVector.apLimit;
        //retProbabilityVector.anLimit = probabilityVector.anLimit;
        //retProbabilityVector.bpLimit = probabilityVector.bpLimit;
        //retProbabilityVector.bnLimit = probabilityVector.bnLimit;
        //retProbabilityVector.cpLimit = probabilityVector.cpLimit;
        //retProbabilityVector.cnLimit = probabilityVector.cnLimit;
        //
        //retProbabilityVector.apCnt = probabilityVector.apCnt;
        //retProbabilityVector.anCnt = probabilityVector.anCnt;
        //retProbabilityVector.bpCnt = probabilityVector.bpCnt;
        //retProbabilityVector.bnCnt = probabilityVector.bnCnt;
        //retProbabilityVector.cpCnt = probabilityVector.cpCnt;
        //retProbabilityVector.cnCnt = probabilityVector.cnCnt;

        for (int dirPos = 0; dirPos < Cell.Dir.values().length; dirPos++) {
            retProbabilityVector.limitArr[dirPos] = probabilityVector.limitArr[dirPos];
            retProbabilityVector.cntArr[dirPos] = probabilityVector.cntArr[dirPos];
        }

        return retProbabilityVector;
    }

    final static int MaxPercent = 100;
    final static int MaxProp = 32;

    public static ProbabilityVector createVector(int apPerc, int bpPerc, int cpPerc, int anPerc, int bnPerc, int cnPerc) {
        final ProbabilityVector retProbabilityVector = new ProbabilityVector();

        ProbabilityService.setProbabilityLimit(retProbabilityVector, Cell.Dir.AP, calcProbByPercent(MaxPercent, apPerc, MaxProp));
        ProbabilityService.setProbabilityLimit(retProbabilityVector, Cell.Dir.AN, calcProbByPercent(MaxPercent, anPerc, MaxProp));

        ProbabilityService.setProbabilityLimit(retProbabilityVector, Cell.Dir.BP, calcProbByPercent(MaxPercent, bpPerc, MaxProp));
        ProbabilityService.setProbabilityLimit(retProbabilityVector, Cell.Dir.BN, calcProbByPercent(MaxPercent, bnPerc, MaxProp));

        ProbabilityService.setProbabilityLimit(retProbabilityVector, Cell.Dir.CP, calcProbByPercent(MaxPercent, cpPerc, MaxProp));
        ProbabilityService.setProbabilityLimit(retProbabilityVector, Cell.Dir.CN, calcProbByPercent(MaxPercent, cnPerc, MaxProp));

        return retProbabilityVector;
    }

    public static int calcProbByPercent(int maxPercent, int percent, int maxProp) {
        return (((maxPercent - (percent * 2)) * maxProp) / maxPercent);
    }
}
