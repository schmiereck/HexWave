package de.schmiereck.hexWave.utils;

import static de.schmiereck.hexWave.utils.DirUtils.calcAxisByDirNumber;
import static de.schmiereck.hexWave.utils.DirUtils.calcDirNumberByAxis;
import static org.junit.jupiter.api.Assertions.*;

import de.schmiereck.hexWave.service.hexGrid.Cell;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;

import java.util.ArrayList;
import java.util.List;

public class DirUtilsTest {

    @org.junit.jupiter.api.Test
    public void test1() {
        final List<Integer> initList = List.of(new Integer[] {Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)});
        final List<Integer> list = new ArrayList<>();
        final List<Integer> addList = new ArrayList<>();

        list.addAll(initList);

        list.stream().forEach(value -> {
            if (value.intValue() % 2 == 1) {
                addList.add(Integer.valueOf(value.intValue() * 2));
            }
        });
        list.addAll(addList);
    }
    @org.junit.jupiter.api.Test
    public void test2() {
        final int startDirNumber = calcDirNumberByAxis(Cell.Dir.AP);

        assertEquals(Cell.Dir.BN, calcAxisByDirNumber(startDirNumber + -2));
        assertEquals(Cell.Dir.CP, calcAxisByDirNumber(startDirNumber + -1));
        assertEquals(Cell.Dir.AP, calcAxisByDirNumber(startDirNumber + 0));
        assertEquals(Cell.Dir.BP, calcAxisByDirNumber(startDirNumber + 1));
        assertEquals(Cell.Dir.CN, calcAxisByDirNumber(startDirNumber + 2));
    }
}
