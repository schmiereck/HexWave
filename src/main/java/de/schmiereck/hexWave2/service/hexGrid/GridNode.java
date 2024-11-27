package de.schmiereck.hexWave2.service.hexGrid;

import de.schmiereck.hexWave2.MainConfig3;

import java.util.ArrayList;
import java.util.List;

/**
 * GridNodeArea:
 * Every GridNode has for every direction a list of Areas with more and more neighbour GridNodes.
 */
public class GridNode {

    private final int posX;
    private final int posY;

    private final Cell[] cellArr;

    public GridNode(final int posX, final int posY, final int maxAreaDistance) {
        this.posX = posX;
        this.posY = posY;
        this.cellArr = new Cell[2];
        this.cellArr[0] = new Cell();
        this.cellArr[1] = new Cell();
    }

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    List<Part> getPartList(final int cellArrPos) {
        return this.cellArr[cellArrPos].getPartList();
    }

    void addPart(final int cellArrPos, final Part part) {
        this.cellArr[cellArrPos].addPart(part);
    }

    public void addPartList(final int cellArrPos, final List<Part> partList) {
        partList.stream().forEach(part -> this.addPart(cellArrPos, part));
    }

    void removePart(final int cellArrPos, final Part part) {
        this.cellArr[cellArrPos].removePart(part);
    }

}
