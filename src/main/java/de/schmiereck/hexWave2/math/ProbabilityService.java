package de.schmiereck.hexWave2.math;

import de.schmiereck.hexWave2.service.hexGrid.Cell;

public class ProbabilityService {
    public static void calcNext(final ProbabilityVector probabilityVector) {
        probabilityVector.apCnt = calcCnt(probabilityVector.apCnt, probabilityVector.apLimit);
        probabilityVector.anCnt = calcCnt(probabilityVector.anCnt, probabilityVector.anLimit);

        probabilityVector.bpCnt = calcCnt(probabilityVector.bpCnt, probabilityVector.bpLimit);
        probabilityVector.bnCnt = calcCnt(probabilityVector.bnCnt, probabilityVector.bnLimit);

        probabilityVector.cpCnt = calcCnt(probabilityVector.cpCnt, probabilityVector.cpLimit);
        probabilityVector.cnCnt = calcCnt(probabilityVector.cnCnt, probabilityVector.cnLimit);
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

    public static boolean checkDir(final ProbabilityVector probabilityVector, final Cell.Dir sourceDir) {
        return switch (sourceDir) {
            case AP -> probabilityVector.apCnt == 0;
            case AN -> probabilityVector.anCnt == 0;

            case BP -> probabilityVector.bpCnt == 0;
            case BN -> probabilityVector.bnCnt == 0;

            case CP -> probabilityVector.cpCnt == 0;
            case CN -> probabilityVector.cnCnt == 0;
        };
    }
}
