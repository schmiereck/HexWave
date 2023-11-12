package de.schmiereck.hexWave2.service.hexGrid;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of all GridNodes in the area ordered by their distance.
 */
public class GridNodeArea {

    private final GridNode parentGridNode;

    private final Cell.Dir dir;

    /**
     * Array that contains all base Grid-Nodes in the Area.
     *
     * 0  1  2  3 ... (Position of Distance in Area)
     * 1  2  3  4 ...
     * 1  3  5  7 ... (Count of Neighbours in this distance)
     * Pos 0 is the Node itself, Pos 1 the next three neighbour Nodes, ...
     *
     * gridNodeArr[areaDistancePos][gridNodePos]
     */
    private final GridNode[][] gridNodeArr;

    /**
     * A List of all Parts in this Area.
     */
    private final List<Part> partList = new ArrayList<>();
    private final int areaDistance;

    public GridNodeArea(final GridNode parentGridNode, final Cell.Dir dir, final int areaDistance) {
        this.parentGridNode = parentGridNode;
        this.dir = dir;
        this.areaDistance = areaDistance;

        // 0:2, 1:3, 2:4, 3:5...
        final int gridNodeSize = calcGridNodeSizeForAreaDistance(areaDistance);
        this.gridNodeArr = new GridNode[gridNodeSize][];

        for (int areaDistancePos = 0; areaDistancePos < gridNodeSize; areaDistancePos++) {
            this.gridNodeArr[areaDistancePos] = new GridNode[calcGridNodeSizeForAreaDistancePos(areaDistancePos)];
        }
    }

    /**
     * @return 0:1, 1:2, 2:3, 3:4, ...
     */
    public static int calcGridNodeArrSizeForMaxAreaDistance(final int maxAreaDistance) {
        return maxAreaDistance + 1;
    }

    /**
     * @return 0:2, 1:3, 2:4, 3:5, ...
     */
    public static int calcGridNodeSizeForAreaDistance(final int areaDistance) {
        return areaDistance + 2;
    }

    /**
     * @return 0:1, 1:3, 2:5, 3:7, ...
     */
    public static int calcGridNodeSizeForAreaDistancePos(final int areaDistancePos) {
        return ((areaDistancePos + 1) * 2) - 1;
    }

    public GridNode getGridNode(final int areaDistancePos, final int gridNodePos) {
        final GridNode[] gridNodeArr = this.gridNodeArr[areaDistancePos];
        return gridNodeArr[gridNodePos];
    }

    public void setGridNodeAreaArr(final int areaDistancePos, final int gridNodePos, final GridNode gridNode) {
        this.gridNodeArr[areaDistancePos][gridNodePos] = gridNode;
    }

    public GridNode getParentGridNode() {
        return this.parentGridNode;
    }

    public List<Part> getPartList() {
        return this.partList;
    }

    public void addPart(final Part part) {
        this.partList.add(part);
    }

    public void removePart(final Part part) {
        this.partList.remove(part);
    }

    public Cell.Dir getDir() {
        return this.dir;
    }

    public int getAreaDistance() {
        return this.areaDistance;
    }

    @FunctionalInterface
    public interface ForEarchGridNodeInterface {
        void forEarch(final GridNode gridNode);
    }

    public void forEarchGridNode(final ForEarchGridNodeInterface forEarchGridNode) {
        //final int startAreaDistancePos = 0;
        final int startAreaDistancePos = this.gridNodeArr.length - 1;

        for (int areaDistancePos = startAreaDistancePos; areaDistancePos < this.gridNodeArr.length; areaDistancePos++) {
            final GridNode[] areaDistanceGridNodeArr = this.gridNodeArr[areaDistancePos];

            for (int gridNodePos = 0; gridNodePos < areaDistanceGridNodeArr.length; gridNodePos++) {
                final GridNode gridNode = areaDistanceGridNodeArr[gridNodePos];
                forEarchGridNode.forEarch(gridNode);
            }
        }
    }
}
