package de.schmiereck.hexWave.service.hexGrid;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class Test_HexGridService_WHEN_calcGridNodeAreaRefValue_is_called {

    @Test
    void calcGridNodeAreaRefValue() {
        //final int areaDistance, final int areaDistancePos, final int gridNodePos, final int maxAreaDistance
        assertEquals(4.0D, HexGridService.calcGridNodeAreaRefValue(1, 1, 0, 5), 0.01D);
        assertEquals(1.8D, HexGridService.calcGridNodeAreaRefValue(2, 2, 0, 5), 0.01D);
        assertEquals(1.06D, HexGridService.calcGridNodeAreaRefValue(3, 3, 0, 5), 0.01D);
        assertEquals(0.625D, HexGridService.calcGridNodeAreaRefValue(4, 4, 0, 5), 0.01D);
        assertEquals(0.288D, HexGridService.calcGridNodeAreaRefValue(5, 5, 0, 5), 0.01D);
    }
}