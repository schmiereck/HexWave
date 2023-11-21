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

    final static int MaxPercent = 100;
    final static int MaxProb = 32;

    public static void initProbabilityLimit(final ProbabilityVector probabilityVector, final int maxProb) {
        for (int dirPos = 0; dirPos < Cell.Dir.values().length; dirPos++) {
            probabilityVector.limitArr[dirPos] = maxProb;
        }
    }

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
                //probabilityVector.stepLimitSum += Math.abs(limit);
                //probabilityVector.stepLimitSum += calcAbsLimitValue2(limit);
                probabilityVector.stepLimitSum += calcProbabilityByLimit(MaxProb, limit);
            }
        }
    }

    public final static int calcProbabilityByLimit(final int maxProb, final int limit) {
        final int retValue;

        if (limit > 0) {
            retValue = ((maxProb) / (limit + 1));
        } else {
            if (limit < 0) {
                retValue = ((maxProb) + ((maxProb) / (limit + -1)));
            } else {
                throw new RuntimeException("Unexpected 0 limit for probability.");
            }
        }
        return retValue;
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

    public static ProbabilityVector createVector(final ProbabilityVector probabilityVector) { //, final int transferProbability)
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

        //retProbabilityVector.setProbability(transferProbability);

        return retProbabilityVector;
    }

    public static ProbabilityVector createVector(final int apPerc, final int bpPerc, final int cpPerc, final int anPerc, final int bnPerc, final int cnPerc) { // , int probability
        final ProbabilityVector retProbabilityVector = new ProbabilityVector();

        ProbabilityService.setProbabilityLimit(retProbabilityVector, Cell.Dir.AP, calcProbByPercent(MaxPercent, MaxProb, apPerc));
        ProbabilityService.setProbabilityLimit(retProbabilityVector, Cell.Dir.AN, calcProbByPercent(MaxPercent, MaxProb, anPerc));

        ProbabilityService.setProbabilityLimit(retProbabilityVector, Cell.Dir.BP, calcProbByPercent(MaxPercent, MaxProb, bpPerc));
        ProbabilityService.setProbabilityLimit(retProbabilityVector, Cell.Dir.BN, calcProbByPercent(MaxPercent, MaxProb, bnPerc));

        ProbabilityService.setProbabilityLimit(retProbabilityVector, Cell.Dir.CP, calcProbByPercent(MaxPercent, MaxProb, cpPerc));
        ProbabilityService.setProbabilityLimit(retProbabilityVector, Cell.Dir.CN, calcProbByPercent(MaxPercent, MaxProb, cnPerc));

        //retProbabilityVector.setProbability(probability);

        return retProbabilityVector;
    }

    public static boolean compare(final ProbabilityVector aProbabilityVector, final ProbabilityVector bProbabilityVector) {
        boolean equal = true;
        //boolean equal = aProbabilityVector.getProbability() == bProbabilityVector.getProbability();
        //if (equal) {
            for (int dirPos = 0; dirPos < Cell.Dir.values().length; dirPos++) {
                if ((aProbabilityVector.limitArr[dirPos] != bProbabilityVector.limitArr[dirPos]) ||
                        (aProbabilityVector.cntArr[dirPos] != bProbabilityVector.cntArr[dirPos])) {
                    equal = false;
                    break;
                }
            }
        //}
        return equal;
    }

    public static int calcProbByPercent(final int maxPercent, final int maxProp, final int percent) {
        final int retProb;
        final int prob = (((maxPercent - (percent * 2)) * maxProp) / maxPercent);
        if (prob == 0) {
            retProb = 1;
        } else {
            retProb = prob;
        }
        return retProb;
    }

    public static int calcAbsLimitValue(final ProbabilityVector probabilityVector, final Cell.Dir dir) {
        return Math.abs(calcLimit(probabilityVector, dir));
    }

    public static int calcAbsLimitValue2(final ProbabilityVector probabilityVector, final Cell.Dir dir) {
        final int limit = calcLimit(probabilityVector, dir);
        //return calcAbsLimitValue2(limit);
        return calcProbabilityByLimit(MaxProb, limit);
    }

    private static int calcAbsLimitValue2(final int limit) {
        final int cnt;
        if (limit >= 0) {
            cnt = 1;
        } else {
            cnt = -1;
        }
        final int limit2 = (limit - MaxProb) / 2;
        return Math.abs(limit2);
    }

    public static void calcMoveVector(final ProbabilityVector probabilityVector, final int aDiff, final int bDiff, final int cDiff) {
        //probabilityVector.limitArr[Cell.Dir.AP.ordinal()] += aDiff;
        //probabilityVector.limitArr[Cell.Dir.AN.ordinal()] -= aDiff;
        calcMoveLimitByDiff(probabilityVector, Cell.Dir.AP, aDiff);
        calcMoveLimitByDiff(probabilityVector, Cell.Dir.AN, -aDiff);
        calcMoveLimitByDiff(probabilityVector, Cell.Dir.BP, bDiff);
        calcMoveLimitByDiff(probabilityVector, Cell.Dir.BN, -bDiff);
        calcMoveLimitByDiff(probabilityVector, Cell.Dir.CP, cDiff);
        calcMoveLimitByDiff(probabilityVector, Cell.Dir.CN, -cDiff);
    }

    private static void calcMoveLimitByDiff(final ProbabilityVector probabilityVector, final Cell.Dir dir, final int diff) {
        final int limit = probabilityVector.limitArr[dir.ordinal()];
        if (limit > 0) {
            final int newLimit = limit - diff;
            if (newLimit < 0) {
                probabilityVector.limitArr[dir.ordinal()] = (newLimit - 1);
            } else {
                if (newLimit > 0) {
                    probabilityVector.limitArr[dir.ordinal()] = newLimit;
                } else {
                    probabilityVector.limitArr[dir.ordinal()] = -1;
                }
            }
        } else {
            if (limit < 0) {
                final int newLimit = limit + diff;
                if (newLimit > 0) {
                    probabilityVector.limitArr[dir.ordinal()] = (newLimit + 1);
                } else {
                    if (newLimit < 0) {
                        probabilityVector.limitArr[dir.ordinal()] = newLimit;
                    } else {
                        probabilityVector.limitArr[dir.ordinal()] = 1;
                    }
                }
            }
        }
    }

    public static void combineCntArr(final ProbabilityVector aProbabilityVector, final ProbabilityVector bProbabilityVector) {
        for (int dirPos = 0; dirPos < Cell.Dir.values().length; dirPos++) {
            //aProbabilityVector.cntArr[dirPos] = (aProbabilityVector.cntArr[dirPos] + bProbabilityVector.cntArr[dirPos]) / 2;
            //aProbabilityVector.cntArr[dirPos] = (aProbabilityVector.cntArr[dirPos] + bProbabilityVector.cntArr[dirPos]);
            //aProbabilityVector.cntArr[dirPos] = Math.max(aProbabilityVector.cntArr[dirPos], bProbabilityVector.cntArr[dirPos]);
            aProbabilityVector.cntArr[dirPos] = Math.min(aProbabilityVector.cntArr[dirPos], bProbabilityVector.cntArr[dirPos]);
        }
    }
}
