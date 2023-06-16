package de.schmiereck.hexWave.service.hexGrid;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    public enum Dir {
        //!np NP,
        AP, // right
        BP, // right-down
        CP, // right-top
        AN, // left
        BN, // left-top
        CN  // left-down
    }
    //public static Dir[] NDir = {
    //        AP, BP, CP, AN, BN, CN
    //};

    private final List<Part> partList = new ArrayList<>();

    public List<Part> getPartList() {
        return this.partList;
    }

    public void addPart(final Part part) {
        this.partList.add(part);
    }

    public void removePart(final Part part) {
        this.partList.remove(part);
    }
}
