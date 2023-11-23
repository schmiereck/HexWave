package de.schmiereck.hexWave2.math;

import static de.schmiereck.hexWave2.MainConfig3.MaxImpulseProb;
import static de.schmiereck.hexWave2.math.ProbabilityService.calcProbabilityByLimit;

import de.schmiereck.hexWave2.service.hexGrid.Cell;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProbabilityService_WHEN_ {

    @Test
    public void GIVEN__THEN_() {
        final ProbabilityVector probabilityVector = new ProbabilityVector();
        ProbabilityService.initProbabilityLimit(probabilityVector, MaxImpulseProb);

        ProbabilityService.setProbabilityLimit(probabilityVector, Cell.Dir.AP, 1);
        ProbabilityService.calcNext(probabilityVector, MaxImpulseProb);
        Assertions.assertEquals(0, probabilityVector.stepLimitSum);

        ProbabilityService.calcNext(probabilityVector, MaxImpulseProb);
        Assertions.assertEquals(16, probabilityVector.stepLimitSum);

        ProbabilityService.calcNext(probabilityVector, MaxImpulseProb);
        Assertions.assertEquals(0, probabilityVector.stepLimitSum);

        ProbabilityService.calcNext(probabilityVector, MaxImpulseProb);
        Assertions.assertEquals(16, probabilityVector.stepLimitSum);
    }

    @Test
    public void GIVEN_limit_THEN_calc_probability() {
        final int max = 12;

        Assertions.assertEquals (6, calcProbabilityByLimit(max,  1)); // x.x.x.x.x.x.

        Assertions.assertEquals( 0, calcProbabilityByLimit(max,  12*12)); // x...........
        Assertions.assertEquals( 0, calcProbabilityByLimit(max,  24)); // x...........
        Assertions.assertEquals( 0, calcProbabilityByLimit(max,  16)); // x...........

        Assertions.assertEquals( 0, calcProbabilityByLimit(max,  12)); // x...........
        Assertions.assertEquals( 1, calcProbabilityByLimit(max,  11)); // x...........
        Assertions.assertEquals( 1, calcProbabilityByLimit(max,  10)); // x...........
        Assertions.assertEquals( 1, calcProbabilityByLimit(max,  9)); // x...........
        Assertions.assertEquals( 1, calcProbabilityByLimit(max,  8)); // x...........
        Assertions.assertEquals( 1, calcProbabilityByLimit(max,  7)); // x.......x...
        Assertions.assertEquals( 1, calcProbabilityByLimit(max,  6)); // x......x....
        Assertions.assertEquals( 2, calcProbabilityByLimit(max,  5)); // x.....x.....
        Assertions.assertEquals( 2, calcProbabilityByLimit(max,  4)); // x....x....x.
        Assertions.assertEquals( 3, calcProbabilityByLimit(max,  3)); // x...x...x...
        Assertions.assertEquals( 4, calcProbabilityByLimit(max,  2)); // x..x..x..x..
        Assertions.assertEquals (6, calcProbabilityByLimit(max,  1)); // x.x.x.x.x.x.
        Assertions.assertEquals( 6, calcProbabilityByLimit(max, -1)); // .x.x.x.x.x.x
        Assertions.assertEquals( 8, calcProbabilityByLimit(max, -2)); // .xx.xx.xx.xx
        Assertions.assertEquals( 9, calcProbabilityByLimit(max, -3)); // .xxx.xxx.xxx
        Assertions.assertEquals(10, calcProbabilityByLimit(max, -4)); // .xxxx.xxxx.x
        Assertions.assertEquals(10, calcProbabilityByLimit(max, -5)); // .xxxxx.xxxxx
        Assertions.assertEquals(11, calcProbabilityByLimit(max, -6)); // .xxxxxx.xxxx
        Assertions.assertEquals(11, calcProbabilityByLimit(max, -7)); // .xxxxxxx.xxx
        Assertions.assertEquals(11, calcProbabilityByLimit(max, -8)); // .xxxxxxxx.xx
        Assertions.assertEquals(11, calcProbabilityByLimit(max, -9)); // .xxxxxxxx.xx
        Assertions.assertEquals(11, calcProbabilityByLimit(max,-10)); // .xxxxxxxxx.x
        Assertions.assertEquals(11, calcProbabilityByLimit(max,-11)); // .xxxxxxxxxx.
        Assertions.assertEquals(12, calcProbabilityByLimit(max,-12)); // .xxxxxxxxxxx

        Assertions.assertEquals(12, calcProbabilityByLimit(max, -16)); // .xxxxxxxxxxx
        Assertions.assertEquals(12, calcProbabilityByLimit(max, -24)); // .xxxxxxxxxxx
        Assertions.assertEquals(12, calcProbabilityByLimit(max, -12*12)); // .xxxxxxxxxxx
    }

    @Test
    public void GIVEN_MAX_limit_THEN_calc_probability() {
        final int max = Integer.MAX_VALUE;

        Assertions.assertEquals(  13_338_407, calcProbabilityByLimit(max,  160)); // x...........

        Assertions.assertEquals( 126_322_567, calcProbabilityByLimit(max,  16)); // x...........

        Assertions.assertEquals( 165_191_049, calcProbabilityByLimit(max,  12)); // x...........
        Assertions.assertEquals( 178_956_970, calcProbabilityByLimit(max,  11)); // x...........
        Assertions.assertEquals( 195_225_786, calcProbabilityByLimit(max,  10)); // x...........
        Assertions.assertEquals( 214_748_364, calcProbabilityByLimit(max,  9)); // x...........
        //Assertions.assertEquals( 1, calcProbabilityByLimit(max,  8)); // x...........
        //Assertions.assertEquals( 1, calcProbabilityByLimit(max,  7)); // x.......x...
        //Assertions.assertEquals( 1, calcProbabilityByLimit(max,  6)); // x......x....
        //Assertions.assertEquals( 2, calcProbabilityByLimit(max,  5)); // x.....x.....
        //Assertions.assertEquals( 2, calcProbabilityByLimit(max,  4)); // x....x....x.
        //Assertions.assertEquals( 3, calcProbabilityByLimit(max,  3)); // x...x...x...
        Assertions.assertEquals(   715_827_882, calcProbabilityByLimit(max,  2)); // x..x..x..x..
        Assertions.assertEquals( 1_073_741_823, calcProbabilityByLimit(max,  1)); // x.x.x.x.x.x.
        Assertions.assertEquals( 1_073_741_824, calcProbabilityByLimit(max, -1)); // .x.x.x.x.x.x
        Assertions.assertEquals( 1_431_655_765, calcProbabilityByLimit(max, -2)); // .xx.xx.xx.xx
        //Assertions.assertEquals( 9, calcProbabilityByLimit(max, -3)); // .xxx.xxx.xxx
        //Assertions.assertEquals(10, calcProbabilityByLimit(max, -4)); // .xxxx.xxxx.x
        //Assertions.assertEquals(10, calcProbabilityByLimit(max, -5)); // .xxxxx.xxxxx
        //Assertions.assertEquals(11, calcProbabilityByLimit(max, -6)); // .xxxxxx.xxxx
        //Assertions.assertEquals(11, calcProbabilityByLimit(max, -7)); // .xxxxxxx.xxx
        //Assertions.assertEquals(11, calcProbabilityByLimit(max, -8)); // .xxxxxxxx.xx
        //Assertions.assertEquals(11, calcProbabilityByLimit(max, -9)); // .xxxxxxxx.xx
        Assertions.assertEquals( 1_952_257_861, calcProbabilityByLimit(max,-10)); // .xxxxxxxxx.x
        //Assertions.assertEquals(11, calcProbabilityByLimit(max,-11)); // .xxxxxxxxxx.
        Assertions.assertEquals( 1_982_292_598, calcProbabilityByLimit(max,-12)); // .xxxxxxxxxxx

        Assertions.assertEquals( 2_021_161_080, calcProbabilityByLimit(max, -16)); // .xxxxxxxxxxx

        Assertions.assertEquals( 2_134_145_240, calcProbabilityByLimit(max, -160)); // .xxxxxxxxxxx
    }
}
