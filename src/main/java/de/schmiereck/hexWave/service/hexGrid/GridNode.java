package de.schmiereck.hexWave.service.hexGrid;

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

    /**
     * Is the Grid-Node-Area for this Node in the given Direction and the given Distance.
     * Used to manage the Fields this Node sends in the Direction(s).
     *
     * The areaDistance is equal to the distance the field reaches in this direction (@see {@link #MAX_AREA_DISTANCE}).
     *
     * gridNodeAreaArr[Cell.Dir][areaDistance]
     */
    private final GridNodeArea[][] gridNodeAreaArr;

    /**
     * A List of all GridNode-Areas (of other Grid-Nodes (@see {@link #gridNodeAreaArr})) that includes this Grid-Node.
     */
    private final List<GridNodeAreaRef> gridNodeAreaRefList = new ArrayList<>();

    public GridNode(final int posX, final int posY, final int maxAreaDistance) {
        this.posX = posX;
        this.posY = posY;
        this.cellArr = new Cell[2];
        this.cellArr[0] = new Cell();
        this.cellArr[1] = new Cell();

        this.gridNodeAreaArr = new GridNodeArea[Cell.Dir.values().length][];
        for (final Cell.Dir dir : Cell.Dir.values()) {
            final GridNodeArea[] gridNodeAreaArr = new GridNodeArea[maxAreaDistance];
            this.gridNodeAreaArr[dir.ordinal()] = gridNodeAreaArr;

            for (int areaDistance = 0; areaDistance < maxAreaDistance; areaDistance++) {
                gridNodeAreaArr[areaDistance] = new GridNodeArea(this, dir, areaDistance);
            }
        }
    }

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public List<Part> getPartList(final int cellArrPos) {
        return this.cellArr[cellArrPos].getPartList();
    }

    public void addPart(final int cellArrPos, final Part part) {
        this.cellArr[cellArrPos].addPart(part);

        this.gridNodeAreaRefList.stream().forEach(gridNodeAreaRef -> {
            gridNodeAreaRef.getGridNodeArea().addPart(part);
        });
    }

    public void removePart(final int cellArrPos, final Part part) {
        this.cellArr[cellArrPos].removePart(part);

        this.gridNodeAreaRefList.stream().forEach(gridNodeAreaRef -> {
            gridNodeAreaRef.getGridNodeArea().removePart(part);
        });
    }

    public GridNodeArea getGridNodeArea(final Cell.Dir dir, final int areaDistance) {
        return this.gridNodeAreaArr[dir.ordinal()][areaDistance];
    }

    public void setGridNodeAreaArr(final Cell.Dir dir, final int areaDistance, final int areaDistancePos, final int gridNodePos, final GridNode gridNode, final int maxAreaDistance) {
        final GridNodeArea gridNodeArea = this.gridNodeAreaArr[dir.ordinal()][areaDistance];
        gridNodeArea.setGridNodeAreaArr(areaDistancePos, gridNodePos, gridNode);
        final int areaNodeCount = GridNodeArea.calcGridNodeSizeForAreaDistance(areaDistance);

        final double areaDistanceValue = ((maxAreaDistance - areaDistance) / (double)maxAreaDistance);

        //final double distanceValue = (areaDistancePos / (double)MAX_AREA_DISTANCE);
        final double distanceValue = (areaDistancePos / (double)areaNodeCount);

        //final double posValue = (MAX_AREA_DISTANCE / ((gridNodePos / 2.0D) + 1.0D)) / MAX_AREA_DISTANCE;
        //final double posValue = ((MAX_AREA_DISTANCE - (gridNodePos / 2))) / (double)MAX_AREA_DISTANCE;
        final double posValue = ((areaNodeCount - (gridNodePos / 2))) / (double)areaNodeCount;

        //final double value = 1.0D;
        //final double value = distanceValue * posValue;
        //final double value = areaDistanceValue;
        final double value = areaDistanceValue * distanceValue * posValue;

        gridNode.addGridNodeAreaRef(new GridNodeAreaRef(gridNodeArea, value));
    }

    public List<GridNodeAreaRef> getGridNodeAreaRefList() {
        return this.gridNodeAreaRefList;
    }

    private void addGridNodeAreaRef(final GridNodeAreaRef gridNodeAreaRef) {
        this.gridNodeAreaRefList.add(gridNodeAreaRef);
    }
}
