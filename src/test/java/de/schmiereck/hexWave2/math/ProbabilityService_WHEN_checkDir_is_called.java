package de.schmiereck.hexWave2.math;

import static de.schmiereck.hexWave2.MainConfig3.MaxImpulseProb;

import de.schmiereck.hexWave2.MainConfig3;
import de.schmiereck.hexWave2.service.hexGrid.Cell;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProbabilityService_WHEN_checkDir_is_called {

    @Test
    public void GIVEN_limit_MAX_THEN_check_is_allways_true() {
        final int MaxLimit = 32;
        final ProbabilityVector probabilityVector = new ProbabilityVector();
        ProbabilityService.initProbabilityLimit(probabilityVector, MaxLimit);
        ProbabilityService.setProbabilityLimit(probabilityVector, Cell.Dir.AP, -MaxLimit);
        ProbabilityService.setProbabilityLimit(probabilityVector, Cell.Dir.AN, MaxLimit);

        for (int pos = 0; pos < 32*2; pos++) {
            if (pos % 33 == 0) {
                Assertions.assertFalse(ProbabilityService.checkDir(probabilityVector, Cell.Dir.AP), "pos:%d".formatted(pos));
            } else {
                Assertions.assertTrue(ProbabilityService.checkDir(probabilityVector, Cell.Dir.AP), "pos:%d".formatted(pos));
            }
            if (pos % 33 == 0) {
                Assertions.assertTrue(ProbabilityService.checkDir(probabilityVector, Cell.Dir.AN), "pos:%d".formatted(pos));
            } else {
                Assertions.assertFalse(ProbabilityService.checkDir(probabilityVector, Cell.Dir.AN), "pos:%d".formatted(pos));
            }
            ProbabilityService.calcNext(probabilityVector, MaxImpulseProb);
        }
    }

    @Test
    public void GIVEN_limit_1_THEN_check_50_percent_true() {
        final int MaxLimit = 32;
        final ProbabilityVector probabilityVector = new ProbabilityVector();
        ProbabilityService.initProbabilityLimit(probabilityVector, MaxLimit);
        ProbabilityService.setProbabilityLimit(probabilityVector, Cell.Dir.AP, 1);
        ProbabilityService.setProbabilityLimit(probabilityVector, Cell.Dir.AN, -1);

        final boolean[] expectedApArr = {
                true, false, true, false, true, false, true, false,
                true, false, true, false, true, false, true, false,

                true, false, true, false, true, false, true, false,
                true, false, true, false, true, false, true, false,
        };
        final boolean[] expectedAnArr = {
                false, true, false, true, false, true, false, true,
                false, true, false, true, false, true, false, true,

                false, true, false, true, false, true, false, true,
                false, true, false, true, false, true, false, true,
        };

        for (int pos = 0; pos < 32; pos++) {
            Assertions.assertEquals(expectedApArr[pos], ProbabilityService.checkDir(probabilityVector, Cell.Dir.AP), "pos:%d".formatted(pos));
            Assertions.assertEquals(expectedAnArr[pos], ProbabilityService.checkDir(probabilityVector, Cell.Dir.AN), "pos:%d".formatted(pos));
            ProbabilityService.calcNext(probabilityVector, MaxImpulseProb);
        }
    }

    @Test
    public void GIVEN_limit_7_THEN_check_90_percent_true() {
        final int MaxLimit = 32;
        final ProbabilityVector probabilityVector = new ProbabilityVector();
        ProbabilityService.initProbabilityLimit(probabilityVector, MaxLimit);
        ProbabilityService.setProbabilityLimit(probabilityVector, Cell.Dir.AP, 7);
        ProbabilityService.setProbabilityLimit(probabilityVector, Cell.Dir.AN, -7);

        final boolean[] expectedApArr = {
                true, false, false, false, false, false, false, false,
                true, false, false, false, false, false, false, false,

                true, false, false, false, false, false, false, false,
                true, false, false, false, false, false, false, false,
        };
        final boolean[] expectedAnArr = {
                false, true, true, true, true, true, true, true,
                false, true, true, true, true, true, true, true,

                false, true, true, true, true, true, true, true,
                false, true, true, true, true, true, true, true,
        };

        for (int pos = 0; pos < 32; pos++) {
            Assertions.assertEquals(expectedApArr[pos], ProbabilityService.checkDir(probabilityVector, Cell.Dir.AP), "pos:%d".formatted(pos));
            Assertions.assertEquals(expectedAnArr[pos], ProbabilityService.checkDir(probabilityVector, Cell.Dir.AN), "pos:%d".formatted(pos));
            ProbabilityService.calcNext(probabilityVector, MaxImpulseProb);
        }
    }

    @Test
    public void GIVEN_limit_THEN_check_n_percent_true() {
        final int MaxPercent = 100;
        final int MaxProb = 32;
        final int ExpectedSize = MaxProb * MaxProb;

        final Boolean[] expectedApArr = new Boolean[ExpectedSize];
        //final Boolean[] expectedAnArr = new Boolean[ExpectedSize];

        for (int percent = 0; percent <= MaxPercent; percent++) {
        //int percent = 99; {
            final int limit = ProbabilityService.calcLimitByPercent(MaxPercent, MaxProb, percent);

            final ProbabilityVector probabilityVector = new ProbabilityVector();
            ProbabilityService.initProbabilityLimit(probabilityVector, MaxProb);
            ProbabilityService.setProbabilityLimit(probabilityVector, Cell.Dir.AP, limit);
            //ProbabilityService.setProbabilityLimit(probabilityVector, Cell.Dir.AN, -prop);

            for (int pos = 0; pos < ExpectedSize; pos++) {
                if (limit >= 0) {
                    expectedApArr[pos] = (pos % (limit + 1)) == 0;
                } else {
                    expectedApArr[pos] = (pos % (limit - 1)) != 0;
                }
                //expectedAnArr[pos] = !expectedApArr[pos];
            }

            final long apCount = Stream.of(expectedApArr).filter(b -> b).count();
            //final long anCount = Stream.of(expectedAnArr).filter(b -> b).count();

            final int probabilityValue = ProbabilityService.calcProbabilityValue(probabilityVector, Cell.Dir.AP, MainConfig3.MaxImpulseProb);

            System.out.printf("AP: percent:%3d (%3.1f%%): limit:%3d: probability:%3d: %s\n",
                    percent, (apCount * 100.0D / ExpectedSize), limit, probabilityValue, Arrays.toString(Arrays.copyOf(expectedApArr, MaxProb)));
            //System.out.printf("AN: percent:%3d (%3.1f%%): prop:%3d: %s\n", percent, (anCount * 100.0D / ExpectedSize), prop, Arrays.toString(expectedAnArr));
            for (int pos = 0; pos < ExpectedSize; pos++) {
                Assertions.assertEquals(expectedApArr[pos], ProbabilityService.checkDir(probabilityVector, Cell.Dir.AP), "AP: percent:%d, pos:%d".formatted(percent, pos));
                //Assertions.assertEquals(expectedAnArr[pos], ProbabilityService.checkDir(probabilityVector, Cell.Dir.AN), "AN: percent:%d, pos:%d".formatted(percent, pos));
                ProbabilityService.calcNext(probabilityVector, MainConfig3.MaxImpulseProb);
            }
        }

    }
}
